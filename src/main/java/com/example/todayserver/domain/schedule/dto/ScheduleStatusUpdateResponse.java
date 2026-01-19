package com.example.todayserver.domain.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

// 일정/할일 상태변경 및 완료 처리용 dto
public class ScheduleStatusUpdateResponse {

    private Long id;
    private boolean is_done;
}
