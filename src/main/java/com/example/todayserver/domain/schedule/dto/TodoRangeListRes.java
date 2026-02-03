package com.example.todayserver.domain.schedule.dto;

import java.util.List;

public record TodoRangeListRes(
        String filter,
        List<TodoItem> todos
) {
    public record TodoItem(
            Long id,
            String title,
            String color,
            String emoji,
            boolean isDone,
            String date,
            String mode
    ) {
        public static TodoItem of(
                Long id,
                String title,
                String color,
                String emoji,
                boolean isDone,
                String date,
                String mode
        ) {
            return new TodoItem(id, title, color, emoji, isDone, date, mode);
        }
    }

    public static TodoRangeListRes of(String filter, List<TodoItem> todos) {
        return new TodoRangeListRes(filter, todos);
    }
}