package com.example.todayserver.domain.schedule.connect.service.notion;

import com.example.todayserver.domain.schedule.connect.dto.ExternalEventDto;
import com.example.todayserver.domain.schedule.connect.dto.ExternalSourceDto;
import com.example.todayserver.domain.schedule.connect.dto.NotionCalendarDto;
import com.example.todayserver.domain.schedule.connect.entity.ExternalAccount;
import com.example.todayserver.domain.schedule.connect.entity.ExternalSource;
import com.example.todayserver.domain.schedule.connect.enums.ExternalProvider;
import com.example.todayserver.domain.schedule.connect.service.core.ExternalCalendarClient;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotionCalendarClient implements ExternalCalendarClient {

    private static final String SEARCH_URI = "/search";
    private static final String QUERY_URI = "/data_sources/{id}/query";
    private static final int SEARCH_PAGE_SIZE = 50;

    @Qualifier("notionWebClient")
    private final WebClient notionWebClient;

    private final NotionEventParser notionEventParser;

    @Override
    public ExternalProvider provider() {
        return ExternalProvider.NOTION;
    }

    // Notion Search API를 호출하여 사용자가 접근 가능한 데이터소스(Database) 목록을 조회
    @Override
    public List<ExternalSourceDto> fetchSources(ExternalAccount account) {
        Long memberId = account.getMember().getId();

        try {
            JsonNode res = notionWebClient.post()
                    .uri(SEARCH_URI)
                    .header(HttpHeaders.AUTHORIZATION, bearer(account.getAccessToken()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(NotionCalendarDto.SearchRequest.dataSourcesOnly(SEARCH_PAGE_SIZE))
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            List<ExternalSourceDto> out = notionEventParser.parseSources(res);

            log.info("[Notion][FETCH_SOURCES][SUCCESS] memberId={}, sources={}", memberId, out.size());
            return out;

        } catch (WebClientResponseException e) {
            log.error("[Notion][FETCH_SOURCES][FAIL] memberId={}, status={}, body={}",
                    memberId, e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new IllegalStateException("Notion /search 실패: " + e.getResponseBodyAsString(), e);

        } catch (Exception e) {
            log.error("[Notion][FETCH_SOURCES][FAIL] memberId={}, unexpectedError={}",
                    memberId, e.toString(), e);
            throw e;
        }
    }

    // Notion Database Query API를 호출하여 특정 데이터소스(ExternalSource)의 페이지들을 조회
    @Override
    public List<ExternalEventDto> fetchEvents(ExternalAccount account, ExternalSource source, LocalDateTime from, LocalDateTime to) {
        Long memberId = account.getMember().getId();
        String sourceId = source.getSourceKey();

        try {
            JsonNode res = notionWebClient.post()
                    .uri(uriBuilder -> uriBuilder.path(QUERY_URI).build(sourceId))
                    .header(HttpHeaders.AUTHORIZATION, bearer(account.getAccessToken()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(NotionCalendarDto.DatabaseQueryRequest.empty())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            List<ExternalEventDto> out = notionEventParser.parseEvents(res, source.getMetaJson(), from, to);

            log.info("[Notion][FETCH_EVENTS][SUCCESS] memberId={}, sourceId={}, events={}, range={}~{}",
                    memberId, sourceId, out.size(), from, to);
            return out;

        } catch (WebClientResponseException e) {
            log.error("[Notion][FETCH_EVENTS][FAIL] memberId={}, sourceId={}, status={}, body={}",
                    memberId, sourceId, e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new IllegalStateException("Notion database query 실패: body=" + e.getResponseBodyAsString(), e);

        } catch (Exception e) {
            log.error("[Notion][FETCH_EVENTS][FAIL] memberId={}, sourceId={}, unexpectedError={}",
                    memberId, sourceId, e.toString(), e);
            throw e;
        }
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
