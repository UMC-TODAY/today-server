package com.example.todayserver.global.oauth.info;

import com.example.todayserver.domain.member.enums.SocialType;
import com.example.todayserver.domain.member.service.util.RandomNicknameGenerator;

import java.util.Map;

public class GoogleUserInfo implements OAuth2UserInfo{
    private final Map<String, Object> attributes;
    private final RandomNicknameGenerator randomNicknameGenerator = new RandomNicknameGenerator();

    public GoogleUserInfo(Map<String, Object> attributes){
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("sub"));
    }

    @Override
    public SocialType getProvider() {
        return SocialType.GOOGLE;
    }

    @Override
    public String getEmail() {
        return String.valueOf(attributes.get("email"));
    }

    @Override
    public String getName() {
        return randomNicknameGenerator.generate();
    }

    @Override
    public String getProfileImage() {
        return String.valueOf(attributes.get("picture"));
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
