package com.example.todayserver.global.common.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeUtil {
    public static String formatTimeAgo(LocalDateTime dateTime) {
        if (dateTime == null) return null;

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(dateTime, now);
        long seconds = duration.getSeconds();

        if (seconds < 60) return "방금 전";
        if (seconds < 3600) return (seconds / 60) + "분 전";
        if (seconds < 86400) return (seconds / 3600) + "시간 전";
        if (seconds < 2592000) return (seconds / 86400) + "일 전";
        if (seconds < 31104000) return (seconds / 2592000) + "달 전";

        return (seconds / 31104000) + "년 전";
    }
}