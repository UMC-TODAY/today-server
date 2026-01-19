package com.example.todayserver.domain.schedule.connect.repository;

import com.example.todayserver.domain.schedule.connect.entity.ScheduleExternal;
import com.example.todayserver.domain.schedule.connect.enums.ScheduleExternalVersionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScheduleExternalRepository extends JpaRepository<ScheduleExternal, Long> {
    Optional<ScheduleExternal> findByExternalSourceIdAndExternalEventIdAndVersionType(
            Long externalSourceId,
            String externalEventId,
            ScheduleExternalVersionType versionType
    );
}
