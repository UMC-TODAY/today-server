package com.example.todayserver.domain.schedule.connect.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

public class CsvScheduleImportDto {

    // CSV 한 행 (Raw)
    public record Row(
            String title,
            String startDatetime,
            String endDatetime,
            String description
    ) {}

    // 검증 + 정책 적용 완료
    public record Normalized(
            String title,
            String memo,
            LocalDateTime startedAt,
            LocalDateTime endedAt,
            boolean isAllDay
    ) {}

    // 행 단위 실패 정보
    public record Failure(
            int row,
            String field,
            String reason
    ) {}

    // 업로드된 일정(성공 건) 요약
    public record Imported(
            Long scheduleId,
            String title,
            LocalDateTime startedAt,
            LocalDateTime endedAt,
            boolean isAllDay,
            String color,
            String memo
    ) {}

    // 파서 결과(부분 성공을 위해 성공/실패를 함께 전달)
    public record ParseResult(
            int totalRows,
            List<Normalized> normalized,
            List<Failure> failures
    ) {}

    // CSV 업로드 결과
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Result(
            String message,
            int totalRows,
            int successCount,
            Integer failCount,
            List<Failure> failures,
            List<Imported> imported
    ) {}
}
