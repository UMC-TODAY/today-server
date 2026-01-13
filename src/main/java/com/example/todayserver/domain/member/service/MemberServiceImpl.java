package com.example.todayserver.domain.member.service;

import com.example.todayserver.domain.member.dto.EmailReqDto;
import com.example.todayserver.domain.member.excpetion.MemberException;
import com.example.todayserver.domain.member.excpetion.code.MemberErrorCode;
import com.example.todayserver.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;
    @Override
    public void checkEmailDuplicate(EmailReqDto.EmailCheck dto) {
        String email = dto.getEmail();
        if (memberRepository.existsByEmail(email)){
            throw new MemberException(MemberErrorCode.EXIST_EMAIL);
        }
    }
}
