package com.example.todayserver.domain.analysis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class FocusChecklistResponse {

    private String date;                    // YYYY-MM-DD
    private LocalDateTime nextResetAt;      // 다음 리셋 시간 (익일 06:00)
    private List<ChecklistItem> items;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ChecklistItem {
        private Long itemId;
        private String content;
        private Boolean isCompleted;
    }
}
