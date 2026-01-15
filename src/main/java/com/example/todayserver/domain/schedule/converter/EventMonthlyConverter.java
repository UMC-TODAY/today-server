package com.example.todayserver.domain.schedule.converter;

import com.example.todayserver.domain.schedule.dto.EventMonthlyListRes;
import com.example.todayserver.domain.schedule.entity.Schedule;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class EventMonthlyConverter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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

        String startedAt = null;
        String endedAt = null;

        if (schedule.getStartedAt() != null) {
            startedAt = schedule.getStartedAt().format(DATE_TIME_FORMATTER);
        }

        if (schedule.getEndedAt() != null) {
            endedAt = schedule.getEndedAt().format(DATE_TIME_FORMATTER);
        }

        return new EventMonthlyListRes.EventDto(
                schedule.getId(),
                schedule.getTitle(),
                schedule.getColor(),
                schedule.getEmoji(),
                schedule.isDone(),
                startedAt,
                endedAt,
                schedule.getSource()
        );
    }
}
