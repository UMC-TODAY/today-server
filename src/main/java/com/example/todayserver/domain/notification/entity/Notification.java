package com.example.todayserver.domain.notification.entity;

import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member receiver; // 알림을 받는 사람

    private String content; // 알림 메시지

    @Enumerated(EnumType.STRING)
    private NotificationType type; // FRIEND_REQUEST, COMMENT, LIKE

    private Long targetId; // 클릭 시 이동할 대상 ID

    private boolean isRead = false; // 읽음 여부

    @Builder
    public Notification(Member receiver, String content, NotificationType type, Long targetId) {
        this.receiver = receiver;
        this.content = content;
        this.type = type;
        this.targetId = targetId;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}