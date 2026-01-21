package com.example.todayserver.domain.schedule.dto;

import com.example.todayserver.domain.schedule.enums.Mode;
import com.example.todayserver.domain.schedule.enums.RepeatType;
import com.example.todayserver.domain.schedule.enums.ScheduleType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

public record ScheduleDetailRes(
        @Schema(description = "일정 ID", example = "1")
        Long id,

        @Schema(description = "일정 유형.\n- TODO: 할 일\n- EVENT: 일정", example = "TODO")
        ScheduleType scheduleType,

        @Schema(description = "작업 모드(CUSTOM / ANYTIME)", example = "CUSTOM")
        Mode mode,

        @Schema(description = "일정 또는 할 일의 제목", example = "유튜브 영상 업로드 하기")
        String title,

        @Schema(description = "일정 날짜", example = "2026-01-10")
        LocalDate date,

        @Schema(description = "소요 시간(분 단위)", example = "60")
        Integer duration,

        @Schema(description = "하루 종일 여부", example = "true")
        boolean isAllDay,

        @Schema(description = "반복 주기 (NONE / DAILY / WEEKLY / MONTHLY / YEARLY)", example = "NONE")
        RepeatType repeatType,

        @Schema(description = "일정/할 일에 대한 메모 내용", example = "편집 끝나는 대로 업로드하기")
        String memo,

        @Schema(description = "이모지", example = "📎")
        String emoji,

        @Schema(description = "배경 색상 HEX 코드", example = "#A7C7FF")
        String bgColor,

        @Schema(description = "일정 시작 시각 (yyyy-MM-dd HH:mm 형식)", example = "2026-01-01 10:00")
        String startAt,

        @Schema(description = "일정 종료 시각 (yyyy-MM-dd HH:mm 형식)", example = "2026-01-02 12:00")
        String endAt,

        @Schema(description = "하위 작업 리스트")
        List<SubScheduleDetailRes> subSchedules
) {
    public record SubScheduleDetailRes(
            @Schema(description = "하위 작업 ID", example = "10")
            Long subScheduleId,

            @Schema(description = "하위 작업 제목", example = "썸네일 만들기")
            String title,

            @Schema(description = "하위 작업 색상", example = "#F0000")
            String subColor,

            @Schema(description = "하위 작업 이모지", example = "📎")
            String subEmoji
    ) {}
}
