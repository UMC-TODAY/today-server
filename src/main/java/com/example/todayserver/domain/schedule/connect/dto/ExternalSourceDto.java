package com.example.todayserver.domain.schedule.connect.dto;

public record ExternalSourceDto(
        String sourceKey,
        String sourceName,
        String metaJson
) {}
