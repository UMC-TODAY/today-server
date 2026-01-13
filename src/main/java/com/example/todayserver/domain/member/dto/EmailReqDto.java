package com.example.todayserver.domain.member.dto;

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
}
