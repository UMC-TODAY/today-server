package com.example.todayserver.domain.analysis.entity;

import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "login_streak")
public class LoginStreak extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Column(name = "last_login_at")
    private LocalDate lastLoginAt;

    @Column(name = "consecutive_days")
    private Integer consecutiveDays;

    /**
     * 연속 접속일 업데이트
     */
    public void updateStreak(LocalDate today) {
        if (this.lastLoginAt == null) {
            // 첫 접속
            this.lastLoginAt = today;
            this.consecutiveDays = 1;
        } else if (this.lastLoginAt.equals(today)) {
            // 오늘 이미 접속함 - 유지
        } else if (this.lastLoginAt.equals(today.minusDays(1))) {
            // 어제 접속 -> 연속 +1
            this.lastLoginAt = today;
            this.consecutiveDays += 1;
        } else {
            // 2일 이상 안 함 -> 초기화
            this.lastLoginAt = today;
            this.consecutiveDays = 1;
        }
    }
}
