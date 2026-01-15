package com.example.todayserver.domain.member.converter;

import com.example.todayserver.domain.member.dto.MemberReqDto;
import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.member.enums.SocialType;

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
}
