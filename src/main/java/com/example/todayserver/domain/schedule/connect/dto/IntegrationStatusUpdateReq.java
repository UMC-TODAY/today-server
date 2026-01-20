package com.example.todayserver.domain.schedule.connect.dto;

import com.example.todayserver.domain.schedule.connect.enums.ExternalProvider;
import io.swagger.v3.oas.annotations.media.Schema;

public record IntegrationStatusUpdateReq(
        @Schema(description = "외부 제공자", example = "GOOGLE")
        ExternalProvider provider,

        @Schema(description = "연동 여부 (false면 해제 처리)", example = "false")
        boolean connected
) {
}
