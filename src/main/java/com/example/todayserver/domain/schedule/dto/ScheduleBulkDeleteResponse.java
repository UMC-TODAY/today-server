package com.example.todayserver.domain.schedule.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

// 할일 목록 일괄 삭제 response 용 dto
public class ScheduleBulkDeleteResponse {

    // 실제 삭제된 개수
    @JsonProperty("deleted_count")
    private long deletedCount;
}
