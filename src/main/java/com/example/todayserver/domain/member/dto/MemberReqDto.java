package com.example.todayserver.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

public class MemberReqDto {
    @Data
    public static class SignupDto{
        @NotBlank
        @Email
        private String email;

        @NotBlank
        private String password;

        @NotNull
        private LocalDate birth;
    }

    @Data
    public static class LoginDto{
        @NotBlank
        @Email
        private String email;

        @NotBlank
        private String password;
    }
}
