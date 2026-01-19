package com.example.todayserver.domain.member.controller;

import com.example.todayserver.domain.member.dto.MemberReqDto;
import com.example.todayserver.domain.member.dto.MemberResDto;
import com.example.todayserver.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

public interface MemberControllerDocs {
    @Operation(
            summary = "닉네임 중복확인",
            description = "해당 닉네임이 사용중인지 확인합니다."
    )
    ApiResponse<Void> checkNickname(@Valid @RequestBody MemberReqDto.Nickname dto);

    @Operation(
            summary = "사용자 정보 보기",
            description = "해당 id를 가진 사용자의 회원정보를 조회합니다."
    )
    ApiResponse<MemberResDto.MemberInfo> getMemberInfo(@PathVariable Long id);

    @Operation(
            summary = "내 정보 보기",
            description = "내 회원정보를 조회합니다."
    )
    ApiResponse<MemberResDto.MemberInfo> getMyInfo(@RequestHeader("Authorization") String token);

    @Operation(
            summary = "회원 탈퇴",
            description = "회원 탈퇴를 진행합니다."
    )
    ApiResponse<Void> withdraw(@RequestHeader("Authorization") String token);
}
