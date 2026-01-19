package com.example.todayserver.domain.schedule.connect.service.google;

import com.example.todayserver.domain.schedule.connect.dto.ExternalEventDto;
import com.example.todayserver.domain.schedule.connect.dto.ExternalSourceDto;
import com.example.todayserver.domain.schedule.connect.dto.GoogleCalendarDto;
import com.example.todayserver.domain.schedule.connect.entity.ExternalAccount;
import com.example.todayserver.domain.schedule.connect.entity.ExternalSource;
import com.example.todayserver.domain.schedule.connect.enums.ExternalProvider;
import com.example.todayserver.domain.schedule.connect.service.core.ExternalCalendarClient;
import com.example.todayserver.global.common.exception.CustomException;
import com.example.todayserver.global.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class GoogleCalendarClient implements ExternalCalendarClient {

    private static final String BASE_URL = "https://www.googleapis.com/calendar/v3";

    @Qualifier("googleCalendarWebClient")
    private final WebClient webClient;

    public GoogleCalendarClient(@Qualifier("googleCalendarWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public ExternalProvider provider() {
        return ExternalProvider.GOOGLE;
    }

    // 캘린더 목록 조회
    @Override
    public List<ExternalSourceDto> fetchSources(ExternalAccount account) {
        try {
            String url = "/users/me/calendarList";

            GoogleCalendarDto.CalendarListResponse body = webClient.get()
                    .uri(url)
                    .headers(h -> h.setBearerAuth(getAccessToken(account)))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, resp ->
                            resp.bodyToMono(String.class)
                                    .defaultIfEmpty("")
                                    .flatMap(errorBody -> {
                                        log.error("[GoogleCalendar] fetchSources failed status={} body={}",
                                                resp.statusCode(), errorBody);
                                        return Mono.error(new CustomException(ErrorCode.EXTERNAL_API_ERROR));
                                    })
                    )
                    .bodyToMono(GoogleCalendarDto.CalendarListResponse.class)
                    .block();

            List<GoogleCalendarDto.CalendarItem> items =
                    body != null && body.items() != null ? body.items() : List.of();

            // 홀리데이 캘린더 제외
            List<GoogleCalendarDto.CalendarItem> filtered = items.stream()
                    .filter(item -> !isHolidayCalendar(item))
                    .toList();

            log.info("[GoogleCalendar] fetchSources success accountId={} count={} (filtered={})",
                    account.getId(), items.size(), filtered.size());

            return filtered.stream()
                    .map(item -> new ExternalSourceDto(
                            item.id(),
                            item.summary(),
                            buildSourceMetaJson(item)
                    ))
                    .toList();

        } catch (CustomException e) {
            throw e;

        } catch (Exception e) {
            log.error("[GoogleCalendar] fetchSources failed", e);
            throw new CustomException(ErrorCode.EXTERNAL_API_ERROR);
        }
    }

    private boolean isHolidayCalendar(GoogleCalendarDto.CalendarItem item) {
        String id = Optional.ofNullable(item.id()).orElse("").toLowerCase();
        String summary = Optional.ofNullable(item.summary()).orElse("").toLowerCase();

        if (id.contains("#holiday@group.v.calendar.google.com")) return true;
        if (id.contains("holiday")) return true;

        if (summary.contains("holiday")) return true;
        if (summary.contains("휴일")) return true;

        return false;
    }

    // 이벤트 목록 조회
    @Override
    public List<ExternalEventDto> fetchEvents(ExternalAccount account, ExternalSource source, LocalDateTime from, LocalDateTime to
    ) {
        try {
            String calendarId = source.getSourceKey();

            // 시간 값을 UTC 'Z' 형식으로 변환
            String timeMin = formatRfc3339(from);
            String timeMax = formatRfc3339(to);

            java.net.URI uri = UriComponentsBuilder
                    .fromHttpUrl(BASE_URL + "/calendars/{calendarId}/events")
                    .queryParam("singleEvents", "true")
                    .queryParam("orderBy", "startTime")
                    .queryParam("timeMin", timeMin)
                    .queryParam("timeMax", timeMax)
                    .buildAndExpand(calendarId)
                    .toUri();

            log.info("[GoogleCalendar] Final Request URI: {}", uri);

            GoogleCalendarDto.EventsResponse body = webClient.get()
                    .uri(uri)
                    .headers(h -> h.setBearerAuth(getAccessToken(account)))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, resp ->
                            resp.bodyToMono(String.class)
                                    .defaultIfEmpty("")
                                    .flatMap(errorBody -> {
                                        log.error("[GoogleCalendar] fetchEvents failed calendarId={} status={} body={}",
                                                source.getSourceKey(), resp.statusCode(), errorBody);
                                        return Mono.error(new CustomException(ErrorCode.EXTERNAL_API_ERROR));
                                    })
                    )
                    .bodyToMono(GoogleCalendarDto.EventsResponse.class)
                    .block();

            List<GoogleCalendarDto.EventItem> items =
                    body != null && body.items() != null ? body.items() : List.of();

            log.info("[GoogleCalendar] fetchEvents success calendarId={} count={}",
                    source.getSourceKey(), items.size());

            return items.stream()
                    .map(this::toExternalEventDto)
                    .toList();

        } catch (CustomException e) {
            throw e;

        } catch (Exception e) {
            log.error("[GoogleCalendar] fetchEvents failed calendarId={}", source.getSourceKey(), e);
            throw new CustomException(ErrorCode.EXTERNAL_API_ERROR);
        }
    }

    private String getAccessToken(ExternalAccount account) {
        String token = Optional.ofNullable(account.getAccessToken())
                .map(String::trim)
                .orElse("");

        if (token.isEmpty()) {
            throw new CustomException(ErrorCode.EXTERNAL_ACCESS_TOKEN_NOT_FOUND);
        }
        return token;
    }

    private String formatRfc3339(LocalDateTime dateTime) {
        return dateTime
                .atZone(ZoneId.of("Asia/Seoul"))
                .withZoneSameInstant(ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
    }

    private String buildSourceMetaJson(GoogleCalendarDto.CalendarItem item) {
        return String.format(
                "{\"timeZone\":\"%s\",\"backgroundColor\":\"%s\"}",
                Optional.ofNullable(item.timeZone()).orElse(""),
                Optional.ofNullable(item.backgroundColor()).orElse("")
        );
    }

    private ExternalEventDto toExternalEventDto(GoogleCalendarDto.EventItem event) {
        LocalDateTime startedAt = toLocalDateTime(event.start());
        LocalDateTime endedAt = toLocalDateTime(event.end());
        boolean allDay = isAllDay(event.start());

        LocalDateTime originUpdatedAt =
                event.updated() != null
                        ? OffsetDateTime.parse(event.updated()).toLocalDateTime()
                        : startedAt;

        String versionKey = event.etag() != null ? event.etag() : event.iCalUID();

        return new ExternalEventDto(
                event.id(),
                event.summary(),
                event.description(),
                startedAt,
                endedAt,
                allDay,
                originUpdatedAt,
                versionKey,
                null
        );
    }

    private LocalDateTime toLocalDateTime(GoogleCalendarDto.EventDateTime src) {
        if (src == null) return null;
        if (src.dateTime() != null) return OffsetDateTime.parse(src.dateTime()).toLocalDateTime();
        if (src.date() != null) return LocalDate.parse(src.date()).atStartOfDay();
        return null;
    }

    private boolean isAllDay(GoogleCalendarDto.EventDateTime src) {
        return src != null && src.date() != null && src.dateTime() == null;
    }
}
