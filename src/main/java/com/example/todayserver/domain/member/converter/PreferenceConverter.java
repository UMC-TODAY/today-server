package com.example.todayserver.domain.member.converter;

import com.example.todayserver.domain.member.dto.PreferenceDto;
import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.member.entity.Preference;
import com.example.todayserver.domain.member.enums.PrivacyScope;

public class PreferenceConverter {

    public static PreferenceDto.Notification toNotification(
            Preference preference
    ){
        return PreferenceDto.Notification.builder()
                .emailAlert(preference.isEmailAlert())
                .kakaoAlert(preference.isKakaoAlert())
                .reminderAlert(preference.isReminderAlert())
                .build();
    }

    public static Preference newPreference(
            Member member
    ){
        return Preference.builder()
                .emailAlert(true)
                .kakaoAlert(true)
                .reminderAlert(true)
                .privacyScope(PrivacyScope.FRIEND)
                .dataUse(true)
                .member(member)
                .build();
    }
}
