package com.example.todayserver.domain.schedule.converter;

import com.example.todayserver.domain.schedule.dto.EventMonthlyListRes;
import com.example.todayserver.domain.schedule.entity.Schedule;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class EventMonthlyConverter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;      // yyyy-MM-dd
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");  // HH:mm

    // 월별 일정 리스트 -> EventMonthlyListRes로 변환
    public EventMonthlyListRes toEventMonthlyListRes(String filter, List<Schedule> schedules
    ) {
        List<EventMonthlyListRes.EventDto> events = schedules.stream()
                .map(this::toEventDto)
                .toList();

        return new EventMonthlyListRes(filter, events);
    }

    // Schedule 엔티티 1건 -> EventDto 로 변환
    private EventMonthlyListRes.EventDto toEventDto(Schedule schedule) {
        String date = schedule.getScheduleDate() != null ? schedule.getScheduleDate().format(DATE_FORMATTER) : null;

        String startTime = schedule.getStartTime() != null ? schedule.getStartTime().format(TIME_FORMATTER) : null;

        String endTime = schedule.getEndTime() != null ? schedule.getEndTime().format(TIME_FORMATTER) : null;

        return new EventMonthlyListRes.EventDto(
                schedule.getId(),
                schedule.getTitle(),
                schedule.getColor(),
                schedule.getEmoji(),
                schedule.isDone(),
                date,
                startTime,
                endTime,
                schedule.getSource()
        );
    }
}
