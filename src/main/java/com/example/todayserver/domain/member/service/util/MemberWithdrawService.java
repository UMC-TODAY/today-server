package com.example.todayserver.domain.member.service.util;

import com.example.todayserver.domain.member.excpetion.MemberException;
import com.example.todayserver.domain.member.excpetion.code.MemberErrorCode;
import com.example.todayserver.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberWithdrawService {

    private final MemberRepository memberRepository;

    public void checkWithdraw(String email) {
        memberRepository.findByDeletedStatus(email)
                .ifPresent(member -> {
                    LocalDate inactivateDate = member.getInactivateDate();
                    System.out.println("inactivate date: " + inactivateDate);

                    if (inactivateDate != null &&
                            inactivateDate.isAfter(LocalDate.now().minusDays(7))) {
                        throw new MemberException(MemberErrorCode.WITHDRAW_EMAIL);
                    }

                    memberRepository.deletePrevEmail(member.getId());
                });
    }
}
