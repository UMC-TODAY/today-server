package com.example.todayserver.domain.member.service;

import com.example.todayserver.domain.member.converter.MemberConverter;
import com.example.todayserver.domain.member.dto.MemberReqDto;
import com.example.todayserver.domain.member.dto.MemberResDto;
import com.example.todayserver.domain.member.dto.TokenDto;
import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.member.service.util.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberService memberService;
    private final TokenService tokenService;

    public MemberResDto.LoginDto emailLogin(MemberReqDto.LoginDto dto){
        Member member = memberService.emailLogin(dto);
        TokenDto tokenDto = tokenService.issueTokens(member);

        return MemberConverter.toLoginResDto(member, tokenDto);
    }
}
