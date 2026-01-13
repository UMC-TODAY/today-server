package com.example.todayserver.domain.member.service;

import com.example.todayserver.domain.member.dto.EmailReqDto;

public interface MemberService {

    void checkEmailDuplicate(EmailReqDto.EmailCheck dto);
}
