package com.example.todayserver.domain.schedule.connect.service.icloud;

import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.member.excpetion.code.MemberErrorCode;
import com.example.todayserver.domain.member.repository.MemberRepository;
import com.example.todayserver.domain.schedule.connect.dto.IcloudIntergrationsReq;
import com.example.todayserver.domain.schedule.connect.entity.ExternalAccount;
import com.example.todayserver.domain.schedule.connect.enums.ExternalAccountStatus;
import com.example.todayserver.domain.schedule.connect.enums.ExternalAuthType;
import com.example.todayserver.domain.schedule.connect.enums.ExternalProvider;
import com.example.todayserver.domain.schedule.connect.repository.ExternalAccountRepository;
import com.example.todayserver.domain.schedule.connect.service.ExternalSyncService;
import com.example.todayserver.global.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class IcloudConnectService {

    private final ExternalAccountRepository externalAccountRepository;
    private final MemberRepository memberRepository;
    private final ExternalSyncService externalSyncService;

    @Transactional
    public void connectAndSync(Long memberId, IcloudIntergrationsReq req) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.NOT_FOUND));

        ExternalAccount account = externalAccountRepository
                .findByMemberIdAndProvider(memberId, ExternalProvider.ICLOUD)
                .orElseGet(() -> ExternalAccount.builder()
                        .member(member)
                        .provider(ExternalProvider.ICLOUD)
                        .authType(ExternalAuthType.ICS)
                        .status(ExternalAccountStatus.CONNECTED)
                        .build()
                );

        account.updateIcsUrl(req.icsUrl());

        externalAccountRepository.save(account);

        // 일정 동기화
        YearMonth now = YearMonth.now();
        externalSyncService.syncMonth(memberId, ExternalProvider.ICLOUD, now.getYear(), now.getMonthValue());
    }
}
