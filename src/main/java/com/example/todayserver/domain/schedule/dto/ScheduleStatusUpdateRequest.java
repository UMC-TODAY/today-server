package com.example.todayserver.domain.schedule.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
// 일정/할일 상태변경 및 완료처리 request용 dto
public class ScheduleStatusUpdateRequest {

    // 요청 JSON은 is_done, 자바 필드는 isDone
    @NotNull
    @JsonProperty("is_done")
    private Boolean isDone;
}
