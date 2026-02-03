package com.example.todayserver.domain.schedule.dto;

import lombok.Builder;

@Builder
public record TodoRangeCompletionRes(
        String from,
        String to,
        long totalCount,
        long completedCount
) {
    public static TodoRangeCompletionRes of(String from, String to, long totalCount, long completedCount) {
        return TodoRangeCompletionRes.builder()
                .from(from)
                .to(to)
                .totalCount(totalCount)
                .completedCount(completedCount)
                .build();
    }
}