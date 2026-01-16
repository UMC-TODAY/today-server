package com.example.todayserver.global.oauth.info;

import com.example.todayserver.domain.member.enums.SocialType;

public interface OAuth2UserInfo {
    String getProviderId();
    SocialType getProvider();
    String getEmail();
    String getName();
    String getProfileImage();
}
