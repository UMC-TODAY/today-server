package com.example.todayserver.domain.member.service.util;

import com.example.todayserver.domain.member.dto.TokenDto;
import com.example.todayserver.domain.member.dto.TokenReissueDto;
import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.member.entity.RefreshToken;
import com.example.todayserver.domain.member.excpetion.AuthException;
import com.example.todayserver.domain.member.excpetion.code.AuthErrorCode;
import com.example.todayserver.domain.member.repository.MemberRepository;
import com.example.todayserver.domain.member.repository.RefreshTokenRepository;
import com.example.todayserver.global.common.jwt.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public TokenDto issueTokens(Member member){
        String accessToken = jwtUtil.createAccessToken(member);
        String refreshTokenValue = jwtUtil.createRefreshToken(member);
        RefreshToken refreshToken = refreshTokenRepository.findByMemberId(member.getId())
                .map(rt -> {
                    rt.update(refreshTokenValue, LocalDateTime.now().plusDays(1));
                    return rt;
                })
                .orElse(
                        new RefreshToken(refreshTokenValue, member, LocalDateTime.now().plusDays(1))
                );


        refreshTokenRepository.save(refreshToken);

        return new TokenDto(accessToken, refreshTokenValue);
    }

    @Transactional
    public TokenDto reissueTokens(TokenReissueDto dto){
        String refreshTokenValue = dto.getRefreshToken();
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(refreshTokenValue)
                        .orElseThrow(() -> new AuthException(AuthErrorCode.TOKEN_NOT_FOUND));

        if (refreshToken.isExpired()){
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }

        Member member = refreshToken.getMember();

        String newRefreshTokenValue= jwtUtil.createRefreshToken(member);
        String accessToken = jwtUtil.createAccessToken(member);

        refreshToken.update(newRefreshTokenValue, LocalDateTime.now().plusDays(1));

        return new TokenDto(accessToken, newRefreshTokenValue);
    }
}
