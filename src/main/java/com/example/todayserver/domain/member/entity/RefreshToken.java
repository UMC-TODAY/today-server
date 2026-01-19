package com.example.todayserver.domain.member.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "refresh_token")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Column(name = "expire_date", nullable = false)
    private LocalDateTime expireDate;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expireDate);
    }

    public RefreshToken(String refreshToken, Member member, LocalDateTime expireDate){
        this.refreshToken = refreshToken;
        this.member = member;
        this.expireDate = expireDate;
    }

    public void update(String refreshToken, LocalDateTime expireDate){
        this.refreshToken = refreshToken;
        this.expireDate = expireDate;
    }
}
