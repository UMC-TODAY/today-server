package com.example.todayserver.domain.schedule.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor

// 할일 목록 일괄 삭제 request 용 dto
public class ScheduleBulkDeleteRequest {

    // 삭제할 schedule id 목록
    @NotEmpty
    private List<Long> ids;
}
