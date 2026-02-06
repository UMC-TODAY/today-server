package com.example.todayserver.domain.schedule.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// 일정/할일 상태변경 및 완료 처리용 dto
public class ScheduleStatusUpdateResponse {

    private Long id;

    // 응답 JSON은 is_done, 자바 필드는 isDone
    @JsonProperty("is_done")
    private boolean isDone;
}
