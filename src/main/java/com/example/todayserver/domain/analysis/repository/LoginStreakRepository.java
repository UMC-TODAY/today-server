package com.example.todayserver.domain.analysis.repository;

import com.example.todayserver.domain.analysis.entity.LoginStreak;
import com.example.todayserver.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginStreakRepository extends JpaRepository<LoginStreak, Long> {
    
    Optional<LoginStreak> findByMember(Member member);
    
    Optional<LoginStreak> findByMemberId(Long memberId);
}
