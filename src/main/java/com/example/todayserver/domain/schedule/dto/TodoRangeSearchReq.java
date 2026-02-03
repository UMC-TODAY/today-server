package com.example.todayserver.domain.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record TodoRangeSearchReq(
        @Schema(description = "조회 범위(from)", example = "2026-01-01")
        @NotNull(message = "from은 필수 값입니다.")
        String from,

        @Schema(description = "조회 범위(to) ", example = "2026-01-07")
        @NotNull(message = "to는 필수 값입니다.")
        String to,

        @Schema(description = "할일 조회 필터", example = "ALL")
//        @Pattern(
//                regexp = "ALL|LOCAL|GOOGLE|NOTION|ICLOUD|CSV",
//                message = "filter는 ALL, LOCAL, GOOGLE, NOTION, ICLOUD, CSV 중 하나여야 합니다."
//        )
        String filter,

        @Schema(description = "지난 할일 숨기기 여부", example = "false")
        Boolean hidePast
) {}