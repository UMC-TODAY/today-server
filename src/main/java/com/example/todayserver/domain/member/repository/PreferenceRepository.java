package com.example.todayserver.domain.member.repository;

import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.member.entity.Preference;
import com.example.todayserver.domain.member.enums.PrivacyScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PreferenceRepository extends JpaRepository<Preference, Long> {

    Optional<Preference> findByMemberId(Long memberId);

    @Modifying
    @Query("""
        UPDATE Preference p
        SET p.emailAlert = :emailAlert,
            p.kakaoAlert = :kakaoAlert,
            p.reminderAlert = :reminderAlert
        WHERE p.member = :member
    """)
    void updateNotifications(Boolean emailAlert, Boolean kakaoAlert, Boolean reminderAlert, Member member);

    @Modifying
    @Query("""
        UPDATE Preference p
        SET p.privacyScope = :privacyScope,
            p.dataUse = :dataUse
        WHERE p.member = :member
    """)
    void updateInfo(PrivacyScope privacyScope, Boolean dataUse, Member member);
}
