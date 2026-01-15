package com.example.todayserver.domain.member.controller;

import com.example.todayserver.domain.member.dto.EmailReqDto;
import com.example.todayserver.domain.member.dto.MemberReqDto;
import com.example.todayserver.domain.member.dto.MemberResDto;
import com.example.todayserver.domain.member.service.EmailService;
import com.example.todayserver.domain.member.service.MemberService;
import com.example.todayserver.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Member", description = "회원가입,정보 관련 API")
public class MemberController implements MemberControllerDocs {

    private final MemberService memberService;
    private final EmailService emailService;

    @PostMapping("/members/email/check")
    public ApiResponse<Void> checkEmail(@Valid @RequestBody EmailReqDto.EmailCheck dto) {
        memberService.checkEmailDuplicate(dto.getEmail());
        return ApiResponse.success(null);
    }

    @PostMapping("/auth/email/verification-codes")
    public ApiResponse<Void> sendEmailVerification(@Valid @RequestBody EmailReqDto.EmailCheck dto) {
        emailService.sendVerificationEmail(dto.getEmail(), "email_code");
        return ApiResponse.success(null);
    }

    @PostMapping("/auth/email/verification-codes/verify")
    public ApiResponse<Void> checkEmailVerification(@Valid @RequestBody EmailReqDto.EmailCode dto){
        emailService.checkEmailVerifyCode(dto.getEmail(), dto.getCode());
        return ApiResponse.success(null);
    }

    @PostMapping("/members/password/verification-codes")
    public ApiResponse<Void> sendPasswordResetVerification(@Valid @RequestBody EmailReqDto.EmailCheck dto){
        emailService.sendVerificationEmail(dto.getEmail(), "password_code");
        return ApiResponse.success(null);
    }

    @PostMapping("/members/password/verification-codes/verify")
    public ApiResponse<Void> checkPasswordResetVerification(@Valid @RequestBody EmailReqDto.EmailCode dto){
        emailService.checkEmailVerifyCode(dto.getEmail(), dto.getCode());
        return ApiResponse.success(null);
    }

    @PostMapping("/auth/signup/email")
    public ApiResponse<Void> emailSignup(@Valid @RequestBody MemberReqDto.SignupDto dto){
        memberService.emailSignup(dto);
        return ApiResponse.success(null);
    }

    @PostMapping("/auth/login/email")
    public ApiResponse<MemberResDto.LoginDto> emailLogin(@Valid @RequestBody MemberReqDto.LoginDto dto){
        return ApiResponse.success(memberService.emailLogin(dto));
    }
}