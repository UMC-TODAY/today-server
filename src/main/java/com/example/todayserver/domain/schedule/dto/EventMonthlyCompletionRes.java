package com.example.todayserver.domain.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record EventMonthlyCompletionRes(

        @Schema(description = "조회 연도", example = "2026")
        int year,

        @Schema(description = "조회 월", example = "1")
        int month,

        @Schema(description = "이번 달 전체 일정(EVENT) 수", example = "10")
        long totalCount,

        @Schema(description = "완료한 일정(EVENT) 수", example = "1")
        long completedCount
) {
    public static EventMonthlyCompletionRes of(
            int year,
            int month,
            long totalCount,
            long completedCount
    ) {
        return new EventMonthlyCompletionRes(year, month, totalCount, completedCount);
    }
}
