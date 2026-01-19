package com.example.todayserver.domain.analysis.repository;

import com.example.todayserver.domain.analysis.entity.DailyDifficulty;
import com.example.todayserver.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyDifficultyRepository extends JpaRepository<DailyDifficulty, Long> {

    // 특정 회원의 특정 날짜 난이도 평가 조회
    Optional<DailyDifficulty> findByMemberAndDate(Member member, LocalDate date);

    // 특정 회원의 특정 날짜 난이도 평가 존재 여부
    boolean existsByMemberAndDate(Member member, LocalDate date);
}
