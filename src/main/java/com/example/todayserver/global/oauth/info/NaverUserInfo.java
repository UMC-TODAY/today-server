package com.example.todayserver.global.oauth.info;

import com.example.todayserver.domain.member.enums.SocialType;
import com.example.todayserver.domain.member.service.util.RandomNicknameGenerator;

import java.time.LocalDate;
import java.util.Map;

public class NaverUserInfo implements OAuth2UserInfo{
    private final Map<String, Object> attributes;
    private final RandomNicknameGenerator randomNicknameGenerator = new RandomNicknameGenerator();

    public NaverUserInfo(Map<String, Object> attributes){
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public SocialType getProvider() {
        return SocialType.NAVER;
    }

    @Override
    public String getEmail() {
        Object email = attributes.get("email");
        if (email == null || !email.toString().contains("naver")) {
            return getProviderId() + "@naver.com";
        }
        return String.valueOf(email);
    }

    @Override
    public String getName() {
        return String.valueOf(randomNicknameGenerator.generate());
    }

    @Override
    public String getProfileImage() {
        return String.valueOf(attributes.get("profile_image"));
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

}
