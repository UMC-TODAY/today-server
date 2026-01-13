package com.example.todayserver.domain.member.controller;

import com.example.todayserver.domain.member.dto.EmailReqDto;
import com.example.todayserver.domain.member.service.MemberService;
import com.example.todayserver.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Member", description = "회원가입,정보 관련 API")
public class MemberController implements MemberControllerDocs{

    private final MemberService memberService;

    @PostMapping("/members/email/check")
    public ApiResponse<Void> checkEmail(@Valid @RequestBody EmailReqDto.EmailCheck dto){
        memberService.checkEmailDuplicate(dto);
        return ApiResponse.success(null);
    }
}
