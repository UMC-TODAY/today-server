package com.example.todayserver.global.oauth;

import com.example.todayserver.domain.member.dto.OAuthDto;
import com.example.todayserver.domain.member.enums.SocialType;
import com.example.todayserver.domain.member.excpetion.AuthException;
import com.example.todayserver.domain.member.excpetion.code.AuthErrorCode;
import com.example.todayserver.global.oauth.info.GoogleUserInfo;
import com.example.todayserver.global.oauth.info.NaverUserInfo;
import com.example.todayserver.global.oauth.info.OAuth2UserInfo;
import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuthDto.ServiceDto getOAuthUserInfoFromService(String registrationId, Map<String, Object> attributes){
        OAuth2UserInfo userInfo = null;
        String nameAttributeKey = null;
        SocialType socialType = toSocialType(registrationId);

        if (socialType.equals(SocialType.GOOGLE)){
            userInfo = new GoogleUserInfo(attributes);
            nameAttributeKey = "sub";
        } else if (socialType.equals(SocialType.NAVER)){
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            userInfo = new NaverUserInfo(response);
            nameAttributeKey = "id";
        } else {
            throw new AuthException(AuthErrorCode.INVALID_SOCIAL_TYPE);
        }

        return new OAuthDto.ServiceDto(userInfo, nameAttributeKey);
    }

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        return switch (toSocialType(registrationId)) {
            case GOOGLE -> new GoogleUserInfo(attributes);
            case NAVER -> new NaverUserInfo(attributes);
            default -> throw new AuthException(AuthErrorCode.INVALID_SOCIAL_TYPE);
        };
    }

    private static SocialType toSocialType(String registrationId){
        return SocialType.valueOf(registrationId.toUpperCase());
    }
}

