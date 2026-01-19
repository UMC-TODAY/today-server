package com.example.todayserver.domain.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor

// 할일 필터링 및 검색 조회 용 dto
// 회원(memberId) 기준으로 완료여부(isDone), 일정타입(scheduleType=category),
// 제목 키워드(title like), 시작일자 범위(startedAt from~to) 조건을 조합해서 검색 가능
public class ScheduleSearchItemResponse {
    private Long id;
    private String title;
    private boolean is_done;
    private String category;
    private LocalDate schedule_date;
    private String emoji;
}
