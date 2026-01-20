package com.example.todayserver.domain.member.service;

import com.example.todayserver.domain.member.converter.MemberConverter;
import com.example.todayserver.domain.member.converter.PreferenceConverter;
import com.example.todayserver.domain.member.dto.MemberReqDto;
import com.example.todayserver.domain.member.dto.MemberResDto;
import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.member.entity.Preference;
import com.example.todayserver.domain.member.enums.SocialType;
import com.example.todayserver.domain.member.excpetion.AuthException;
import com.example.todayserver.domain.member.excpetion.MemberException;
import com.example.todayserver.domain.member.excpetion.code.AuthErrorCode;
import com.example.todayserver.domain.member.excpetion.code.MemberErrorCode;
import com.example.todayserver.domain.member.repository.EmailCodeRepository;
import com.example.todayserver.domain.member.repository.MemberRepository;
import com.example.todayserver.domain.member.repository.PreferenceRepository;
import com.example.todayserver.domain.member.service.util.AwsFileService;
import com.example.todayserver.domain.member.service.util.MemberWithdrawService;
import com.example.todayserver.domain.member.service.util.RandomNicknameGenerator;
import com.example.todayserver.global.common.jwt.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final EmailCodeRepository emailCodeRepository;
    private final RandomNicknameGenerator nicknameGenerator;
    private final PasswordEncoder passwordEncoder;
    private final MemberWithdrawService memberWithdrawService;
    private final JwtUtil jwtUtil;
    private final PreferenceRepository preferenceRepository;
    private final AwsFileService awsFileService;

    @Override
    public void checkEmailDuplicate(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new MemberException(MemberErrorCode.EXIST_EMAIL);
        }
    }

    @Transactional
    @Override
    public void emailSignup(MemberReqDto.SignupDto dto) {
        String email = dto.getEmail();
        checkEmailDuplicate(email);
        if (!emailCodeRepository.existsByEmailAndVerifiedIsTrue(email)) {
            throw new AuthException(AuthErrorCode.INVALID_EMAIL);
        }
        memberWithdrawService.checkWithdraw(email);

        while (true) {
            String nickname = nicknameGenerator.generate();
            String salt = passwordEncoder.encode(dto.getPassword());
            try {
                Member member = MemberConverter.toMember(dto, salt, nickname);
                memberRepository.save(member);
                preferenceRepository.findByMemberId(member.getId())
                    .orElseGet(() ->
                            preferenceRepository.save(
                                    PreferenceConverter.newPreference(member)
                            )
                );
                return;
            } catch (DataIntegrityViolationException e) {
                //재시도
            }
        }
    }

    @Override
    public Member emailLogin(MemberReqDto.LoginDto dto) {
        Member member = getMemberByEmail(dto.getEmail());

        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new MemberException(MemberErrorCode.INVALID_PW);
        }

        return member;
    }

    @Override
    public void checkNicknameDuplicate(String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            throw new MemberException(MemberErrorCode.DUPLICATE_NICKNAME);
        }
    }

    @Override
    public MemberResDto.MemberInfo getMemberInfo(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));
        return MemberConverter.toMemberInfo(member);
    }

    @Override
    public MemberResDto.MemberInfo getMyInfo(String token) {
        String email = getEmailByAccessToken(token);
        Member member = getMemberByEmail(email);
        return MemberConverter.toMemberInfo(member);
    }

    @Transactional
    @Override
    public void withdraw(String token) {
        String email = getEmailByAccessToken(token);
        Member member = getMemberByEmail(email);
        memberRepository.delete(member);
    }

    @Transactional
    @Override
    public void updatePassword(String password, String token) {
        String email = getEmailByAccessToken(token);
        Member member = getMemberByEmail(email);
        if (!member.getSocialType().equals(SocialType.EMAIL)){
            throw new MemberException(MemberErrorCode.NO_PASSWORD);
        }
        memberRepository.updatePassword(password, member.getId());
    }

    @Transactional
    @Override
    public void updateProfile(String token, MemberReqDto.ProfileInfo dto) {
        String email = getEmailByAccessToken(token);
        Member member = getMemberByEmail(email);

        MultipartFile profileImage = dto.getProfileImage();
        String nickname = dto.getNickName();


        try {
            String imageUrl = awsFileService.saveProfileImg(profileImage, member.getId());
            memberRepository.updateProfile(imageUrl, nickname, member.getId());
        } catch (IOException e) {
            throw new MemberException(MemberErrorCode.IMAGE_UPLOAD_FAIL);
        }
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
