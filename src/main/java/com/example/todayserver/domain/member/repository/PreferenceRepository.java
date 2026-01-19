package com.example.todayserver.domain.member.repository;

import com.example.todayserver.domain.member.entity.Preference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreferenceRepository extends JpaRepository<Preference, Long> {

    Optional<Preference> findByMemberId(Long memberId);
}
