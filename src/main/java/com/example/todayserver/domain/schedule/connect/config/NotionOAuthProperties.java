package com.example.todayserver.domain.schedule.connect.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "external.notion")
public class NotionOAuthProperties {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String version;
    private String apiBase;
}
