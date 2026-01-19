package com.example.todayserver.domain.analysis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class DifficultyResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Create {
        private Long difficultyId;
        private String date;              // YYYY-MM-DD
        private Integer difficultyLevel;
        private String difficultyName;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Update {
        private String date;              // YYYY-MM-DD
        private Integer difficultyLevel;
        private String difficultyName;
        private LocalDateTime updatedAt;
    }
}
