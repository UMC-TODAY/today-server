package com.example.todayserver.domain.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

// 할일 목록 일괄 삭제 response 용 dto
public class ScheduleBulkDeleteResponse {
    // 실제 삭제된 개수
    private long deleted_count;
}
