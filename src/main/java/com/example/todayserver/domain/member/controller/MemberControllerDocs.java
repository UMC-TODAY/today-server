package com.example.todayserver.domain.member.controller;

import com.example.todayserver.domain.member.dto.*;
import com.example.todayserver.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface MemberControllerDocs {

    @Operation(
            summary = "이메일 중복확인",
            description = "해당 이메일로 가입된 계정이 있는지 확인합니다."
    )
    ApiResponse<Void> checkEmail(@Valid @RequestBody EmailReqDto.EmailCheck dto);

    @Operation(
            summary = "이메일 인증코드 발송",
            description = "해당 이메일로 인증코드 6자리를 발송합니다."
    )
    ApiResponse<Void> sendEmailVerification(@Valid @RequestBody EmailReqDto.EmailCheck dto);

    @Operation(
            summary = "이메일 인증코드 확인",
            description = "인증코드가 유효한지 확인합니다."
    )
    ApiResponse<Void> checkEmailVerification(@Valid @RequestBody EmailReqDto.EmailCode dto);

    @Operation(
            summary = "비밀번호 재설정 인증코드 발송",
            description = "해당 이메일로 인증코드 6자리를 발송합니다."
    )
    ApiResponse<Void> sendPasswordResetVerification(@Valid @RequestBody EmailReqDto.EmailCheck dto);

    @Operation(
            summary = "비밀번호 재설정 인증코드 확인",
            description = "인증코드가 유효한지 확인합니다."
    )
    ApiResponse<Void> checkPasswordResetVerification(@Valid @RequestBody EmailReqDto.EmailCode dto);

    @Operation(
            summary = "이메일 회원가입",
            description = "인증된 이메일로 회원가입을 진행합니다."
    )
    ApiResponse<Void> emailSignup(@Valid @RequestBody MemberReqDto.SignupDto dto);

    @Operation(
            summary = "이메일 로그인",
            description = "이메일 로그인을 잔행합니다."
    )
    ApiResponse<MemberResDto.LoginDto> emailLogin(@Valid @RequestBody MemberReqDto.LoginDto dto);

    @Operation(
            summary = "로그인 유지",
            description = "리프레시 토큰으로 새 엑세스 토큰을 발급합니다."
    )
    ApiResponse<TokenDto> reissue(@Valid @RequestBody TokenReissueDto dto);
}