package com.example.todayserver.domain.schedule.connect.controller;

import com.example.todayserver.domain.schedule.connect.dto.GoogleAuthorizeUrlRes;
import com.example.todayserver.domain.schedule.connect.service.google.GoogleConnectService;
import com.example.todayserver.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Preferences", description = "설정 관련 API")
@RestController
@RequestMapping("/api/v1/preferences/integrations")
@RequiredArgsConstructor
public class ExternalIntegrationController {

    private final GoogleConnectService googleConnectService;

    @Operation(
            summary = "Google 캘린더 연동 인가 URL 발급",
            description = "현재 로그인한 사용자의 Google 캘린더 연동을 위한 OAuth 인가 URL을 생성합니다."
    )
    @PostMapping("/google/authorize")
    public ApiResponse<GoogleAuthorizeUrlRes> authorizeGoogle(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "id") Long memberId
    ) {
        GoogleAuthorizeUrlRes res =
                googleConnectService.buildGoogleAuthorizeUrl(memberId);
        return ApiResponse.success(res);
    }

    @Operation(
            summary = "Google 캘린더 연동 콜백 처리",
            description = "Google OAuth 콜백을 받아 Authorization Code를 토큰으로 교환하고, ExternalAccount에 저장합니다."
    )
    @GetMapping("/google/callback")
    public ApiResponse<Void> callbackGoogle(
            @RequestParam("code")
            @Parameter(description = "Google OAuth Authorization Code", example = "4/0AfJohX...")
            String code,

            @AuthenticationPrincipal(expression = "id") Long memberId
    ) {
        googleConnectService.handleGoogleCallback(code, memberId);
        return ApiResponse.success(null);
    }
}
