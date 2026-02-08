package com.example.todayserver.domain.member.service;

import com.example.todayserver.domain.member.converter.MemberConverter;
import com.example.todayserver.domain.member.dto.MemberReqDto;
import com.example.todayserver.domain.member.dto.MemberResDto;
import com.example.todayserver.domain.member.dto.TokenDto;
import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.member.service.util.TokenService;
import com.example.todayserver.global.common.jwt.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberService memberService;
    private final TokenService tokenService;

    public MemberResDto.LoginDto emailLogin(MemberReqDto.LoginDto dto, HttpServletResponse response){
        Member member = memberService.emailLogin(dto);
        TokenDto tokenDto = tokenService.issueTokens(member);
        CookieUtil.addCookie(response, "refreshToken", tokenDto.getRefreshToken(), (int) Duration.ofDays(1).toSeconds());

        return MemberConverter.toLoginResDto(member, tokenDto.getAccessToken());
    }
}
