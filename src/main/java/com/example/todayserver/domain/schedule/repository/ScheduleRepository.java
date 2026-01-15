package com.example.todayserver.domain.schedule.repository;

import com.example.todayserver.domain.schedule.entity.Schedule;
import com.example.todayserver.domain.schedule.enums.ScheduleSource;
import com.example.todayserver.domain.schedule.enums.ScheduleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    // 월별 일정 데이터 전체 조회
    List<Schedule> findByMemberIdAndScheduleTypeAndStartedAtBetween(
            Long memberId,
            ScheduleType scheduleType,
            LocalDateTime startedAtFrom,
            LocalDateTime startedAtTo
    );


    // 월별 일정 데이터 필터 조회
    List<Schedule> findByMemberIdAndScheduleTypeAndStartedAtBetweenAndSource(
            Long memberId,
            ScheduleType scheduleType,
            LocalDateTime startedAtFrom,
            LocalDateTime startedAtTo,
            ScheduleSource source
    );

    // 월별 총 일정 개수 조회
    long countByMemberIdAndScheduleTypeAndStartedAtBetween(
            Long memberId,
            ScheduleType scheduleType,
            LocalDateTime startedAtFrom,
            LocalDateTime startedAtTo
    );

    // 월별 완료된 일정 총 개수 조회
    long countByMemberIdAndScheduleTypeAndStartedAtBetweenAndIsDoneTrue(
            Long memberId,
            ScheduleType scheduleType,
            LocalDateTime startedAtFrom,
            LocalDateTime startedAtTo
    );
}
