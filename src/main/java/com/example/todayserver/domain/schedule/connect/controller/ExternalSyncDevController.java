package com.example.todayserver.domain.schedule.connect.controller;

import com.example.todayserver.domain.schedule.connect.dto.NotionCalendarDto;
import com.example.todayserver.domain.schedule.connect.entity.ExternalAccount;
import com.example.todayserver.domain.schedule.connect.enums.ExternalProvider;
import com.example.todayserver.domain.schedule.connect.repository.ExternalAccountRepository;
import com.example.todayserver.domain.schedule.connect.service.ExternalSyncAsyncService;
import com.example.todayserver.domain.schedule.connect.service.notion.NotionCalendarClient;
import com.example.todayserver.global.common.response.ApiResponse;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;

@Slf4j
@Tag(name = "Dev", description = "임시 테스트용 API (배포 전 제거)")
@RestController
@RequestMapping("/api/v1/dev/external")
@RequiredArgsConstructor
public class ExternalSyncDevController {

    private final ExternalSyncAsyncService externalSyncAsyncService;

    @Qualifier("notionWebClient")
    private final WebClient notionWebClient;

    private final ExternalAccountRepository externalAccountRepository;

    /* =========================
       Notion - Raw API Tests
     ========================= */

    @Operation(
            summary = "[DEV] Notion Search(data_source) 원문 호출",
            description = "현재 로그인 사용자의 NOTION ExternalAccount 토큰으로 /v1/search를 호출하고 원문 JSON을 반환합니다."
    )
    @PostMapping("/notion/search/data-source")
    public ApiResponse<JsonNode> searchNotionDataSource(
            @AuthenticationPrincipal(expression = "id") Long memberId,
            @RequestParam(defaultValue = "50") int pageSize
    ) {
        ExternalAccount account = externalAccountRepository
                .findByMemberIdAndProvider(memberId, ExternalProvider.NOTION)
                .orElseThrow(() -> new IllegalArgumentException("NOTION ExternalAccount 없음"));

        try {
            JsonNode res = notionWebClient.post()
                    .uri("/search")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + account.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    // ✅ NotionCalendarClient(정리본) 기준 SearchRequest 사용
                    .bodyValue(NotionCalendarDto.SearchRequest.dataSourcesOnly(pageSize))
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            return ApiResponse.success(res);

        } catch (WebClientResponseException e) {
            // Notion이 내려주는 에러 바디를 그대로 확인 가능하도록
            String body = e.getResponseBodyAsString();
            log.error("[DevNotion][FAIL] /search status={}, body={}", e.getStatusCode(), body);
            throw new IllegalStateException("Notion /search 실패: status=" + e.getStatusCode() + ", body=" + body, e);
        }
    }

    @Operation(
            summary = "[DEV] Notion Database Query 원문 호출",
            description = "databaseId를 받아 /v1/databases/{id}/query를 호출하고 원문 JSON을 반환합니다."
    )
    @PostMapping("/notion/database/{databaseId}/query")
    public ApiResponse<JsonNode> queryNotionDatabaseRaw(
            @AuthenticationPrincipal(expression = "id") Long memberId,
            @PathVariable String databaseId
    ) {
        ExternalAccount account = externalAccountRepository
                .findByMemberIdAndProvider(memberId, ExternalProvider.NOTION)
                .orElseThrow(() -> new IllegalArgumentException("NOTION ExternalAccount 없음"));

        try {
            JsonNode res = notionWebClient.post()
                    .uri(uriBuilder -> uriBuilder.path("/databases/{id}/query").build(databaseId))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + account.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(NotionCalendarDto.DatabaseQueryRequest.empty())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            return ApiResponse.success(res);

        } catch (WebClientResponseException e) {
            String body = e.getResponseBodyAsString();
            log.error("[DevNotion][FAIL] /databases/{}/query status={}, body={}", databaseId, e.getStatusCode(), body);
            throw new IllegalStateException("Notion DB query 실패: status=" + e.getStatusCode() + ", body=" + body, e);
        }
    }

    /* =========================
       Notion - Sync Tests
     ========================= */

    @Operation(
            summary = "[DEV] Notion 월 동기화 강제 실행(로그인 사용자)",
            description = "현재 로그인한 사용자(memberId) 기준으로 Notion 동기화를 비동기 실행합니다. (임시 테스트용)"
    )
    @PostMapping("/notion/sync/month")
    public ApiResponse<String> syncNotionMonthForLoginUser(
            @AuthenticationPrincipal(expression = "id") Long memberId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        LocalDate now = LocalDate.now();
        int y = (year == null) ? now.getYear() : year;
        int m = (month == null) ? now.getMonthValue() : month;

        log.info("[DevSync] request syncMonth memberId={}, provider=NOTION, year={}, month={}", memberId, y, m);

        externalSyncAsyncService.syncMonth(memberId, ExternalProvider.NOTION, y, m);

        return ApiResponse.success("sync requested");
    }

    @Operation(
            summary = "[DEV] Notion 월 동기화 강제 실행(memberId 직접 입력)",
            description = "memberId를 파라미터로 받아 Notion 동기화를 비동기 실행합니다. (임시 테스트용)"
    )
    @PostMapping("/notion/sync/month/{memberId}")
    public ApiResponse<String> syncNotionMonthForMemberId(
            @PathVariable Long memberId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        LocalDate now = LocalDate.now();
        int y = (year == null) ? now.getYear() : year;
        int m = (month == null) ? now.getMonthValue() : month;

        log.info("[DevSync] request syncMonth memberId={}, provider=NOTION, year={}, month={}", memberId, y, m);

        externalSyncAsyncService.syncMonth(memberId, ExternalProvider.NOTION, y, m);

        return ApiResponse.success("sync requested");
    }

    /* =========================
       Google - Existing Sync Tests
     ========================= */

    @Operation(
            summary = "[DEV] 구글 캘린더 월 동기화 강제 실행(로그인 사용자)",
            description = "현재 로그인한 사용자(memberId) 기준으로 Google 동기화를 비동기 실행합니다. (임시 테스트용)"
    )
    @PostMapping("/google/sync/month")
    public ApiResponse<String> syncGoogleMonthForLoginUser(
            @AuthenticationPrincipal(expression = "id") Long memberId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        LocalDate now = LocalDate.now();
        int y = (year == null) ? now.getYear() : year;
        int m = (month == null) ? now.getMonthValue() : month;

        log.info("[DevSync] request syncMonth memberId={}, provider=GOOGLE, year={}, month={}", memberId, y, m);

        externalSyncAsyncService.syncMonth(memberId, ExternalProvider.GOOGLE, y, m);

        return ApiResponse.success("sync requested");
    }

    @Operation(
            summary = "[DEV] 구글 캘린더 월 동기화 강제 실행(memberId 직접 입력)",
            description = "memberId를 파라미터로 받아 Google 동기화를 비동기 실행합니다. (임시 테스트용)"
    )
    @PostMapping("/google/sync/month/{memberId}")
    public ApiResponse<String> syncGoogleMonthForMemberId(
            @PathVariable Long memberId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        LocalDate now = LocalDate.now();
        int y = (year == null) ? now.getYear() : year;
        int m = (month == null) ? now.getMonthValue() : month;

        log.info("[DevSync] request syncMonth memberId={}, provider=GOOGLE, year={}, month={}", memberId, y, m);

        externalSyncAsyncService.syncMonth(memberId, ExternalProvider.GOOGLE, y, m);

        return ApiResponse.success("sync requested");
    }
}
