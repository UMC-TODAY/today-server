package com.example.todayserver.domain.schedule.converter;

import com.example.todayserver.domain.schedule.dto.ScheduleDetailRes;
import com.example.todayserver.domain.schedule.entity.Schedule;
import com.example.todayserver.domain.schedule.entity.SubSchedule;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ScheduleDetailConverter {

    private static final DateTimeFormatter REQ_DT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ScheduleDetailRes toDetailRes(Schedule schedule, List<SubSchedule> subSchedules) {
        List<ScheduleDetailRes.SubScheduleDetailRes> subRes =
                (subSchedules == null || subSchedules.isEmpty())
                        ? null
                        : subSchedules.stream()
                        .map(this::toSubDetailRes)
                        .toList();

        return new ScheduleDetailRes(
                schedule.getId(),
                schedule.getScheduleType(),
                schedule.getMode(),
                schedule.getTitle(),
                resolveDate(schedule.getStartedAt()),
                schedule.getDurationMinutes(),
                schedule.isAllDay(),
                schedule.getRepeatType(),
                schedule.getMemo(),
                schedule.getEmoji(),
                schedule.getColor(),
                formatDt(schedule.getStartedAt()),
                formatDt(schedule.getEndedAt()),
                subRes
        );
    }

    private ScheduleDetailRes.SubScheduleDetailRes toSubDetailRes(SubSchedule s) {
        return new ScheduleDetailRes.SubScheduleDetailRes(
                s.getId(),
                s.getTitle(),
                s.getColor(),
                s.getEmoji()
        );
    }

    private String formatDt(LocalDateTime dt) {
        return dt == null ? null : dt.format(REQ_DT_FORMATTER);
    }

    private LocalDate resolveDate(LocalDateTime startedAt) {
        return startedAt == null ? null : startedAt.toLocalDate();
    }
}
