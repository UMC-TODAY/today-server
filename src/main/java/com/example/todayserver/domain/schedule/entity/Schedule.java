package com.example.todayserver.domain.schedule.entity;

import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.schedule.enums.Mode;
import com.example.todayserver.domain.schedule.enums.RepeatType;
import com.example.todayserver.domain.schedule.enums.ScheduleType;
import com.example.todayserver.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Mode mode;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = true, length = 500)
    private String memo;

    @Column(nullable = false, length = 10)
    private String color;

    @Column(nullable = true, length = 10)
    private String emoji;

    @Column(name = "schedule_date", nullable = true)
    private LocalDate scheduleDate;

    @Column(name = "start_time", nullable = true)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = true)
    private LocalTime endTime;

    @Column(name = "repeat_type", nullable = true)
    @Enumerated(EnumType.STRING)
    private RepeatType repeatType;

    @Column(name = "duration_minutes", nullable = true)
    private Integer durationMinutes;

    @Column(name = "is_done", nullable = false)
    private boolean isDone;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}