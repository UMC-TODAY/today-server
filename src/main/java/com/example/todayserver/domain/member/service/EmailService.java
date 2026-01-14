package com.example.todayserver.domain.member.service;

import com.example.todayserver.domain.member.entity.EmailVerification;
import com.example.todayserver.domain.member.excpetion.AuthException;
import com.example.todayserver.domain.member.excpetion.code.AuthErrorCode;
import com.example.todayserver.domain.member.repository.EmailCodeRepository;
import com.example.todayserver.domain.member.service.util.EmailVerificationService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final EmailCodeRepository emailCodeRepository;
    private final EmailVerificationService emailVerificationService;

    public void sendVerificationEmail(String emailTo, String type) {
        String code = emailVerificationService.generateVerificationCode();
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            emailVerificationService.sendVerifyCode(emailTo, mimeMessage, code, type);
            emailVerificationService.saveEmailVerifyCode(emailTo, code);
        } catch (MessagingException e) {
            throw new AuthException(AuthErrorCode.CODE_ERROR);
        }
    }

    public void checkEmailVerifyCode(String email, String code){
        EmailVerification ev = emailCodeRepository.findByEmailAndCode(email, code)
                .orElseThrow(() -> new AuthException(AuthErrorCode.CODE_NOT_EXIST));

        if (ev.isExpired()){
            throw new AuthException(AuthErrorCode.CODE_EXPIRED);
        }
        ev.verify();
    }
}