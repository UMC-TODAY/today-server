package com.example.todayserver.domain.member.service;

import com.example.todayserver.domain.member.dto.MemberReqDto;
import com.example.todayserver.domain.member.dto.MemberResDto;
import com.example.todayserver.domain.member.entity.Member;
import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {

    void checkEmailDuplicate(String email);
    void emailSignup(MemberReqDto.SignupDto dto);
    Member emailLogin(MemberReqDto.LoginDto dto);
    void checkNicknameDuplicate(String nickname);
    MemberResDto.MemberInfo getMemberInfo(Long id);
    MemberResDto.MemberInfo getMyInfo(String token);
    void withdraw(String token);
    void updatePassword(String password, String token);

    @Transactional
    void updatePasswordReset(String password, String token);

    @Transactional
    void updateProfile(String token, MultipartFile profileImage, String nickName);
}
