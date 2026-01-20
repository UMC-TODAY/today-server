package com.example.todayserver.domain.member.service;

import com.example.todayserver.domain.member.converter.PreferenceConverter;
import com.example.todayserver.domain.member.dto.PreferenceDto;
import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.member.entity.Preference;
import com.example.todayserver.domain.member.excpetion.MemberException;
import com.example.todayserver.domain.member.excpetion.PreferenceException;
import com.example.todayserver.domain.member.excpetion.code.MemberErrorCode;
import com.example.todayserver.domain.member.excpetion.code.PreferenceErrorCode;
import com.example.todayserver.domain.member.repository.MemberRepository;
import com.example.todayserver.domain.member.repository.PreferenceRepository;
import com.example.todayserver.global.common.jwt.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PreferenceService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final PreferenceRepository preferenceRepository;

    public PreferenceDto.Notification getNotifications(String token){
        String email = getEmailByAccessToken(token);
        Member member = getMemberByEmail(email);
        Preference preference = preferenceRepository.findByMemberId(member.getId())
                .orElseThrow(() -> new PreferenceException(PreferenceErrorCode.NOT_FOUND));
        return PreferenceConverter.toNotification(preference);
    }

    @Transactional
    public void updateNotifications(String token, PreferenceDto.Notification dto){
        String email = getEmailByAccessToken(token);
        Member member = getMemberByEmail(email);
        preferenceRepository.updateNotifications(dto.getEmailAlert(), dto.getKakaoAlert(), dto.getReminderAlert(), member);
    }

    public PreferenceDto.Info getInfo(String token){
        String email = getEmailByAccessToken(token);
        Member member = getMemberByEmail(email);
        Preference preference = preferenceRepository.findByMemberId(member.getId())
                .orElseThrow(() -> new PreferenceException(PreferenceErrorCode.NOT_FOUND));
        return PreferenceConverter.toInfo(preference);
    }

    private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));
    }

    private String getEmailByAccessToken(String token) {
        String accessToken = token.split(" ")[1];
        return jwtUtil.getEmail(accessToken);
    }
}
