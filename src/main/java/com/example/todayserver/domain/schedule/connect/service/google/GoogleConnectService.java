package com.example.todayserver.domain.schedule.connect.service.google;

import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.member.excpetion.code.MemberErrorCode;
import com.example.todayserver.domain.member.repository.MemberRepository;
import com.example.todayserver.domain.schedule.connect.dto.GoogleAuthorizeUrlRes;
import com.example.todayserver.domain.schedule.connect.dto.GoogleTokenRes;
import com.example.todayserver.domain.schedule.connect.entity.ExternalAccount;
import com.example.todayserver.domain.schedule.connect.enums.ExternalAccountStatus;
import com.example.todayserver.domain.schedule.connect.enums.ExternalAuthType;
import com.example.todayserver.domain.schedule.connect.enums.ExternalProvider;
import com.example.todayserver.domain.schedule.connect.repository.ExternalAccountRepository;
import com.example.todayserver.domain.schedule.connect.service.ExternalSyncAsyncService;
import com.example.todayserver.global.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GoogleConnectService {

    private final MemberRepository memberRepository;
    private final ExternalAccountRepository externalAccountRepository;
    private final GoogleOAuthClient googleOAuthClient;
    private final ExternalSyncAsyncService externalSyncAsyncService;


    // Google 캘린더 연동 인가 URL 생성
    @Transactional(readOnly = true)
    public GoogleAuthorizeUrlRes buildGoogleAuthorizeUrl(Long memberId) {
        String state = String.valueOf(memberId);
        String url = googleOAuthClient.buildAuthorizeUrl(state);
        return new GoogleAuthorizeUrlRes(url);
    }

    // Google OAuth 콜백 처리
    @Transactional
    public void handleGoogleCallback(String code, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.NOT_FOUND));

        // code -> token 교환 (서버 ↔ 구글 서버)
        GoogleTokenRes tokenRes = googleOAuthClient.exchangeCodeForToken(code);
        LocalDateTime expiresAt = googleOAuthClient.calcExpiresAt(tokenRes.expiresIn());

        // ExternalAccount 조회 or 생성
        ExternalAccount externalAccount = externalAccountRepository
                .findByMemberAndProvider(member, ExternalProvider.GOOGLE)
                .orElseGet(() -> ExternalAccount.builder()
                        .member(member)
                        .provider(ExternalProvider.GOOGLE)
                        .authType(ExternalAuthType.OAUTH)
                        .status(ExternalAccountStatus.CONNECTED)
                        .build()
                );

        // 토큰/상태 업데이트
        externalAccount.updateTokens(tokenRes.accessToken(), tokenRes.refreshToken(), expiresAt);
        externalAccount.updateStatus(ExternalAccountStatus.CONNECTED);
        externalAccount.updateLastSyncedAt(null); // 아직 동기화 전이므로 초기화

        // 저장
        externalAccountRepository.save(externalAccount);

        // 일정 자동 동기화
        LocalDate now = LocalDate.now();
        runAfterCommit(() -> externalSyncAsyncService.syncMonth(
                memberId,
                ExternalProvider.GOOGLE,
                now.getYear(),
                now.getMonthValue()
        ));
    }

    private void runAfterCommit(Runnable task) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    task.run();
                }
            });
        } else {
            task.run();
        }
    }
}
