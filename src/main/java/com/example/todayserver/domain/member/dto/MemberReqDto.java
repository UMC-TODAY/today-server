package com.example.todayserver.domain.member.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

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

    @Data
    public static class Nickname{
        @NotBlank
        private String nickname;
    }

    @Data
    public static class Password{
        @Size(min = 8, max = 32)
        @NotBlank
        private String password;
    }
}
