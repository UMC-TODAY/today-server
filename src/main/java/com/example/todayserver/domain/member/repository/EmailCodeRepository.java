package com.example.todayserver.domain.member.repository;

import com.example.todayserver.domain.member.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailCodeRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findByEmailAndCode(
            String email, String verificationCode);
    boolean existsByEmailAndVerifiedIsTrue(String email);
    void deleteByEmail(String email);
}