package com.example.todayserver.domain.member.controller;

import com.example.todayserver.domain.member.dto.PreferenceResDto;
import com.example.todayserver.domain.member.service.PreferenceService;
import com.example.todayserver.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/preferences/members")
@Tag(name = "Preference", description = "회원정보 관련 API")
public class PreferenceController implements PreferenceControllerDocs{

    private final PreferenceService preferenceService;

    @GetMapping("/notifications")
    public ApiResponse<PreferenceResDto.Notification> getNotifications(@RequestHeader("Authorization") String token){
        return ApiResponse.success(preferenceService.getNotifications(token));
    }
}
