package com.example.todayserver.domain.member.converter;

import com.example.todayserver.domain.member.dto.PreferenceResDto;
import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.member.entity.Preference;
import com.example.todayserver.domain.member.enums.PrivacyScope;

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
