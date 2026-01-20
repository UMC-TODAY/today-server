package com.example.todayserver.global.config;

import com.example.todayserver.domain.schedule.connect.config.NotionOAuthProperties;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(3);
    private static final Duration RESPONSE_TIMEOUT = Duration.ofSeconds(10);

    private final NotionOAuthProperties notionProperties;

    @Bean
    public WebClient oauthWebClient() {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient()))
                .build();
    }

    @Bean
    public WebClient googleCalendarWebClient() {
        return WebClient.builder()
                .baseUrl("https://www.googleapis.com/calendar/v3")
                .clientConnector(new ReactorClientHttpConnector(httpClient()))
                .build();
    }

    /**
     * Notion API 전용 WebClient
     * - baseUrl: https://api.notion.com/v1
     */
    @Bean
    public WebClient notionWebClient() {
        return WebClient.builder()
                .baseUrl(notionProperties.getApiBase())
                .defaultHeader("Notion-Version", notionProperties.getVersion())
                .build();
    }

    private HttpClient httpClient() {
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) CONNECT_TIMEOUT.toMillis())
                .responseTimeout(RESPONSE_TIMEOUT)
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler((int) RESPONSE_TIMEOUT.toSeconds(), TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler((int) RESPONSE_TIMEOUT.toSeconds(), TimeUnit.SECONDS))
                );
    }
}
