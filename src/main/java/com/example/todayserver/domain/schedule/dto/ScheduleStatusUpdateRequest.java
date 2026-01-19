package com.example.todayserver.domain.schedule.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ScheduleStatusUpdateRequest {

    private boolean is_done;
}
