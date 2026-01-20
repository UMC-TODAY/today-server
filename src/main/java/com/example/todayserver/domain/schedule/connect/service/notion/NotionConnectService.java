package com.example.todayserver.domain.schedule.connect.service.notion;

import com.example.todayserver.domain.member.repository.MemberRepository;
import com.example.todayserver.domain.schedule.connect.dto.NotionTokenRes;
import com.example.todayserver.domain.schedule.connect.entity.ExternalAccount;
import com.example.todayserver.domain.schedule.connect.enums.ExternalAccountStatus;
import com.example.todayserver.domain.schedule.connect.enums.ExternalAuthType;
import com.example.todayserver.domain.schedule.connect.enums.ExternalProvider;
import com.example.todayserver.domain.schedule.connect.repository.ExternalAccountRepository;
import com.example.todayserver.domain.schedule.connect.service.ExternalSyncAsyncService;
import com.example.todayserver.global.common.exception.CustomException;
import com.example.todayserver.global.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotionConnectService {

    private final ExternalAccountRepository externalAccountRepository;
    private final MemberRepository memberRepository;
    private final NotionOAuthClient notionOAuthClient;
    private final ExternalSyncAsyncService externalSyncAsyncService;

    @Transactional
    public String buildAuthorizeUrl(Long memberId) {

        ExternalAccount account = externalAccountRepository
                .findByMemberIdAndProvider(memberId, ExternalProvider.NOTION)
                .orElseGet(() ->
                        externalAccountRepository.save(
                                ExternalAccount.builder()
                                        .member(memberRepository.getReferenceById(memberId))
                                        .provider(ExternalProvider.NOTION)
                                        .authType(ExternalAuthType.OAUTH)
                                        .status(ExternalAccountStatus.ERROR)
                                        .build()
                        )
                );

        String state = "st_" + UUID.randomUUID();
        account.issueOAuthState(state, LocalDateTime.now().plusMinutes(10));

        return notionOAuthClient.buildAuthorizeUrl(state);
    }

    @Transactional
    public void handleCallback(String code, String state) {

        ExternalAccount account = externalAccountRepository
                .findByProviderAndOauthState(ExternalProvider.NOTION, state)
                .orElseThrow(() -> new CustomException(ErrorCode.EXTERNAL_OAUTH_STATE_INVALID));

        if (account.isOAuthStateExpired()) {
            throw new CustomException(ErrorCode.EXTERNAL_OAUTH_STATE_INVALID);
        }

        NotionTokenRes token = notionOAuthClient.exchangeCodeForToken(code);

        // Notion은 일반적으로 refresh token / expires_at을 주지 않는 형태라 null 처리
        account.activate(token.accessToken(), null);

        // 일정 자동 동기화
        LocalDate now = LocalDate.now();
        externalSyncAsyncService.syncMonth(
                account.getMember().getId(),
                ExternalProvider.NOTION,
                now.getYear(),
                now.getMonthValue()
        );
    }
}
