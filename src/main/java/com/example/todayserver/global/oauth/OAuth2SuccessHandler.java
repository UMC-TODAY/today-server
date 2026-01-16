package com.example.todayserver.global.oauth;

import com.example.todayserver.domain.member.dto.TokenDto;
import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.member.excpetion.MemberException;
import com.example.todayserver.domain.member.excpetion.code.MemberErrorCode;
import com.example.todayserver.domain.member.repository.MemberRepository;
import com.example.todayserver.domain.member.service.util.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final TokenService tokenService;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Member member = memberRepository.findByEmail((String) oAuth2User.getAttributes().get("email"))
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        TokenDto tokenDto = tokenService.issueTokens(member);

        response.sendRedirect(
                "http://localhost:3000/oauth/success?token=" + tokenDto.getAccessToken()
        );
    }
}
