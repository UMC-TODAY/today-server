package com.example.todayserver.domain.schedule.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
//일정/할일 상태변경 및 완료처리 request용 dto
public class ScheduleStatusUpdateRequest {

    private boolean is_done;
}
