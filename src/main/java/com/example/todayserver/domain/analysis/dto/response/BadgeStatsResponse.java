package com.example.todayserver.domain.analysis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BadgeStatsResponse {

    private BadgeInfo badge;
    private CompletedScheduleInfo completedSchedule;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class BadgeInfo {
        private Integer totalCount;        // 총 뱃지 카운트
        private Integer rankingPercent;    // 상위 % (전체 유저 대비)
        private String rankingDirection;   // "UP" (상위 표시)
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class CompletedScheduleInfo {
        private Integer totalCount;        // 총 완료한 일정 수 (TASK + EVENT)
        private Integer rankingPercent;    // 상위 % (전체 유저 대비)
        private String rankingDirection;   // "UP" (상위 표시)
    }
}
