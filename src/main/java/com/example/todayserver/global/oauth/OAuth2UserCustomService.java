    package com.example.todayserver.global.oauth;

    import com.example.todayserver.domain.member.converter.MemberConverter;
    import com.example.todayserver.domain.member.converter.PreferenceConverter;
    import com.example.todayserver.domain.member.dto.OAuthDto;
    import com.example.todayserver.domain.member.entity.Member;
    import com.example.todayserver.domain.member.entity.Preference;
    import com.example.todayserver.domain.member.enums.Status;
    import com.example.todayserver.domain.member.excpetion.MemberException;
    import com.example.todayserver.domain.member.excpetion.code.MemberErrorCode;
    import com.example.todayserver.domain.member.repository.MemberRepository;
    import com.example.todayserver.domain.member.repository.PreferenceRepository;
    import com.example.todayserver.domain.member.service.util.MemberWithdrawService;
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
        private final MemberWithdrawService memberWithdrawService;
        private final PreferenceRepository preferenceRepository;

        @Transactional
        @Override
        public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
            OAuth2User user = super.loadUser(userRequest);
            Map<String, Object> attributes = user.getAttributes();
            String registrationId = userRequest.getClientRegistration().getRegistrationId();

            OAuthDto.ServiceDto oAuthDto = OAuth2UserInfoFactory.getOAuthUserInfoFromService(registrationId, attributes);

            Member member = saveOrUpdate(oAuthDto.getUserInfo());

            return new DefaultOAuth2User(
                    Collections.emptySet(),
                    oAuthDto.getUserInfo().getAttributes(),
                    oAuthDto.getNameAttributeKey()
            );
        }

        private Member saveOrUpdate(OAuth2UserInfo userInfo) {
            memberWithdrawService.checkWithdraw(userInfo.getEmail());
            Member member = memberRepository.findBySocialTypeAndProviderUserIdAndStatus(
                            userInfo.getProvider(),
                            userInfo.getProviderId(),
                            Status.ACTIVATE
                    )

                    .map(m -> {
                        if (!m.getSocialType().equals(userInfo.getProvider())) {
                            throw new MemberException(MemberErrorCode.DUPLICATE_SOCIAL);
                        }
                        m.updateFromOAuth(userInfo);
                        return m;
                    })
                    .orElse(
                            MemberConverter.toOAuthMember(userInfo)
                    );
            memberRepository.save(member);
            preferenceRepository.findByMemberId(member.getId())
                .orElseGet(() ->
                        preferenceRepository.save(
                                PreferenceConverter.newPreference(member)
                        )
            );
            return member;
        }
    }
