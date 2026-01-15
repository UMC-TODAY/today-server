package com.example.todayserver.domain.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SubScheduleCreateReq(
        @NotBlank(message = "하위 작업 타이틀은 필수입니다.")
        String subTitle,

        @NotBlank(message = "subColor는 필수입니다.")
        @Pattern(
                regexp = "^#[0-9A-Fa-f]{6}$",
                message = "subColor는 7자리 HEX 값 형식이어야 합니다."
        )
        String subColor,
        @Schema(description = "이모지", example = "📎")
                String subEmoji
) {}