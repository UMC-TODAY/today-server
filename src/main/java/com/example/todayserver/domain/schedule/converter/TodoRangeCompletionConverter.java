package com.example.todayserver.domain.schedule.converter;

import com.example.todayserver.domain.schedule.dto.TodoRangeCompletionRes;
import org.springframework.stereotype.Component;

@Component
public class TodoRangeCompletionConverter {

    // 기간(from/to) 기준 완료 현황 응답 변환
    public TodoRangeCompletionRes toRes(String from, String to, long total, long completed) {
        return TodoRangeCompletionRes.of(from, to, total, completed);
    }
}
