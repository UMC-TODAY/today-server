package com.example.todayserver.domain.schedule.connect.repository;

import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.schedule.connect.entity.ExternalAccount;
import com.example.todayserver.domain.schedule.connect.enums.ExternalAccountStatus;
import com.example.todayserver.domain.schedule.connect.enums.ExternalProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExternalAccountRepository extends JpaRepository<ExternalAccount, Long> {
    // 특정 사용자+특정 Provider 연동 계정 조회
    Optional<ExternalAccount> findByMemberAndProvider(Member member, ExternalProvider provider);

    Optional<ExternalAccount> findByMemberIdAndProviderAndStatus(
            Long memberId, ExternalProvider provider, ExternalAccountStatus status
    );

    Optional<ExternalAccount> findByMemberIdAndProvider(Long memberId, ExternalProvider provider);

    Optional<ExternalAccount> findByProviderAndOauthState(ExternalProvider provider, String oauthState);

    List<ExternalAccount> findAllByMemberId(Long memberId);
}
