package com.example.todayserver.domain.analysis.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DifficultyLevel {
    VERY_EASY(1, "매우 쉬움"),
    EASY(2, "쉬움"),
    SLIGHTLY_EASY(3, "조금 쉬움"),
    NORMAL(4, "보통"),
    SLIGHTLY_HARD(5, "조금 어려움"),
    HARD(6, "어려움"),
    VERY_HARD(7, "많이 어려움");

    private final int level;
    private final String name;

    public static DifficultyLevel fromLevel(int level) {
        for (DifficultyLevel difficulty : values()) {
            if (difficulty.level == level) {
                return difficulty;
            }
        }
        throw new IllegalArgumentException("유효하지 않은 난이도 레벨입니다: " + level);
    }
}
