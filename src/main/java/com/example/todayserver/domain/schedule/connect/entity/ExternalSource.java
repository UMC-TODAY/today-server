package com.example.todayserver.domain.schedule.connect.entity;

import com.example.todayserver.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "external_source")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ExternalSource extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_key", nullable = false, length = 255)
    private String sourceKey;

    @Column(name = "source_name", nullable = false, length = 255)
    private String sourceName;

    // timeZone, color 등 원본 메타데이터(JSON 문자열)
    @Column(name = "meta_json", columnDefinition = "json", nullable = true)
    private String metaJson;

    @Column(name = "sync_enabled", nullable = false)
    private boolean syncEnabled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "external_account_id", nullable = false)
    private ExternalAccount externalAccount;

    @OneToMany(mappedBy = "externalSource", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ScheduleExternal> scheduleExternals = new ArrayList<>();
}
