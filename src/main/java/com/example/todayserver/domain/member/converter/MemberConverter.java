package com.example.todayserver.domain.member.converter;

import com.example.todayserver.domain.member.dto.MemberReqDto;
import com.example.todayserver.domain.member.dto.MemberResDto;
import com.example.todayserver.domain.member.dto.TokenDto;
import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.member.enums.SocialType;

import java.util.Map;

public class MemberConverter {

    public static Member toMember(
            MemberReqDto.SignupDto dto,
            String password,
            String nickname
    ){
        return Member.builder()
                .email(dto.getEmail())
                .password(password)
                .birth(dto.getBirth())
                .nickname(nickname)
                .socialType(SocialType.EMAIL)
                .build();
    }

    public static MemberResDto.LoginDto toLoginResDto(
            Member member,
            TokenDto tokenDto
    ){
        return MemberResDto.LoginDto.builder()
                .memberId(member.getId())
                .accessToken(tokenDto.getAccessToken())
                .refreshToken(tokenDto.getRefreshToken())
                .build();
    }

    public static Member toOAuthMember(
            String email,
            SocialType provider,
            Map<String, Object> attributes
    ){
        return Member.builder()
                .email(email)
                .nickname((String) attributes.get("name"))
                .profileImage((String) attributes.get("picture"))
                .socialType(provider)
                .providerUserId((String) attributes.get("sub"))
                .build();
    }
}
