package com.example.todayserver.domain.analysis.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

public class DifficultyRequest {

    @Getter
    public static class Create {
        @NotNull(message = "날짜는 필수입니다.")
        private LocalDate date;

        @NotNull(message = "난이도는 필수입니다.")
        @Min(value = 1, message = "난이도는 1~7 사이여야 합니다.")
        @Max(value = 7, message = "난이도는 1~7 사이여야 합니다.")
        private Integer difficultyLevel;
    }

    @Getter
    public static class Update {
        @NotNull(message = "날짜는 필수입니다.")
        private LocalDate date;

        @NotNull(message = "난이도는 필수입니다.")
        @Min(value = 1, message = "난이도는 1~7 사이여야 합니다.")
        @Max(value = 7, message = "난이도는 1~7 사이여야 합니다.")
        private Integer difficultyLevel;
    }
}
