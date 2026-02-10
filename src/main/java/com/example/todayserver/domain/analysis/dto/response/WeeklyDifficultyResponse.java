package com.example.todayserver.domain.analysis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class WeeklyDifficultyResponse {

    private String weekStart;      // 주 시작일 (일요일)
    private String weekEnd;        // 주 종료일 (토요일)
    private List<DayDifficulty> days;  // 요일별 난이도 (7개)

    @Getter
    @Builder
    @AllArgsConstructor
    public static class DayDifficulty {
        private String date;           // 날짜 (yyyy-MM-dd)
        private String dayOfWeek;      // 요일 (SUNDAY, MONDAY, ...)
        private String dayName;        // 요일 한글 (일, 월, 화, ...)
        private Integer difficultyLevel;   // 난이도 레벨 (1~7), 미등록 시 null
        private String difficultyName;     // 난이도 이름, 미등록 시 null
        private Boolean isRegistered;      // 등록 여부
    }
}
