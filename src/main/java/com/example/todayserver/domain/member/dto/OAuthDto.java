package com.example.todayserver.domain.member.dto;

import com.example.todayserver.global.oauth.info.OAuth2UserInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class OAuthDto {

    @Getter
    @AllArgsConstructor
    public static class ServiceDto {
        OAuth2UserInfo userInfo;
        String nameAttributeKey;
    }
}
