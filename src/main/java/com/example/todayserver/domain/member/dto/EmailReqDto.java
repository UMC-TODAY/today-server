package com.example.todayserver.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

@Getter
public class EmailReqDto {

    @Data
    public static class EmailCheck{
        @NotNull(message = "email은 필수입니다.")
        private String email;
    }

    @Data
    public static class EmailCode{
        private String email;
        @JsonProperty("verify-code")
        private String code;
    }
}
