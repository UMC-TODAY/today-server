package com.example.todayserver.domain.member.service;

import com.example.todayserver.domain.member.dto.MemberReqDto;
import com.example.todayserver.domain.member.dto.MemberResDto;
import com.example.todayserver.domain.member.entity.Member;

public interface MemberService {

    void checkEmailDuplicate(String email);
    void emailSignup(MemberReqDto.SignupDto dto);
    Member emailLogin(MemberReqDto.LoginDto dto);
    void checkNicknameDuplicate(String nickname);
    MemberResDto.MemberInfo getMemberInfo(Long id);
    MemberResDto.MemberInfo getMyInfo(String email);
    void withdraw(String email);
    void updatePassword(String password, String email);
}
