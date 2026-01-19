package com.example.todayserver.domain.schedule.connect.converter;

import com.example.todayserver.domain.schedule.connect.dto.ExternalEventDto;
import com.example.todayserver.domain.schedule.connect.entity.ExternalSource;
import com.example.todayserver.domain.schedule.connect.entity.ScheduleExternal;
import com.example.todayserver.domain.schedule.connect.enums.ScheduleExternalVersionType;
import com.example.todayserver.domain.schedule.entity.Schedule;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ScheduleExternalConverter {

    private ScheduleExternalConverter() {}

    public static ScheduleExternal newMapping(
            ExternalEventDto event,
            ExternalSource source,
            Schedule schedule,
            ScheduleExternalVersionType versionType
    ) {
        return ScheduleExternal.builder()
                .externalSource(source)
                .schedule(schedule)
                .externalEventId(event.externalEventId())
                .versionType(versionType)
                .versionKey(buildVersionKey(event, versionType))
                .originUpdatedAt(event.originUpdatedAt())
                .build();
    }

    public static String buildVersionKey(ExternalEventDto event, ScheduleExternalVersionType versionType) {
        return switch (versionType) {
            case ETAG -> notBlankElse(event.versionKey(), "");
            case UPDATED_AT -> event.originUpdatedAt() == null ? "" : event.originUpdatedAt().toString();
            case HASH -> sha256(
                    safe(event.title())
                            + "|" + safe(event.description())
                            + "|" + safe(event.startedAt())
                            + "|" + safe(event.endedAt())
                            + "|" + safe(event.originUpdatedAt())
            );
        };
    }

    private static String notBlankElse(String v, String fallback) {
        return (v != null && !v.isBlank()) ? v : fallback;
    }

    private static String safe(Object v) {
        return v == null ? "" : v.toString();
    }

    private static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }
}
