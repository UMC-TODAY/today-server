package com.example.todayserver.domain.member.service.util;

import com.example.todayserver.domain.member.entity.EmailVerification;
import com.example.todayserver.domain.member.repository.EmailCodeRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final EmailCodeRepository emailCodeRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendVerifyCode(String emailTo, MimeMessage mimeMessage, String code, String type) throws MessagingException {
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
        mimeMessageHelper.setTo(emailTo);
        mimeMessageHelper.setSubject("[TO:DAY] 이메일 인증 코드 발송");
        mimeMessageHelper.setText(createHtmlEmailBody(code, type),true);
        mimeMessageHelper.setFrom(fromEmail);
        javaMailSender.send(mimeMessage);
    }

    @Transactional
    public void saveEmailVerifyCode(String emailTo, String code) {
        emailCodeRepository.deleteByEmail(emailTo);
        EmailVerification ev = EmailVerification.builder()
                .email(emailTo)
                .code(code)
                .expireDate(LocalDateTime.now().plusMinutes(10))
                .build();
        emailCodeRepository.save(ev);
    }

    // 인증번호 생성
    public String generateVerificationCode() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    // 인증 이메일 뷰 생성
    public String createHtmlEmailBody(String code, String type) {
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process(type, context);
    }
}
