package com.example.todayserver.domain.analysis.entity;

import com.example.todayserver.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "focus_checklist")
public class FocusChecklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "focus_checklist_id")
    private Long id;

    @Column(name = "content", nullable = false, length = 255)
    private String content;

    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 완료 상태 업데이트
    public void updateCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
}
