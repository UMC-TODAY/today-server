package com.example.todayserver.domain.member.controller;

import com.example.todayserver.domain.member.dto.PreferenceDto;
import com.example.todayserver.domain.member.service.PreferenceService;
import com.example.todayserver.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/preferences/members")
@Tag(name = "Preference", description = "회원정보 관련 API")
public class PreferenceController implements PreferenceControllerDocs{

    private final PreferenceService preferenceService;

    @GetMapping("/notifications")
    public ApiResponse<PreferenceDto.Notification> getNotifications(@RequestHeader("Authorization") String token){
        return ApiResponse.success(preferenceService.getNotifications(token));
    }

    @PatchMapping("/notifications")
    public ApiResponse<Void> updateNotifications(@RequestHeader("Authorization") String token, PreferenceDto.Notification dto){
        preferenceService.updateNotifications(token, dto);
        return ApiResponse.success(null);
    }

    @GetMapping("/info")
    public ApiResponse<PreferenceDto.Info> getInfo(@RequestHeader("Authorization") String token){
        return ApiResponse.success(preferenceService.getInfo(token));
    }

    @PatchMapping("/info")
    public ApiResponse<Void> updateInfo(@RequestHeader("Authorization") String token, PreferenceDto.Info dto){
        preferenceService.updateInfo(token, dto);
        return ApiResponse.success(null);
    }
}
