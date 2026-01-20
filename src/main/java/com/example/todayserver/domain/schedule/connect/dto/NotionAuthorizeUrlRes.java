package com.example.todayserver.domain.schedule.connect.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Notion 캘린더 연동 인가 URL 응답")
public record NotionAuthorizeUrlRes(
        @Schema(
                description = "프론트에서 이동할 Notion OAuth 인가 URL",
                example = "https://accounts.google.com/o/oauth2/v2/auth?client_id=..."
        )
                String authorizeUrl
) {
}