package com.example.todayserver.domain.friend.controller;

import com.example.todayserver.domain.friend.dto.FriendRequestDTO;
import com.example.todayserver.domain.friend.dto.FriendResponseDTO;
import com.example.todayserver.domain.friend.service.FriendCommandService;
import com.example.todayserver.domain.friend.service.FriendQueryService;
import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friends")
@Tag(name = "Friend", description = "친구 API")
public class FriendController {

    private final FriendCommandService friendCommandService;

    @Operation(summary = "친구 요청 및 취소", description = "상대방에게 친구 요청을 보내거나, 이미 보낸 요청을 취소합니다.")
    @PostMapping("/request")
    public ApiResponse<String> requestFriend(
            @AuthenticationPrincipal Member loginMember,
            @RequestBody FriendRequestDTO.RequestFriendDTO request) {
        String result = friendCommandService.requestOrCancelFriend(loginMember, request.getReceiverId());
        return ApiResponse.success(result);
    }

    private final FriendQueryService friendQueryService;

    @Operation(summary = "친구 목록 조회", description = "수락 완료된 친구 목록을 가져옵니다.")
    @GetMapping("")
    public ApiResponse<FriendResponseDTO.FriendListDTO> getFriendList(
            @AuthenticationPrincipal Member loginMember) {
        return ApiResponse.success(friendQueryService.getFriendList(loginMember));
    }

    @Operation(summary = "친구 삭제", description = "친구 목록에서 친구를 삭제(관계 끊기)합니다.")
    @DeleteMapping("/{friendRecordId}")
    public ApiResponse<String> deleteFriend(
            @AuthenticationPrincipal Member loginMember,
            @PathVariable Long friendRecordId) {
        friendCommandService.deleteFriend(loginMember, friendRecordId);
        return ApiResponse.success("친구 삭제가 완료되었습니다.");
    }

    @Operation(summary = "일정 공유 설정 토글", description = "친구에게 내 일정을 공유할지 여부를 설정합니다.")
    @PatchMapping("/{friendRecordId}/sharing")
    public ApiResponse<String> toggleSharing(
            @AuthenticationPrincipal Member loginMember,
            @PathVariable Long friendRecordId) {
        String result = friendCommandService.toggleCalendarSharing(loginMember, friendRecordId);
        return ApiResponse.success(result);
    }

    @Operation(summary = "친구 검색", description = "닉네임으로 내 친구 목록을 검색합니다.")
    @GetMapping("/search")
    public ApiResponse<FriendResponseDTO.FriendListDTO> searchFriends(
            @AuthenticationPrincipal Member loginMember,
            @RequestParam(name = "keyword") String keyword) {

        // QueryService의 검색 메서드 호출
        return ApiResponse.success(friendQueryService.searchFriends(loginMember, keyword));
    }
}