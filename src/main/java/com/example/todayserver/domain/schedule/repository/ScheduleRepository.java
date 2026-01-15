package com.example.todayserver.domain.schedule.repository;

import com.example.todayserver.domain.schedule.entity.Schedule;
import com.example.todayserver.domain.schedule.enums.ScheduleSource;
import com.example.todayserver.domain.schedule.enums.ScheduleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    // 월별 일정 데이터 전체 조회
    List<Schedule> findByMemberIdAndScheduleTypeAndScheduleDateBetween(
            Long memberId,
            ScheduleType scheduleType,
            LocalDate startDate,
            LocalDate endDate
    );


    // 월별 일정 데이터 필터 조회
    List<Schedule> findByMemberIdAndScheduleTypeAndScheduleDateBetweenAndSource(
            Long memberId,
            ScheduleType scheduleType,
            LocalDate startDate,
            LocalDate endDate,
            ScheduleSource source
    );
}
