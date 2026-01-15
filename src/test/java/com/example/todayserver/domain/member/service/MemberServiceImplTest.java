package com.example.todayserver.domain.member.service;

import com.example.todayserver.domain.member.dto.EmailReqDto;
import com.example.todayserver.domain.member.excpetion.MemberException;
import com.example.todayserver.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


@SpringBootTest
class MemberServiceImplTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;


    @Test
    void checkEmailDuplicate(){
        EmailReqDto.EmailCheck dto = new EmailReqDto.EmailCheck();
        dto.setEmail("test2@example.com");

        memberService.checkEmailDuplicate(dto.getEmail());
        assertDoesNotThrow(() -> memberService.checkEmailDuplicate(dto.getEmail()));
    }

    @Test
    void checkEmailDuplicate_error(){
        EmailReqDto.EmailCheck dto = new EmailReqDto.EmailCheck();
        dto.setEmail("test@example.com");

        MemberException exception = Assertions.assertThrows(
                MemberException.class,
                () -> memberService.checkEmailDuplicate(dto.getEmail())
        );

        Assertions.assertTrue(
                exception.getMessage().contains("이미 가입된 이메일입니다.")
        );
    }
}