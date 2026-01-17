package com.example.todayserver.global.oauth;

import com.example.todayserver.domain.member.converter.MemberConverter;
import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.member.enums.SocialType;
import com.example.todayserver.domain.member.excpetion.AuthException;
import com.example.todayserver.domain.member.excpetion.MemberException;
import com.example.todayserver.domain.member.excpetion.code.AuthErrorCode;
import com.example.todayserver.domain.member.excpetion.code.MemberErrorCode;
import com.example.todayserver.domain.member.repository.MemberRepository;
import com.example.todayserver.global.oauth.info.GoogleUserInfo;
import com.example.todayserver.global.oauth.info.OAuth2UserInfo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class OAuth2UserCustomService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);
        Map<String, Object> attributes = user.getAttributes();

        OAuth2UserInfo userInfo = null;

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        SocialType provider = SocialType.valueOf(registrationId.toUpperCase());

        if (provider.equals(SocialType.GOOGLE)){
            userInfo = new GoogleUserInfo(attributes);
        }

        if (userInfo == null) {
            throw new AuthException(AuthErrorCode.INVALID_SOCIAL_TYPE);
        }

        Member member = saveOrUpdate(userInfo);
        return new DefaultOAuth2User(
                Collections.emptySet(),
                user.getAttributes(),
                "sub"
        );
    }

    private Member saveOrUpdate(OAuth2UserInfo userInfo){
        String email = (String) userInfo.getEmail();

        Member member = memberRepository.findByEmail(email)
                .map(m -> {
                    if (!m.getSocialType().equals(userInfo.getProvider())) {
                        throw new MemberException(MemberErrorCode.DUPLICATE_SOCIAL);
                    }
                    m.updateFromOAuth(userInfo);
                    return m;
                })
                .orElse(
                        MemberConverter.toOAuthMember(email, userInfo)
                );
        return memberRepository.save(member);
    }
}
