package com.example.todayserver.domain.schedule.connect.service.csv;

import com.example.todayserver.domain.schedule.connect.dto.CsvScheduleImportDto;
import com.example.todayserver.domain.schedule.connect.exception.CsvRowValidationException;
import com.example.todayserver.global.common.exception.CustomException;
import com.example.todayserver.global.common.exception.ErrorCode;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class CsvScheduleParser {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final String H_TITLE = "title";
    private static final String H_START = "start_datetime";
    private static final String H_END = "end_datetime";
    private static final String H_DESC = "description";

    // CSV를 파싱해 성공(Normalized)과 실패(Failure)를 함께 반환
    public CsvScheduleImportDto.ParseResult parse(MultipartFile file) {
        validateFile(file);
        validateUtf8Encoding(file);

        List<CsvScheduleImportDto.Normalized> normalizedList = new ArrayList<>();
        List<CsvScheduleImportDto.Failure> failures = new ArrayList<>();
        int total = 0;

        try (Reader reader = buildUtf8ReaderWithoutBom(file)) {

            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setIgnoreEmptyLines(true)
                    .setTrim(true)
                    .build();

            Iterable<CSVRecord> records = format.parse(reader);

            for (CSVRecord record : records) {
                total++;
                int rowNum = (int) record.getRecordNumber() + 1; // 헤더 포함 행 번호 기준

                try {
                    assertRequiredHeaders(record);

                    CsvScheduleImportDto.Row row = parseRow(record);
                    CsvScheduleImportDto.Normalized normalized = normalizeAndValidate(row);

                    normalizedList.add(normalized);

                } catch (CsvRowValidationException e) {
                    failures.add(new CsvScheduleImportDto.Failure(rowNum, e.getField(), e.getMessage()));
                } catch (CustomException e) {
                    // 파일 자체 문제(헤더/인코딩 등)는 즉시 중단
                    throw e;
                } catch (Exception e) {
                    failures.add(new CsvScheduleImportDto.Failure(rowNum, "unknown", "처리 중 오류가 발생했습니다."));
                }
            }

        } catch (IOException e) {
            throw new CustomException(ErrorCode.CSV_FILE_READ_FAILED);
        }

        return new CsvScheduleImportDto.ParseResult(total, normalizedList, failures);
    }

    // 파일 존재/확장자(.csv) 검증
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(ErrorCode.CSV_FILE_REQUIRED);
        }
        String name = Optional.ofNullable(file.getOriginalFilename()).orElse("");
        if (!name.toLowerCase().endsWith(".csv")) {
            throw new CustomException(ErrorCode.CSV_FILE_EXTENSION_INVALID);
        }
    }

    // UTF-8이 아닌 인코딩 업로드를 방지
    private void validateUtf8Encoding(MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            String decoded = new String(bytes, StandardCharsets.UTF_8);
            if (decoded.indexOf('\uFFFD') >= 0) {
                throw new CustomException(ErrorCode.CSV_ENCODING_INVALID);
            }
        } catch (IOException e) {
            throw new CustomException(ErrorCode.CSV_FILE_READ_FAILED);
        }
    }

    // UTF-8 BOM 제거 후 Reader 생성
    private Reader buildUtf8ReaderWithoutBom(MultipartFile file) throws IOException {
        PushbackInputStream pbis = new PushbackInputStream(file.getInputStream(), 3);
        byte[] bom = new byte[3];
        int read = pbis.read(bom, 0, 3);

        boolean hasBom =
                read == 3 &&
                        (bom[0] & 0xFF) == 0xEF &&
                        (bom[1] & 0xFF) == 0xBB &&
                        (bom[2] & 0xFF) == 0xBF;

        if (!hasBom && read > 0) {
            pbis.unread(bom, 0, read);
        }

        return new BufferedReader(new InputStreamReader(pbis, StandardCharsets.UTF_8));
    }

    // 필수 헤더 존재 여부 검증
    private void assertRequiredHeaders(CSVRecord record) {
        if (!record.isMapped(H_TITLE)
                || !record.isMapped(H_START)
                || !record.isMapped(H_END)
                || !record.isMapped(H_DESC)) {
            throw new CustomException(ErrorCode.CSV_HEADER_INVALID);
        }
    }

    // CSVRecord -> Row DTO 변환
    private CsvScheduleImportDto.Row parseRow(CSVRecord record) {
        return new CsvScheduleImportDto.Row(
                record.get(H_TITLE),
                record.get(H_START),
                record.get(H_END),
                record.isMapped(H_DESC) ? record.get(H_DESC) : null
        );
    }

    // Row 검증/정책 적용 후 Normalized DTO 변환
    private CsvScheduleImportDto.Normalized normalizeAndValidate(CsvScheduleImportDto.Row row) {
        if (row.title() == null || row.title().isBlank()) {
            throw new CsvRowValidationException("title", "필수값입니다.");
        }
        String title = row.title().trim();
        if (title.length() > 100) {
            throw new CsvRowValidationException("title", "제목은 최대 100자까지 가능합니다.");
        }

        if (row.startDatetime() == null || row.startDatetime().isBlank()) {
            throw new CsvRowValidationException("start_datetime", "필수값입니다.");
        }
        if (row.endDatetime() == null || row.endDatetime().isBlank()) {
            throw new CsvRowValidationException("end_datetime", "필수값입니다.");
        }

        boolean isAllDay = isDateOnly(row.startDatetime()) && isDateOnly(row.endDatetime());

        LocalDateTime startedAt = parseDateTime(row.startDatetime(), "start_datetime");
        LocalDateTime endedAt = parseDateTime(row.endDatetime(), "end_datetime");

        if (isAllDay) {
            endedAt = endedAt.plusDays(1);
        }

        if (!endedAt.isAfter(startedAt)) {
            throw new CsvRowValidationException("end_datetime", "종료 시각은 시작 시각 이후여야 합니다.");
        }

        String memo = row.description();
        if (memo != null && memo.length() > 500) {
            throw new CsvRowValidationException("description", "메모는 최대 500자까지 가능합니다.");
        }

        return new CsvScheduleImportDto.Normalized(title, memo, startedAt, endedAt, isAllDay);
    }

    // YYYY-MM-DD 형식(날짜만)인지 판별
    private boolean isDateOnly(String raw) {
        return raw != null && raw.trim().length() == 10 && !raw.contains(" ");
    }

    // YYYY-MM-DD 또는 YYYY-MM-DD HH:mm 파싱
    private LocalDateTime parseDateTime(String raw, String field) {
        try {
            String value = raw.trim();

            if (value.length() == 16 && value.contains(" ")) {
                return LocalDateTime.parse(value, DATETIME_FMT);
            }

            if (value.length() == 10) {
                return LocalDate.parse(value, DATE_FMT).atStartOfDay();
            }

            throw new IllegalArgumentException();

        } catch (Exception e) {
            throw new CsvRowValidationException(
                    field,
                    "날짜 형식은 YYYY-MM-DD 또는 YYYY-MM-DD HH:mm 형식이어야 합니다."
            );
        }
    }
}
