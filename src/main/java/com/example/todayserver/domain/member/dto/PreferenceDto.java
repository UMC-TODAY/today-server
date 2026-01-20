package com.example.todayserver.domain.member.dto;

import com.example.todayserver.domain.member.enums.PrivacyScope;
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

    @Data
    @Builder
    public static class Info {
        PrivacyScope privacyScope;
        Boolean dataUse;
    }
}
