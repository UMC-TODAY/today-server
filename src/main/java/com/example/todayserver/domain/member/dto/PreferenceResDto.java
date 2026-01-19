package com.example.todayserver.domain.member.dto;

import lombok.Builder;
import lombok.Data;

public class PreferenceResDto {

    @Data
    @Builder
    public static class Notification {
        boolean reminderAlert;
        boolean kakaoAlert;
        boolean emailAlert;
    }
}
