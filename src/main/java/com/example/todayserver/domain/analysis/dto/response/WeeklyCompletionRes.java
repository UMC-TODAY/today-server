package com.example.todayserver.domain.analysis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class WeeklyCompletionRes {

    private List<DayCompletionRate> weeklyRates;
    private List<AnalysisMessage> analysisMessages;
    private Statistics statistics;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class DayCompletionRate {
        private String dayOfWeek;        // "MONDAY"
        private String dayName;           // "월요일"
        private Integer totalCount;       // 총 일정 수
        private Integer completedCount;   // 완료한 일정 수
        private Double completionRate;    // 완료율 (%)
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class AnalysisMessage {
        private String type;              // "DEVIATION", "HIGH_COMPLETION" 등
        private String message;           // 분석 메시지
        private String recommendation;    // 추천 사항
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Statistics {
        private Double highestRate;       // 최고 완료율
        private Double lowestRate;        // 최저 완료율
        private Double averageRate;       // 평균 완료율
        private Double deviation;         // 편차
    }
}
