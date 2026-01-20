package com.example.todayserver.domain.schedule.connect.entity;

import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.schedule.connect.enums.ExternalAccountStatus;
import com.example.todayserver.domain.schedule.connect.enums.ExternalAuthType;
import com.example.todayserver.domain.schedule.connect.enums.ExternalProvider;
import com.example.todayserver.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "external_account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ExternalAccount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 20)
    private ExternalProvider provider;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_type", nullable = false, length = 20)
    private ExternalAuthType authType;

    @Lob
    @Column(name = "access_token",  nullable = true)
    private String accessToken;

    @Lob
    @Column(name = "refresh_token",  nullable = true)
    private String refreshToken;

    @Column(name = "expired_at",  nullable = true)
    private LocalDateTime expiredAt;

    //계정 관련 추가 정보 (구글 userId, scope, 이메일 등)를 암호화해서 JSON 형태로 저장할 때 사용)
    @Lob
    @Column(name = "credential_encrypted",  nullable = true)
    private String credentialEncrypted;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ExternalAccountStatus status;

    // 마지막으로 동기화를 수행한 시각
    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;

    @Column(name = "oauth_state", length = 100)
    private String oauthState;

    @Column(name = "oauth_state_expired_at")
    private LocalDateTime oauthStateExpiredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "externalAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ExternalSource> externalSources = new ArrayList<>();

    public void updateTokens(String accessToken, String refreshToken, LocalDateTime expiredAt) {
        this.accessToken = accessToken;
        if (refreshToken != null && !refreshToken.isBlank()) {
            this.refreshToken = refreshToken;
        }
        this.expiredAt = expiredAt;
    }

    public void updateStatus(ExternalAccountStatus status) {
        this.status = status;
    }

    public void updateLastSyncedAt(LocalDateTime lastSyncedAt) {
        this.lastSyncedAt = lastSyncedAt;
    }

    public void issueOAuthState(String state, LocalDateTime expiredAt) {
        this.oauthState = state;
        this.oauthStateExpiredAt = expiredAt;
        this.status = ExternalAccountStatus.ERROR;
    }

    public void clearOAuthState() {
        this.oauthState = null;
        this.oauthStateExpiredAt = null;
    }

    public boolean isOAuthStateExpired() {
        return oauthStateExpiredAt != null
                && LocalDateTime.now().isAfter(oauthStateExpiredAt);
    }

    public void activate(String accessToken, LocalDateTime expiredAt) {
        this.accessToken = accessToken;
        this.expiredAt = expiredAt;
        this.status = ExternalAccountStatus.CONNECTED;
        clearOAuthState();
    }

}