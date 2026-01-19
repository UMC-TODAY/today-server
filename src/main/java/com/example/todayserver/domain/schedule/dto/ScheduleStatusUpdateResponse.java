package com.example.todayserver.domain.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScheduleStatusUpdateResponse {

    private Long id;
    private boolean is_done;
}
