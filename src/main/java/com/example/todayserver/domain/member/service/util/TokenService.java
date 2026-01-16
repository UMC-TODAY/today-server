package com.example.todayserver.domain.member.service.util;

import com.example.todayserver.domain.member.dto.TokenDto;
import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.member.entity.RefreshToken;
import com.example.todayserver.domain.member.repository.RefreshTokenRepository;
import com.example.todayserver.global.common.jwt.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public TokenDto issueTokens(Member member){
        String accessToken = jwtUtil.createAccessToken(member);
        String refreshToken = jwtUtil.createRefreshToken(member);

        refreshTokenRepository.save(new RefreshToken(refreshToken, member));
        return new TokenDto(accessToken, refreshToken);
    }
}
