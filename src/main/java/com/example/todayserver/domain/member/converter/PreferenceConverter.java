package com.example.todayserver.domain.member.converter;

import com.example.todayserver.domain.member.dto.PreferenceResDto;
import com.example.todayserver.domain.member.entity.Preference;

public class PreferenceConverter {

    public static PreferenceResDto.Notification toNotification(
            Preference preference
    ){
        return PreferenceResDto.Notification.builder()
                .emailAlert(preference.isEmailAlert())
                .kakaoAlert(preference.isKakaoAlert())
                .reminderAlert(preference.isReminderAlert())
                .build();
    }
}
