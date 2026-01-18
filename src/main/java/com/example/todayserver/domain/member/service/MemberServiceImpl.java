package com.example.todayserver.domain.member.service;

import com.example.todayserver.domain.member.converter.MemberConverter;
import com.example.todayserver.domain.member.dto.MemberReqDto;
import com.example.todayserver.domain.member.dto.MemberResDto;
import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.member.excpetion.AuthException;
import com.example.todayserver.domain.member.excpetion.MemberException;
import com.example.todayserver.domain.member.excpetion.code.AuthErrorCode;
import com.example.todayserver.domain.member.excpetion.code.MemberErrorCode;
import com.example.todayserver.domain.member.repository.EmailCodeRepository;
import com.example.todayserver.domain.member.repository.MemberRepository;
import com.example.todayserver.domain.member.service.util.RandomNicknameGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final EmailCodeRepository emailCodeRepository;
    private final RandomNicknameGenerator nicknameGenerator;
    private final PasswordEncoder passwordEncoder;

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
        while (true) {
            String nickname = nicknameGenerator.generate();
            String salt = passwordEncoder.encode(dto.getPassword());
            try {
                Member member = MemberConverter.toMember(dto, salt, nickname);
                memberRepository.save(member);
                return;
            } catch (DataIntegrityViolationException e) {
                //재시도
            }
        }
    }

    @Override
    public Member emailLogin(MemberReqDto.LoginDto dto) {
        Member member = memberRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

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
}
