package com.example.todayserver.domain.member.repository;

import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.member.enums.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);
    Optional<Member> findByEmail(String email);
    Optional<Member> findBySocialTypeAndProviderUserId(SocialType socialType, String providerId);
    boolean existsByNickname(String nickname);
}