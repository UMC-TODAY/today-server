package com.example.todayserver.domain.member.service;

import com.example.todayserver.domain.member.excpetion.AuthException;
import com.example.todayserver.domain.member.excpetion.code.AuthErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public String sendVerificationEmail(String emailTo) {
        String code = generateVerificationCode();
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(emailTo);
            mimeMessageHelper.setSubject("[TO:DAY] 이메일 인증 코드 발송");
            mimeMessageHelper.setText(createHtmlEmailBody(code),true);
            mimeMessageHelper.setFrom(fromEmail);
            javaMailSender.send(mimeMessage);

            return code;
        } catch (MessagingException e) {
            throw new AuthException(AuthErrorCode.CODE_ERROR);
        }
    }

    // 인증번호 생성
    private String generateVerificationCode() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    // 인증 이메일 뷰 생성
    private String createHtmlEmailBody(String code) {
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process("email_code", context);
    }
}