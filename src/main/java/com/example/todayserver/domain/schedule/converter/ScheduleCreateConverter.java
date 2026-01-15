package com.example.todayserver.domain.schedule.converter;

import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.schedule.dto.ScheduleCreateReq;
import com.example.todayserver.domain.schedule.dto.SubScheduleCreateReq;
import com.example.todayserver.domain.schedule.entity.Schedule;
import com.example.todayserver.domain.schedule.entity.SubSchedule;
import com.example.todayserver.domain.schedule.enums.ScheduleSource;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Component
public class ScheduleCreateConverter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Schedule toSchedule(ScheduleCreateReq req, Member member) {

        LocalDate scheduleDate = resolveScheduleDate(req);
        LocalTime startTime = parseTimeOrNull(req.startAt());
        LocalTime endTime = parseTimeOrNull(req.endAt());

        return Schedule.builder()
                .member(member)
                .scheduleType(req.scheduleType())
                .mode(req.mode())
                .source(ScheduleSource.LOCAL)
                .title(req.title())
                .memo(req.memo())
                .color(req.bgColor())
                .emoji(req.emoji())
                .scheduleDate(scheduleDate)
                .startTime(startTime)
                .endTime(endTime)
                .repeatType(req.repeatType())
                .durationMinutes(req.duration())
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

    // date 또는 startAt 기준으로 scheduleDate 계산
    private LocalDate resolveScheduleDate(ScheduleCreateReq req) {
        if (req.date() != null) {
            return req.date();
        }
        if (req.startAt() != null && !req.startAt().isBlank()) {
            return LocalDate.parse(req.startAt(), DATE_TIME_FORMATTER);
        }
        return null;
    }

    // LocalTime으로 파싱
    private LocalTime parseTimeOrNull(String dateTimeText) {
        if (dateTimeText == null || dateTimeText.isBlank()) {
            return null;
        }
        return LocalTime.parse(dateTimeText, DATE_TIME_FORMATTER);
    }
}
