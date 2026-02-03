package com.example.todayserver.domain.schedule.converter;

import com.example.todayserver.domain.schedule.dto.TodoRangeListRes;
import com.example.todayserver.domain.schedule.entity.Schedule;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class TodoRangeConverter {

    public TodoRangeListRes toTodoRangeListRes(
            String filterLabel,
            List<Schedule> schedules,
            DateTimeFormatter dateFmt
    ) {
        List<TodoRangeListRes.TodoItem> todos = schedules.stream()
                .map(s -> toTodoItem(s, dateFmt))
                .toList();

        return TodoRangeListRes.of(filterLabel, todos);
    }

    private TodoRangeListRes.TodoItem toTodoItem(Schedule s, DateTimeFormatter dateFmt) {
        String date = (s.getStartedAt() == null)
                ? null
                : s.getStartedAt().toLocalDate().format(dateFmt);

        String mode = (s.getMode() == null)
                ? null
                : s.getMode().name();

        return TodoRangeListRes.TodoItem.of(
                s.getId(),
                s.getTitle(),
                s.getColor(),
                s.getEmoji(),
                s.isDone(),
                date,
                mode
        );
    }
}