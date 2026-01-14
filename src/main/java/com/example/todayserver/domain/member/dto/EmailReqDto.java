package com.example.todayserver.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;

@Getter
public class EmailReqDto {

    @Data
    public static class EmailCheck{
        @NotBlank
        @Email
        private String email;
    }

    @Data
    public static class EmailCode{
        @NotBlank
        @Email
        private String email;
        @JsonProperty("verify-code")
        private String code;
    }
}
