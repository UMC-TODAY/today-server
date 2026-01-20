package com.example.todayserver.domain.schedule.connect.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

public final class NotionCalendarDto {

    private NotionCalendarDto() {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SearchRequest(Filter filter, Integer page_size, String query, Sort sort) {

        public static SearchRequest dataSourcesOnly(int pageSize) {
            return new SearchRequest(new Filter("object", "data_source"), pageSize, null, null);
        }

        public record Filter(String property, String value) {}
        public record Sort(String direction, String timestamp) {}
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record DatabaseQueryRequest(Object filter, List<Object> sorts, Integer page_size, String start_cursor) {
        public static DatabaseQueryRequest empty() {
            return new DatabaseQueryRequest(null, null, null, null);
        }
    }

    public record Mapping(String titleProp, String dateProp) {

        public static Mapping fromMetaJson(String metaJson, ObjectMapper om) {
            String title = "Name";
            String date = "Date";

            if (metaJson == null || metaJson.isBlank()) return new Mapping(title, date);

            try {
                JsonNode n = om.readTree(metaJson);
                String t = n.path("titleProp").asText(null);
                String d = n.path("dateProp").asText(null);

                if (t != null && !t.isBlank()) title = t;
                if (d != null && !d.isBlank()) date = d;

            } catch (Exception ignored) {
                // metaJson 파싱 실패해도 기본값으로 동작
            }

            return new Mapping(title, date);
        }
    }

    public record DateRange(LocalDateTime start, LocalDateTime end, boolean allDay) {}
}
