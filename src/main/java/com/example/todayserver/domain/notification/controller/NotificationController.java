package com.example.todayserver.domain.notification.controller;

import com.example.todayserver.domain.member.repository.MemberRepository;
import com.example.todayserver.domain.member.service.MemberServiceImpl;
import com.example.todayserver.domain.notification.dto.NotificationResponseDTO;
import com.example.todayserver.domain.notification.service.NotificationCommandService;
import com.example.todayserver.domain.notification.service.NotificationQueryService;
import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.global.common.jwt.JwtUtil;
import com.example.todayserver.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification", description = "알림 관련 API")
public class NotificationController {

    private final NotificationQueryService notificationQueryService;
    private final NotificationCommandService notificationCommandService;
    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository; // 이메일로 멤버를 찾기

    // 알림 목록 조회
    @GetMapping
    @Operation(summary = "내 알림 목록 조회 API")
    public ApiResponse<List<NotificationResponseDTO>> getNotifications(
            @RequestHeader("Authorization") String token) {

        Member member = getMemberFromToken(token);
        List<NotificationResponseDTO> notifications = notificationQueryService.getNotifications(member);
        return ApiResponse.success(notifications);
    }

    // 친구 요청 수락
    @PostMapping("/{notificationId}/accept")
    @Operation(summary = "친구 요청 수락 API")
    public ApiResponse<Void> acceptFriendRequest(
            @RequestHeader("Authorization") String token,
            @PathVariable(name = "notificationId") Long notificationId) {

        Member member = getMemberFromToken(token);
        notificationCommandService.acceptFriendRequest(member, notificationId);
        return ApiResponse.success(null);
    }

    // 친구 요청 거절
    @PostMapping("/{notificationId}/reject")
    @Operation(summary = "친구 요청 거절 API")
    public ApiResponse<Void> rejectFriendRequest(
            @RequestHeader("Authorization") String token,
            @PathVariable(name = "notificationId") Long notificationId) {

        Member member = getMemberFromToken(token);
        notificationCommandService.rejectFriendRequest(member, notificationId);
        return ApiResponse.success(null);
    }

    // JwtUtil의 getEmail을 사용하는 메서드
    private Member getMemberFromToken(String token) {
        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;

        // 이메일 추출
        String email = jwtUtil.getEmail(actualToken);

        // 이메일로 멤버 조회
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }
}