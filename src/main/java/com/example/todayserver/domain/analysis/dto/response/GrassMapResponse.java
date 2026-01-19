package com.example.todayserver.domain.analysis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class GrassMapResponse {

    private Period period;
    private Grid grid;
    private List<Grass> grass;
    private Summary summary;
    private LevelCriteria levelCriteria;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Period {
        private String startDate;    // YYYY-MM-DD
        private String endDate;       // YYYY-MM-DD
        private Integer days;         // 91
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Grid {
        private Integer rows;         // 7 (일~토)
        private Integer cols;         // 13 (13주)
        private Integer size;         // 91
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Grass {
        private String date;          // YYYY-MM-DD
        private String dayOfWeek;     // "SUNDAY", "MONDAY", ...
        private Integer completedCount;
        private Integer level;        // 0~4
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Summary {
        private Integer totalCompletedCount;    // 전체 완료 일정 수
        private Integer maxCompletedCount;      // 하루 최대 완료 수
        private Double averageCompletedCount;   // 평균 완료 수
        private Integer activeDays;             // 완료한 날짜의 수 (1개 이상)
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class LevelCriteria {
        private String level0;        // "0개"
        private String level1;        // "1개"
        private String level2;        // "2~3개"
        private String level3;        // "4~5개"
        private String level4;        // "6개 이상"
    }
}
