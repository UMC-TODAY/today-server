package com.example.todayserver.domain.schedule.connect.dto;

import java.util.List;


public final class GoogleCalendarDto {

    private GoogleCalendarDto() {
    }

   // users/me/calendarList 응답
    public record CalendarListResponse(
            List<CalendarItem> items
    ) { }

    // 일정 단건
    public record CalendarItem(
            String id,              // calendarId
            String summary,         // 캘린더 이름
            String timeZone,        // 타임존
            String backgroundColor  // 캘린더 색상 (옵션)
    ) { }

    // calendars/{calendarId}/events 응답
    public record EventsResponse(
            List<EventItem> items
    ) { }

    // 이벤트 단건
    public record EventItem(
            String id,
            String summary,
            String description,
            EventDateTime start,
            EventDateTime end,
            String updated,   // RFC3339
            String etag,
            String iCalUID
    ) { }

    // 이벤트 시간
    public record EventDateTime(
            String dateTime,  // "2026-01-19T10:00:00+09:00"
            String date,      // "2026-01-19" (종일 일정)
            String timeZone
    ) { }
}
