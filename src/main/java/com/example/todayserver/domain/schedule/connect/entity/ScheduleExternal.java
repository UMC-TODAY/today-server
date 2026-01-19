package com.example.todayserver.domain.schedule.connect.entity;

import com.example.todayserver.domain.schedule.entity.Schedule;
import com.example.todayserver.domain.schedule.connect.enums.ScheduleExternalVersionType;
import com.example.todayserver.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "schedule_external")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ScheduleExternal extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_event_id", nullable = false, length = 255)
    private String externalEventId;

    @Enumerated(EnumType.STRING)
    @Column(name = "version_type", nullable = false, length = 20)
    private ScheduleExternalVersionType versionType;

    @Column(name = "version_key", nullable = false, length = 255)
    private String versionKey;

    @Column(name = "origin_updated_at", nullable = false)
    private LocalDateTime originUpdatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "external_source_id", nullable = false)
    private ExternalSource externalSource;
}