package com.example.todayserver.domain.schedule.connect.dto;

import java.time.LocalDateTime;

public record ExternalEventDto(
        String externalEventId,
        String title,
        String description,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        boolean allDay,
        LocalDateTime originUpdatedAt,
        String versionKey,
        String rawJson
) { }