package com.example.todayserver.domain.member.controller;

import com.example.todayserver.domain.member.dto.MemberReqDto;
import com.example.todayserver.domain.member.dto.MemberResDto;
import com.example.todayserver.domain.member.service.MemberServiceImpl;
import com.example.todayserver.global.common.jwt.JwtUtil;
import com.example.todayserver.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Tag(name = "Member", description = "회원정보 관련 API")
public class MemberController implements MemberControllerDocs {

    private final MemberServiceImpl memberService;

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
        return ApiResponse.success(memberService.getMyInfo(token));
    }

    @PatchMapping("/withdraw")
    public ApiResponse<Void> withdraw(@RequestHeader("Authorization") String token){
        memberService.withdraw(token);
        return ApiResponse.success(null);
    }

    @PatchMapping("/password")
    public ApiResponse<Void> updatePassword(@RequestHeader("Authorization") String token, @Valid @RequestBody MemberReqDto.Password dto){
        memberService.updatePassword(dto.getPassword(), token);
        return ApiResponse.success(null);
    }

    @PatchMapping("/password/reset")
    public ApiResponse<Void> updatePasswordRest(@Valid @RequestBody MemberReqDto.LoginDto dto){
        memberService.updatePasswordReset(dto.getPassword(), dto.getEmail());
        return ApiResponse.success(null);
    }


    @PatchMapping(value = "/profile",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Void> updateProfile(@RequestHeader("Authorization") String token, @ModelAttribute MemberReqDto.ProfileInfo dto){
        memberService.updateProfile(token, dto);
        return ApiResponse.success(null);
    }
}
