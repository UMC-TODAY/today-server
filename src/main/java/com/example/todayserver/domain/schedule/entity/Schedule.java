package com.example.todayserver.domain.schedule.entity;

import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.schedule.enums.Mode;
import com.example.todayserver.domain.schedule.enums.RepeatType;
import com.example.todayserver.domain.schedule.enums.ScheduleSource;
import com.example.todayserver.domain.schedule.enums.ScheduleType;
import com.example.todayserver.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@Entity
@NoArgsConstructor
@Table(name = "schedule")
public class Schedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "schedule_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ScheduleType scheduleType;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private Mode mode;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false, length = 20)
    private ScheduleSource source;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = true, length = 500)
    private String memo;

    @Column(nullable = false, length = 10)
    private String color;

    @Column(nullable = true, length = 10)
    private String emoji;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "repeat_type", nullable = true)
    @Enumerated(EnumType.STRING)
    private RepeatType repeatType;

    @Column(name = "duration_minutes", nullable = true)
    private Integer durationMinutes;

    @Column(name = "is_done", nullable = false)
    private boolean isDone;

    @Column(name = "is_all_day", nullable = false)
    private boolean isAllDay;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // Schedule 삭제 시 연결된 SubSchedule도 함께 삭제되도록 설정
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SubSchedule> subSchedules = new ArrayList<>();

    // isDone의 상태를 업데이트 하기 위한 메서드
    public void updateDone(boolean isDone) {
        this.isDone = isDone;
    }

    public void updatePatch(
            ScheduleType scheduleType,
            Mode mode,
            String title,
            String memo,
            String color,
            String emoji,
            RepeatType repeatType,
            Integer durationMinutes,
            Boolean isAllDay,
            LocalDateTime startedAt,
            LocalDateTime endedAt
    ) {
        if (scheduleType != null) this.scheduleType = scheduleType;
        if (mode != null) this.mode = mode;
        if (title != null) this.title = title;
        if (memo != null) this.memo = memo;
        if (color != null) this.color = color;
        if (emoji != null) this.emoji = emoji;
        if (repeatType != null) this.repeatType = repeatType;
        if (durationMinutes != null) this.durationMinutes = durationMinutes;
        if (isAllDay != null) this.isAllDay = isAllDay;

        if (startedAt != null) this.startedAt = startedAt;
        if (endedAt != null) this.endedAt = endedAt;
    }
}
