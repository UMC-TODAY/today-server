package com.example.todayserver.domain.analysis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TogetherDaysResponse {

    private Integer togetherDays;      // 함께한 일수
    private Integer consecutiveDays;   // 연속 접속일
    private String joinedAt;           // 가입 날짜 (YYYY-MM-DD)
    private String message;            // 메시지
}
