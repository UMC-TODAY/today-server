package com.example.todayserver.domain.member.controller;

import com.example.todayserver.domain.member.dto.PreferenceDto;
import com.example.todayserver.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestHeader;

public interface PreferenceControllerDocs {

    @Operation(
            summary = "알림 설정 보기",
            description = "알림 설정 정보를 조회합니다."
    )
    ApiResponse<PreferenceDto.Notification> getNotifications(@RequestHeader("Authorization") String token);

    @Operation(
            summary = "알림 설정 수정",
            description = "알림 설정 정보를 수정합니다."
    )
    ApiResponse<Void> updateNotifications(@RequestHeader("Authorization") String token, PreferenceDto.Notification dto);

    @Operation(
            summary = "개인정보 및 분석설정 보기",
            description = "개인정보 및 분석설정 정보를 조회합니다."
    )
    ApiResponse<PreferenceDto.Info> getInfo(@RequestHeader("Authorization") String token);
}
