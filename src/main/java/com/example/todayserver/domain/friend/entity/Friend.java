package com.example.todayserver.domain.friend.entity;

import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friend extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    private Member requester; // 요청자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private Member receiver; // 수신자

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendStatus status;

    @Column(nullable = false)
    @Builder.Default
    private boolean isSharingCalendar = true; // 일정 공유 여부 (기본값 ON)

    // 일정 공유 on/off 토글
    public void toggleCalendarSharing() {
        this.isSharingCalendar = !this.isSharingCalendar;
    }

    // 친구 수락 메서드
    public void acceptRequest() {
        this.status = FriendStatus.ACCEPTED;
    }
}