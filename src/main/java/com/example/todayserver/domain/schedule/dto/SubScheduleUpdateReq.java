package com.example.todayserver.domain.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

public record SubScheduleUpdateReq(

        @Schema(description = "하위 작업 ID(있으면 수정, 없으면 신규 추가)", example = "10")
        Long subScheduleId,

        @Schema(description = "하위 작업 타이틀(부분 수정)", example = "안건 정리")
        String subTitle,

        @Schema(description = "하위 작업 색상(부분 수정)", example = "#FF0000")
        @Pattern(
                regexp = "^#[0-9A-Fa-f]{6}$",
                message = "subColor는 7자리 HEX 값 형식이어야 합니다."
        )
        String subColor,

        @Schema(description = "하위 작업 이모지(부분 수정)", example = "📌")
        String subEmoji
) {}
