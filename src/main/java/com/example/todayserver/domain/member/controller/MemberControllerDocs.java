package com.example.todayserver.domain.member.controller;

import com.example.todayserver.domain.member.dto.MemberReqDto;
import com.example.todayserver.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

public interface MemberControllerDocs {
    @Operation(
            summary = "닉네임 중복확인",
            description = "해당 닉네임이 사용중인지 확인합니다."
    )
    ApiResponse<Void> checkNickname(@Valid @RequestBody MemberReqDto.Nickname dto);
}
