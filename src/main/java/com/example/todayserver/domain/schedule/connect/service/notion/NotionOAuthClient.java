package com.example.todayserver.domain.schedule.connect.service.notion;

import com.example.todayserver.domain.schedule.connect.config.NotionOAuthProperties;
import com.example.todayserver.domain.schedule.connect.dto.NotionTokenRes;
import com.example.todayserver.domain.schedule.connect.service.core.ExternalOAuthClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import java.util.Base64;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotionOAuthClient implements ExternalOAuthClient {

    private final NotionOAuthProperties props;

    @Qualifier("notionWebClient")
    private final WebClient notionWebClient;

    /**
     * Notion OAuth 인가 URL 생성
     * https://api.notion.com/v1/oauth/authorize
     */
    @Override
    public String buildAuthorizeUrl(String state) {
        return UriComponentsBuilder
                .fromHttpUrl("https://api.notion.com/v1/oauth/authorize")
                .queryParam("client_id", props.getClientId())
                .queryParam("response_type", "code")
                .queryParam("owner", "user")
                .queryParam("redirect_uri", props.getRedirectUri())
                .queryParam("state", state)
                .build(true)
                .toUriString();
    }

    /**
     * authorization code → access token 교환
     * POST https://api.notion.com/v1/oauth/token
     */
    @Override
    public NotionTokenRes exchangeCodeForToken(String code) {
        String basicAuth = Base64.getEncoder().encodeToString(
                (props.getClientId() + ":" + props.getClientSecret())
                        .getBytes(StandardCharsets.UTF_8)
        );

        return notionWebClient.post()
                .uri("/oauth/token")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + basicAuth)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "grant_type", "authorization_code",
                        "code", code,
                        "redirect_uri", props.getRedirectUri()
                ))
                .retrieve()
                .bodyToMono(NotionTokenRes.class)
                .block();
    }
}
