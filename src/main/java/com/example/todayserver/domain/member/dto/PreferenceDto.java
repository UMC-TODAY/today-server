package com.example.todayserver.domain.member.dto;

import lombok.Builder;
import lombok.Data;

public class PreferenceDto {

    @Data
    @Builder
    public static class Notification {
        Boolean reminderAlert;
        Boolean kakaoAlert;
        Boolean emailAlert;
    }
}
