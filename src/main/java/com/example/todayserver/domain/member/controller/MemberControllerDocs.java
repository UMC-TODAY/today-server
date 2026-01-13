package com.example.todayserver.domain.member.controller;

import com.example.todayserver.domain.member.dto.EmailReqDto;
import com.example.todayserver.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

public interface MemberControllerDocs {

    @Operation(
            summary = "이메일 중복확인",
            description = "해당 이메일로 가입된 계정이 있는지 확인합니다."
    )
    ApiResponse<Void> checkEmail(@Valid @RequestBody EmailReqDto.EmailCheck dto);
}