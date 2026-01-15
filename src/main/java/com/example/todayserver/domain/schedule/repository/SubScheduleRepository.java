package com.example.todayserver.domain.schedule.repository;

import com.example.todayserver.domain.schedule.entity.SubSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubScheduleRepository extends JpaRepository<SubSchedule, Long> {
}
