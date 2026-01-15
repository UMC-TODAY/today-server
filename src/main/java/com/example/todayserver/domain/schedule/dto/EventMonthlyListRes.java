package com.example.todayserver.domain.schedule.dto;

import com.example.todayserver.domain.schedule.enums.ScheduleSource;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record EventMonthlyListRes(

        @Schema(description = "조회에 사용된 필터", example = "ALL")
        String filter,

        @Schema(description = "월별 일정 목록")
        List<EventDto> events
) {

    @Schema(description = "월별 일정 개별 정보")
    public record EventDto(

            @Schema(description = "일정 ID", example = "1")
            Long id,

            @Schema(description = "일정 제목", example = "설거지하기")
            String title,

            @Schema(description = "색상 HEX 값", example = "#5A5D62")
            String color,

            @Schema(description = "이모지", example = "🍽️")
            String emoji,

            @Schema(description = "완료 여부", example = "false")
            boolean isDone,

            @Schema(description = "일정 날짜 (yyyy-MM-dd)", example = "2026-01-01")
            String date,

            @Schema(description = "시작 시간 (HH:mm)", example = "11:00")
            String startTime,

            @Schema(description = "종료 시간 (HH:mm)", example = "13:00")
            String endTime,

            @Schema(description = "일정 출처", example = "GOOGLE")
            ScheduleSource source
    ) {
    }
}
