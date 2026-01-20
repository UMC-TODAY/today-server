package com.example.todayserver.domain.member.entity;

import com.example.todayserver.domain.member.enums.PrivacyScope;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "preference")
public class Preference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reminder_alert")
    private boolean reminderAlert = true;

    @Column(name = "kakao_alert")
    private boolean kakaoAlert = true;

    @Column(name = "email_alert")
    private boolean emailAlert = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "privacy_scope")
    private PrivacyScope privacyScope = PrivacyScope.FRIEND;

    @Column(name = "data_use")
    private boolean dataUse = true;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;
}
