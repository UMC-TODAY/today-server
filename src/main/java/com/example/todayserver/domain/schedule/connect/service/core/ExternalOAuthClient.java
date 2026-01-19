package com.example.todayserver.domain.schedule.connect.service.core;

public interface ExternalOAuthClient {
    String buildAuthorizeUrl(String state);
    Object exchangeCodeForToken(String code);
}
