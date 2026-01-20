package com.example.todayserver.domain.schedule.connect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record GoogleTokenRes(
        @Schema(description = "액세스 토큰", example = "ya29.a0AfB_byCDeFg...")
        @JsonProperty("access_token")
        String accessToken,

        @Schema(description = "액세스 토큰 만료까지 남은 시간(초)", example = "3599")
        @JsonProperty("expires_in")
        Long expiresIn,

        @Schema(description = "리프레시 토큰 (처음 동의 시에만 내려올 수 있음)",
                example = "1//0gAbCdEfGhIjKlMnOpQrStUvWxYz...")
        @JsonProperty("refresh_token")
        String refreshToken,

        @Schema(description = "부여된 권한(scope) 목록",
                example = "https://www.googleapis.com/auth/calendar.readonly")
        @JsonProperty("scope")
        String scope,

        @Schema(description = "토큰 타입", example = "Bearer")
        @JsonProperty("token_type")
        String tokenType,

        @Schema(description = "ID 토큰 (필요 시 구글 사용자 정보 파싱에 사용)",
                example = "eyJhbGciOiJSUzI1NiIsImtpZCI6Ij...")
        @JsonProperty("id_token")
        String idToken
) {
}
