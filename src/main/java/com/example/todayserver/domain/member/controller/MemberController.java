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
        String email = getEmailByAccessToken(token);
        return ApiResponse.success(memberService.getMyInfo(email));
    }

    @PatchMapping("/withdraw")
    public ApiResponse<Void> withdraw(@RequestHeader("Authorization") String token){
        String email = getEmailByAccessToken(token);
        memberService.withdraw(email);
        return ApiResponse.success(null);
    }

    @PatchMapping("/password")
    public ApiResponse<Void> updatePassword(@RequestHeader("Authorization") String token, @Valid @RequestBody MemberReqDto.Password dto){
        String email = getEmailByAccessToken(token);
        memberService.updatePassword(dto.getPassword(), email);
        return ApiResponse.success(null);
    }

    private String getEmailByAccessToken(String token) {
        String accessToken = token.split(" ")[1];
        return jwtUtil.getEmail(accessToken);
    }

}
