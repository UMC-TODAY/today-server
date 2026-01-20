package com.example.todayserver.domain.schedule.connect.service.google;

import com.example.todayserver.domain.schedule.connect.config.GoogleOAuthProperties;
import com.example.todayserver.domain.schedule.connect.dto.GoogleTokenRes;
import com.example.todayserver.domain.schedule.connect.service.core.ExternalOAuthClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDateTime;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class GoogleOAuthClient implements ExternalOAuthClient {

    private static final String AUTH_BASE_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";

    private final GoogleOAuthProperties properties;
    private final WebClient webClient;

    public GoogleOAuthClient(
            GoogleOAuthProperties properties,
            @Qualifier("oauthWebClient") WebClient webClient
    ) {
        this.properties = properties;
        this.webClient = webClient;
    }

    @Override
    public String buildAuthorizeUrl(String state) {
        URI uri = UriComponentsBuilder.fromHttpUrl(AUTH_BASE_URL)
                .queryParam("client_id", properties.getClientId())
                .queryParam("redirect_uri", properties.getRedirectUri())
                .queryParam("response_type", "code")
                .queryParam("scope", properties.getScope())
                .queryParam("access_type", "offline")
                .queryParam("include_granted_scopes", "true")
                .queryParam("prompt", "consent")
                .queryParam("state", state)
                .build()
                .toUri();

        return uri.toString();
    }

    @Override
    public GoogleTokenRes exchangeCodeForToken(String code) {
        return webClient.post()
                .uri(TOKEN_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("code", code)
                        .with("client_id", properties.getClientId())
                        .with("client_secret", properties.getClientSecret())
                        .with("redirect_uri", properties.getRedirectUri())
                        .with("grant_type", "authorization_code")
                )
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp ->
                        resp.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(errorBody -> Mono.error(
                                        new com.example.todayserver.global.common.exception.CustomException(
                                                com.example.todayserver.global.common.exception.ErrorCode.EXTERNAL_API_ERROR
                                        )
                                ))
                )
                .bodyToMono(GoogleTokenRes.class)
                .block();
    }

    public LocalDateTime calcExpiresAt(Long expiresInSeconds) {
        return expiresInSeconds == null
                ? null
                : LocalDateTime.now().plusSeconds(expiresInSeconds);
    }
}
