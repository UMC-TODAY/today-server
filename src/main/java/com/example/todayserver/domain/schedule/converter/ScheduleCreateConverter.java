package com.example.todayserver.domain.schedule.converter;

import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.schedule.dto.ScheduleCreateReq;
import com.example.todayserver.domain.schedule.dto.SubScheduleCreateReq;
import com.example.todayserver.domain.schedule.entity.Schedule;
import com.example.todayserver.domain.schedule.entity.SubSchedule;
import com.example.todayserver.domain.schedule.enums.ScheduleSource;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Component
public class ScheduleCreateConverter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Schedule toSchedule(ScheduleCreateReq req, Member member) {

        LocalDateTime startedAt = resolveStartedAt(req);
        LocalDateTime endedAt = parseDateTimeOrNull(req.endAt());

        return Schedule.builder()
                .member(member)
                .scheduleType(req.scheduleType())
                .mode(req.mode())
                .source(ScheduleSource.LOCAL)
                .title(req.title())
                .memo(req.memo())
                .color(req.bgColor())
                .emoji(req.emoji())
                .startedAt(startedAt)
                .endedAt(endedAt)
                .repeatType(req.repeatType())
                .durationMinutes(req.duration()) // EVENT 일 경우 null
                .isDone(false) // 초기값 = false
                .build();
    }

    // subSchedules -> SubSchedule 엔티티 리스트 변환
    public List<SubSchedule> toSubSchedules(ScheduleCreateReq req, Schedule schedule) {
        List<SubScheduleCreateReq> subSchedules = req.subSchedules();
        if (subSchedules == null || subSchedules.isEmpty()) {
            return Collections.emptyList();
        }

        return subSchedules.stream()
                .map(subReq -> SubSchedule.builder()
                        .schedule(schedule)
                        .title(subReq.subTitle())
                        .color(subReq.subColor())
                        .emoji(subReq.subEmoji())
                        .build()
                )
                .toList();
    }

    // startedAt 계산
    private LocalDateTime resolveStartedAt(ScheduleCreateReq req) {
        if (req.startAt() != null && !req.startAt().isBlank()) {
            return LocalDateTime.parse(req.startAt(), DATE_TIME_FORMATTER);
        }
        if (req.date() != null) {
            return req.date().atStartOfDay(); // 해당 일자의 T00:00:00
        }
        return null;
    }

    // endedAt 파싱 (Event에서만 사용)
    private LocalDateTime parseDateTimeOrNull(String dateTimeText) {
        if (dateTimeText == null || dateTimeText.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeText, DATE_TIME_FORMATTER);
    }
}
