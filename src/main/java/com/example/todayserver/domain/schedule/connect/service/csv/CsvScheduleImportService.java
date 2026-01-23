package com.example.todayserver.domain.schedule.connect.service.csv;

import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.member.excpetion.code.MemberErrorCode;
import com.example.todayserver.domain.member.repository.MemberRepository;
import com.example.todayserver.domain.schedule.connect.converter.ScheduleConverter;
import com.example.todayserver.domain.schedule.connect.dto.CsvScheduleImportDto;
import com.example.todayserver.domain.schedule.entity.Schedule;
import com.example.todayserver.domain.schedule.repository.ScheduleRepository;
import com.example.todayserver.global.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CsvScheduleImportService {

    private final MemberRepository memberRepository;
    private final ScheduleRepository scheduleRepository;
    private final CsvScheduleParser csvScheduleParser;

    // CSV 업로드(부분 성공) 처리 후 결과 반환
    public CsvScheduleImportDto.Result importCsv(Long memberId, MultipartFile file) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.NOT_FOUND));

        CsvScheduleImportDto.ParseResult parsed = csvScheduleParser.parse(file);

        int total = parsed.totalRows();
        List<CsvScheduleImportDto.Failure> failures = new ArrayList<>(parsed.failures());
        List<CsvScheduleImportDto.Imported> imported = new ArrayList<>();

        int success = 0;

        // 파서에서 성공으로 분류된 Normalized만 저장
        for (CsvScheduleImportDto.Normalized n : parsed.normalized()) {
            Schedule saved = scheduleRepository.save(ScheduleConverter.fromCsv(n, member));
            success++;

            imported.add(new CsvScheduleImportDto.Imported(
                    saved.getId(),
                    saved.getTitle(),
                    saved.getStartedAt(),
                    saved.getEndedAt(),
                    saved.isAllDay(),
                    saved.getColor(),
                    saved.getMemo()
            ));
        }

        String message = buildMessage(total, success);

        return new CsvScheduleImportDto.Result(
                message,
                total,
                success,
                failures.isEmpty() ? null : failures.size(),
                failures.isEmpty() ? null : failures,
                imported
        );
    }

    // 결과에 따라 사용자 메시지 생성
    private String buildMessage(int total, int success) {
        if (success == total) {
            return "CSV 업로드가 완료되었습니다.";
        }
        if (success > 0) {
            return String.format("CSV 업로드가 완료되었습니다. 총 %d건 중 %d건만 등록되었습니다.", total, success);
        }
        return "CSV 업로드에 실패했습니다. 입력값을 확인해 주세요.";
    }
}
