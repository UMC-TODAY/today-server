package com.example.todayserver.domain.member.controller;

import com.example.todayserver.domain.member.dto.MemberReqDto;
import com.example.todayserver.domain.member.dto.MemberResDto;
import com.example.todayserver.domain.member.service.MemberServiceImpl;
import com.example.todayserver.global.common.jwt.JwtUtil;
import com.example.todayserver.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "Member", description = "회원정보 관련 API")
public class MemberController implements MemberControllerDocs {

    private final MemberServiceImpl memberService;
    private final JwtUtil jwtUtil;

    @PostMapping("/nickname/check")
    public ApiResponse<Void> checkNickname(@Valid @RequestBody MemberReqDto.Nickname dto){
        memberService.checkNicknameDuplicate(dto.getNickname());
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}")
    public ApiResponse<MemberResDto.MemberInfo> getMemberInfo(@PathVariable Long id){
        return ApiResponse.success(memberService.getMemberInfo(id));
    }

    @GetMapping("/me")
    public ApiResponse<MemberResDto.MemberInfo> getMyInfo(@RequestHeader("Authorization") String token){
        String accessToken = token.split(" ")[1];
        String email = jwtUtil.getEmail(accessToken);
        return ApiResponse.success(memberService.getMyInfo(email));
    }

}
