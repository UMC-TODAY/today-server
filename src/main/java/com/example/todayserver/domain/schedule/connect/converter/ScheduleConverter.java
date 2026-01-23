package com.example.todayserver.domain.schedule.connect.converter;

import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.schedule.connect.dto.CsvScheduleImportDto;
import com.example.todayserver.domain.schedule.connect.dto.ExternalEventDto;
import com.example.todayserver.domain.schedule.entity.Schedule;
import com.example.todayserver.domain.schedule.enums.ScheduleSource;
import com.example.todayserver.domain.schedule.enums.ScheduleType;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ScheduleConverter {
    private ScheduleConverter() {}

    private static final List<String> EVENT_COLOR_PALETTE = List.of(
            "#F5DEFE", "#88A2C9", "#F0EFC4", "#8DE7C9", "#FBEBD5",
            "#E7ECE1", "#F3DCBA", "#DAE1E8", "#FB8C54", "#858585",
            "#D2F0FF", "#E6B6B6", "#FCE3E3", "#EBC99A", "#DCDCDC",
            "#DEFEE2", "#AEDEAC", "#B396C6", "#FCDCE2", "#A0C9CE",
            "#EDEDED", "#B1BAD4", "#F3C9B4", "#C7AFAF", "#A1E992",
            "#F5DEFE", "#BE77C4", "#F0DEC4", "#698F95", "#F9BF6F",
            "#D4FDE3", "#885959", "#B9DCFE", "#B87959", "#C3ADEC",
            "#E2E2E2", "#A05858", "#F9AEAE", "#F7950C", "#C9ADD8",
            "#A0C5AE", "#6CA669", "#A0B7B0", "#B1757F", "#A4DCC3",
            "#F6FEDE", "#929BB3", "#B49789", "#B86969", "#B7FAAA",
            "#D4EAFD", "#906294", "#A5937B", "#DBEBED", "#FAEEDD",
            "#F9D8CF", "#D36FAB", "#275C8C", "#94C68A", "#EBE2A7",
            "#A0BA7A", "#E3D8D8", "#C3C3C3", "#D9F5D3", "#F7F4FD"
    );

    private static String randomEventColor() {
        int idx = ThreadLocalRandom.current().nextInt(EVENT_COLOR_PALETTE.size());
        return EVENT_COLOR_PALETTE.get(idx);
    }

    public static Schedule fromExternalEvent(ExternalEventDto event, Member member, ScheduleSource source) {
        return Schedule.builder()
                .member(member)
                .scheduleType(ScheduleType.EVENT)
                .mode(null)
                .source(source)
                .title(event.title() == null || event.title().isBlank() ? "(제목 없음)" : event.title())
                .memo(event.description())
                .color(randomEventColor())
                .emoji(null)
                .startedAt(event.startedAt())
                .endedAt(event.endedAt())
                .repeatType(null)
                .durationMinutes(null)
                .isDone(false)
                .isAllDay(event.allDay())
                .build();
    }

    public static Schedule fromCsv(
            CsvScheduleImportDto.Normalized dto,
            Member member
    ) {
        return Schedule.builder()
                .member(member)
                .scheduleType(ScheduleType.EVENT)
                .mode(null)
                .source(ScheduleSource.CSV)
                .title(dto.title())
                .memo(dto.memo())
                .color(randomEventColor())
                .emoji(null)
                .startedAt(dto.startedAt())
                .endedAt(dto.endedAt())
                .repeatType(null)
                .durationMinutes(null)
                .isDone(false)
                .isAllDay(dto.isAllDay())
                .build();
    }
}
