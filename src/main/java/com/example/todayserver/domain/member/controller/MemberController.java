package com.example.todayserver.domain.member.controller;

import com.example.todayserver.domain.member.dto.MemberReqDto;
import com.example.todayserver.domain.member.service.MemberServiceImpl;
import com.example.todayserver.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "Member", description = "회원정보 관련 API")
public class MemberController implements MemberControllerDocs {

    private final MemberServiceImpl memberService;

    @PostMapping("/nickname/check")
    public ApiResponse<Void> checkNickname(@Valid @RequestBody MemberReqDto.Nickname dto){
        memberService.checkNicknameDuplicate(dto.getNickname());
        return ApiResponse.success(null);
    }

}
