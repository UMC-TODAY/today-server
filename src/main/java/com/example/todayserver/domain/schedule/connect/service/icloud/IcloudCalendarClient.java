package com.example.todayserver.domain.schedule.connect.service.icloud;

import com.example.todayserver.domain.schedule.connect.dto.ExternalEventDto;
import com.example.todayserver.domain.schedule.connect.dto.ExternalSourceDto;
import com.example.todayserver.domain.schedule.connect.entity.ExternalAccount;
import com.example.todayserver.domain.schedule.connect.entity.ExternalSource;
import com.example.todayserver.domain.schedule.connect.enums.ExternalProvider;
import com.example.todayserver.domain.schedule.connect.service.core.ExternalCalendarClient;
import com.example.todayserver.global.common.exception.CustomException;
import com.example.todayserver.global.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class IcloudCalendarClient implements ExternalCalendarClient {
    private final IcloudIcsEventParser icsEventParser;

    @Qualifier("icloudWebClient")
    private final WebClient webClient;

    public IcloudCalendarClient(
            @Qualifier("icloudWebClient") WebClient webClient,
            IcloudIcsEventParser icsEventParser
    ) {
        this.webClient = webClient;
        this.icsEventParser = icsEventParser;
    }

    @Override
    public ExternalProvider provider() {
        return ExternalProvider.ICLOUD;
    }

    @Override
    public List<ExternalSourceDto> fetchSources(ExternalAccount account) {
        return List.of(
                new ExternalSourceDto(
                        "ICLOUD_ICS_DEFAULT",
                        "iCloud(ICS)",
                        null
                )
        );
    }

    @Override
    public List<ExternalEventDto> fetchEvents(
            ExternalAccount account,
            ExternalSource source,
            LocalDateTime from,
            LocalDateTime to
    ) {
        String icsUrl = extractIcsUrl(account);
        String normalized = normalizeIcsUrl(icsUrl);

        var resp = webClient.get()
                .uri(normalized)
                .accept(MediaType.parseMediaType("text/calendar"))
                .header("User-Agent", "Mozilla/5.0")
                .exchangeToMono(r -> r.toEntity(String.class))
                .block();

        if (resp == null) throw new CustomException(ErrorCode.EXTERNAL_CALENDAR_FETCH_FAILED);

        String contentType = (resp.getHeaders().getContentType() != null)
                ? resp.getHeaders().getContentType().toString()
                : "null";

        String body = resp.getBody();

        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new CustomException(ErrorCode.EXTERNAL_CALENDAR_FETCH_FAILED);
        }
        if (body == null || body.isBlank()) {
            throw new CustomException(ErrorCode.EXTERNAL_CALENDAR_FETCH_FAILED);
        }

        if (!body.contains("BEGIN:VCALENDAR")) {
            throw new CustomException(ErrorCode.EXTERNAL_CALENDAR_FETCH_FAILED);
        }

        return icsEventParser.parse(body, from, to);
    }


    private String extractIcsUrl(ExternalAccount account) {
        if (account == null) {
            throw new CustomException(ErrorCode.EXTERNAL_ACCOUNT_NOT_FOUND);
        }

        String url = account.getIcsUrl();
        if (url == null || url.isBlank()) {
            throw new CustomException(ErrorCode.EXTERNAL_CALENDAR_INVALID_URL);
        }

        return url;
    }

    private String normalizeIcsUrl(String url) {
        if (url == null) {
            throw new CustomException(ErrorCode.EXTERNAL_CALENDAR_INVALID_URL);
        }

        String normalized = url
                .replace("\u200B", "")   // zero-width space
                .replace("\uFEFF", "")   // BOM
                .replaceAll("\\s+", "")  // 모든 공백/개행/탭 제거
                .trim();

        if (normalized.startsWith("webcal://")) {
            normalized = "https://" + normalized.substring("webcal://".length());
        }

        if (!normalized.startsWith("http://") && !normalized.startsWith("https://")) {
            throw new CustomException(ErrorCode.EXTERNAL_CALENDAR_INVALID_URL);
        }

        return normalized;
    }

}
