package com.example.todayserver.domain.member.service;

import com.example.todayserver.domain.member.dto.MemberReqDto;

public interface MemberService {

    void checkEmailDuplicate(String email);
    void emailSignup(MemberReqDto.SignupDto dto);
}
