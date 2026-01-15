package com.example.todayserver.domain.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "월별 일정 조회 요청")
public record EventMonthlySearchReq(

        @Schema(description = "조회 연도", example = "2026")
        @NotNull(message = "year는 필수 값입니다.")
        Integer year,

        @Schema(description = "조회 월(1~12)", example = "1")
        @NotNull(message = "month는 필수 값입니다.")
        @Min(value = 1, message = "month는 1부터 12 사이의 값이어야 합니다.")
        @Max(value = 12, message = "month는 1부터 12 사이의 값이어야 합니다.")
        Integer month,

        @Schema(description = "일정 출처 필터 (ALL, GOOGLE, NOTION, ICLOUD, CSV)", example = "ALL")
        @Pattern(
                regexp = "ALL|LOCAL|GOOGLE|NOTION|ICLOUD|CSV",
                message = "filter는 ALL, LOCAL, GOOGLE, NOTION, ICLOUD, CSV 중 하나여야 합니다."
        )
        String filter,

        @Schema(description = "지난 일정 숨기기 여부", example = "false")
        Boolean hidePast
) {
}
