package com.example.todayserver.domain.schedule.connect.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record IcloudIntergrationsReq(
        @Schema(
                description = "icloud 캘린더 공유 링크(ICS)",
                example = "webcal://p149-caldav.icloud.com/published..."
        )
        String icsUrl
) {
}