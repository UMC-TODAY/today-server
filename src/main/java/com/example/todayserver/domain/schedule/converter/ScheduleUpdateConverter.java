package com.example.todayserver.domain.schedule.converter;

import com.example.todayserver.domain.schedule.dto.ScheduleUpdateReq;
import com.example.todayserver.domain.schedule.entity.Schedule;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
public class ScheduleUpdateConverter {

    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public LocalDateTime resolveStartedAt(Schedule origin, ScheduleUpdateReq req) {
        if (req.startAt() != null && !req.startAt().isBlank()) {
            return LocalDateTime.parse(req.startAt(), DT);
        }
        if (req.date() != null && origin.getStartedAt() != null) {
            LocalTime t = origin.getStartedAt().toLocalTime();
            return LocalDateTime.of(req.date(), t);
        }
        return null;
    }

    public LocalDateTime resolveEndedAt(Schedule origin, ScheduleUpdateReq req) {
        if (req.endAt() != null && !req.endAt().isBlank()) {
            return LocalDateTime.parse(req.endAt(), DT);
        }
        if (req.date() != null && origin.getEndedAt() != null) {
            LocalTime t = origin.getEndedAt().toLocalTime();
            return LocalDateTime.of(req.date(), t);
        }
        return null;
    }

    public LocalDate resolveDateFromSchedule(Schedule schedule) {
        return schedule.getStartedAt() == null ? null : schedule.getStartedAt().toLocalDate();
    }
}
