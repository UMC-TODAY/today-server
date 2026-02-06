package com.example.todayserver.domain.schedule.repository;

import com.example.todayserver.domain.schedule.entity.SubSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface SubScheduleRepository extends JpaRepository<SubSchedule, Long> {
    List<SubSchedule> findAllBySchedule_Id(Long scheduleId);

    Optional<SubSchedule> findByIdAndSchedule_Id(Long subScheduleId, Long scheduleId);

    // 요청한 schedule id 목록 중 본인(memberId) 소유 schedule에 속한 sub_schedule을 먼저 일괄 삭제
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
    delete from SubSchedule ss
    where ss.schedule.member.id = :memberId
      and ss.schedule.id in :scheduleIds
    """)
    int deleteAllByMemberIdAndScheduleIdIn(
            @Param("memberId") Long memberId,
            @Param("scheduleIds") List<Long> scheduleIds
    );
}
