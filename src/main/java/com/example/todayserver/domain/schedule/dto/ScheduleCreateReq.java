package com.example.todayserver.domain.schedule.dto;

import com.example.todayserver.domain.schedule.enums.Mode;
import com.example.todayserver.domain.schedule.enums.RepeatType;
import com.example.todayserver.domain.schedule.enums.ScheduleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.List;

public record ScheduleCreateReq(

        @Schema(description = "일정 유형.\n- TODO: 할 일\n- EVENT: 일정", example = "TODO")
        @NotNull(message = "scheduleType은 필수 값입니다.")
        ScheduleType scheduleType,

        @Schema(description = "작업 모드(CUSTOM / ANYTIME)", example = "CUSTOM")
        @NotNull(message = "mode는 필수 값입니다.")
        Mode mode,

        @Schema(description = "일정 또는 할 일의 제목", example = "유튜브 영상 업로드 하기")
        @NotBlank(message = "title은 필수 값입니다.")
        String title,

        @Schema(description = "일정 날짜", example = "2026-01-10")
        LocalDate date,

        @Schema(description = "소요 시간(분 단위)", example = "60")
        Integer duration,

        @Schema(description = "반복 주기 (NONE / DAILY / WEEKLY / MONTHLY / YEARLY)", example = "NONE")
        RepeatType repeatType,

        @Schema(description = "일정/할 일에 대한 메모 내용", example = "편집 끝나는 대로 업로드하기")
        String memo,

        @Schema(description = "이모지", example = "📎")
        String emoji,

        @Schema(description = "배경 색상 HEX 코드", example = "#A7C7FF")
        @NotBlank(message = "bgColor는 필수 값입니다.")
        @Pattern(
                regexp = "^#[0-9A-Fa-f]{6}$",
                message = "bgColor는 7자리 HEX 값 형식이어야 합니다."
        )
        String bgColor,

        @Schema(description = "일정 시작 시각 (yyyy-MM-dd HH:mm 형식)", example = "2026-01-01 10:00")
        String startAt,

        @Schema(description = "일정 종료 시각 (yyyy-MM-dd HH:mm 형식)", example = "2026-01-02 12:00")
        String endAt,

        @Schema(description = "하위 작업 리스트")
        @Valid
        List<SubScheduleCreateReq> subSchedules
) {
}
