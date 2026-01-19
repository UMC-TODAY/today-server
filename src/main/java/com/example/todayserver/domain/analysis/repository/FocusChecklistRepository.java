package com.example.todayserver.domain.analysis.repository;

import com.example.todayserver.domain.analysis.entity.FocusChecklist;
import com.example.todayserver.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FocusChecklistRepository extends JpaRepository<FocusChecklist, Long> {

    // 특정 회원의 모든 체크리스트 항목 조회
    List<FocusChecklist> findByMemberOrderByIdAsc(Member member);
    
    // 특정 회원의 체크리스트 항목 개수 조회
    long countByMember(Member member);
}
