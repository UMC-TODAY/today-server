package com.example.todayserver.domain.member.converter;

import com.example.todayserver.domain.member.dto.MemberReqDto;
import com.example.todayserver.domain.member.dto.MemberResDto;
import com.example.todayserver.domain.member.dto.TokenDto;
import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.member.enums.SocialType;
import com.example.todayserver.domain.member.enums.Status;
import com.example.todayserver.global.oauth.info.OAuth2UserInfo;

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
            OAuth2UserInfo userInfo
    ){
        return Member.builder()
                .email(userInfo.getEmail())
                .nickname(userInfo.getName())
                .profileImage(userInfo.getProfileImage())
                .socialType(userInfo.getProvider())
                .providerUserId(userInfo.getProviderId())
                .status(Status.ACTIVATE)
                .build();
    }

    public static MemberResDto.MemberInfo toMemberInfo(
        Member member
    ){
        return MemberResDto.MemberInfo.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .profileImage(member.getProfileImage())
                .nickname(member.getNickname())
                .build();
    }
}
