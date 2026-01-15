package com.example.todayserver.domain.member.service;

import com.example.todayserver.domain.member.dto.MemberReqDto;
import com.example.todayserver.domain.member.dto.MemberResDto;

public interface MemberService {

    void checkEmailDuplicate(String email);
    void emailSignup(MemberReqDto.SignupDto dto);
    MemberResDto.LoginDto emailLogin(MemberReqDto.LoginDto dto);
}
