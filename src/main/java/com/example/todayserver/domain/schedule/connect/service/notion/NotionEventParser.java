package com.example.todayserver.domain.schedule.connect.service.notion;

import com.example.todayserver.domain.schedule.connect.dto.ExternalEventDto;
import com.example.todayserver.domain.schedule.connect.dto.ExternalSourceDto;
import com.example.todayserver.domain.schedule.connect.dto.NotionCalendarDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Notion API 응답(Json)을 내부 DTO로 변환하는 파서
@Component
@RequiredArgsConstructor
public class NotionEventParser {

    // Notion DB 기본 템플릿 기준 매핑 (초기 연동 시 사용)
    private static final String DEFAULT_META_JSON = "{\"titleProp\":\"이름\",\"dateProp\":\"날짜\"}";

    private final ObjectMapper objectMapper;

    // Notion search API 결과에서 연동 가능한 데이터 소스(Database) 목록을 추출
    public List<ExternalSourceDto> parseSources(JsonNode res) {
        if (!hasResultsArray(res)) return List.of();

        List<ExternalSourceDto> out = new ArrayList<>();
        for (JsonNode item : res.get("results")) {
            String object = item.path("object").asText();
            if (!"data_source".equals(object) && !"database".equals(object)) continue;

            String id = item.path("id").asText();
            String title = extractRichTextTitle(item.path("title")).orElse("(Untitled)");

            out.add(new ExternalSourceDto(id, title, DEFAULT_META_JSON));
        }
        return out;
    }

    // Notion database query 결과에서 일정(Page)을 ExternalEventDto 목록으로 변환한다.
    public List<ExternalEventDto> parseEvents(
            JsonNode res,
            String metaJson,
            LocalDateTime from,
            LocalDateTime to
    ) {
        if (!hasResultsArray(res)) return List.of();

        NotionCalendarDto.Mapping mapping =
                NotionCalendarDto.Mapping.fromMetaJson(metaJson, objectMapper);

        List<ExternalEventDto> out = new ArrayList<>();
        for (JsonNode page : res.get("results")) {
            if (!"page".equals(page.path("object").asText())) continue;

            JsonNode props = page.path("properties");

            String title = extractTitleFromProperties(props, mapping.titleProp())
                    .orElse("(제목 없음)");

            Optional<NotionCalendarDto.DateRange> drOpt =
                    extractDateRangeFromProperties(props, mapping.dateProp());
            if (drOpt.isEmpty() || drOpt.get().start() == null) continue;

            NotionCalendarDto.DateRange dr = drOpt.get();
            LocalDateTime start = dr.start();
            LocalDateTime end = (dr.end() != null) ? dr.end() : dr.start();

            // 요청한 월 범위 밖 이벤트 제외
            if (!isInRange(start, end, from, to)) continue;

            String pageId = page.path("id").asText();
            String lastEdited = page.path("last_edited_time").asText(null);

            out.add(new ExternalEventDto(
                    pageId,
                    title,
                    null,
                    start,
                    end,
                    dr.allDay(),
                    parseNotionDateTime(lastEdited),
                    lastEdited,
                    page.toString()
            ));
        }

        return out;
    }

     // Notion 응답에 results 배열이 존재하는지 확인
    private boolean hasResultsArray(JsonNode res) {
        return res != null && res.has("results") && res.get("results").isArray();
    }

     // 이벤트가 요청한 기간(from~to)에 포함되는지 판단
    private boolean isInRange(LocalDateTime start, LocalDateTime end, LocalDateTime from, LocalDateTime to) {
        return !(start.isAfter(to) || end.isBefore(from));
    }

     // Notion title 배열에서 첫 번째 plain_text 값을 추출
    private Optional<String> extractRichTextTitle(JsonNode titleArray) {
        if (titleArray == null || !titleArray.isArray() || titleArray.isEmpty()) return Optional.empty();
        return Optional.ofNullable(titleArray.get(0).path("plain_text").asText(null));
    }


     // properties에서 title 타입 속성 값을 추출
    private Optional<String> extractTitleFromProperties(JsonNode props, String titlePropName) {
        if (props == null || titlePropName == null) return Optional.empty();

        JsonNode p = props.path(titlePropName);
        if (p.isMissingNode() || p.isNull()) return Optional.empty();

        JsonNode titleArr = p.path("title");
        if (!titleArr.isArray() || titleArr.isEmpty()) return Optional.empty();

        return Optional.ofNullable(titleArr.get(0).path("plain_text").asText(null));
    }

     // properties에서 date 타입 속성을 추출해 DateRange로 변환
     private Optional<NotionCalendarDto.DateRange> extractDateRangeFromProperties(
            JsonNode props,
            String datePropName
    ) {
        if (props == null || datePropName == null) return Optional.empty();

        JsonNode p = props.path(datePropName);
        if (p.isMissingNode() || p.isNull()) return Optional.empty();

        JsonNode date = p.path("date");
        if (date.isMissingNode() || date.isNull()) return Optional.empty();

        String startStr = date.path("start").asText(null);
        String endStr = date.path("end").asText(null);

        LocalDateTime start = parseNotionDateTime(startStr);
        LocalDateTime end = parseNotionDateTime(endStr);

        boolean allDay = isAllDayFormat(startStr, endStr);
        return Optional.of(new NotionCalendarDto.DateRange(start, end, allDay));
    }

    // 날짜 문자열이 YYYY-MM-DD 형식인지 판단하여 종일 일정 여부를 계산
    private boolean isAllDayFormat(String startStr, String endStr) {
        return (startStr != null && startStr.length() == 10)
                && (endStr == null || endStr.length() == 10);
    }

    // Notion 날짜 문자열을 LocalDateTime으로 변환
    private LocalDateTime parseNotionDateTime(String iso) {
        if (iso == null || iso.isBlank()) return null;

        if (iso.length() == 10) {
            LocalDate d = LocalDate.parse(iso);
            return d.atStartOfDay();
        }

        return OffsetDateTime.parse(iso).toLocalDateTime();
    }
}
