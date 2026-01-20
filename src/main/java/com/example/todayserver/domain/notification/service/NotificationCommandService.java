package com.example.todayserver.domain.notification.service;

import com.example.todayserver.domain.notification.entity.Notification;
import com.example.todayserver.domain.notification.entity.NotificationType;
import com.example.todayserver.domain.notification.repository.NotificationRepository;
import com.example.todayserver.domain.friend.entity.Friend;
import com.example.todayserver.domain.friend.repository.FriendRepository;
import com.example.todayserver.domain.member.entity.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationCommandService {

    private final NotificationRepository notificationRepository;
    private final FriendRepository friendRepository;

    // 친구 요청 수락
    public void acceptFriendRequest(Member loginMember, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없습니다."));

        // 내 알림이 맞는지 확인
        validateOwner(loginMember, notification);

        // 이미 처리된 알림인지 확인
        if (notification.isRead() && notification.getType() == NotificationType.FRIEND_REQUEST) {

        }

        Friend friend = friendRepository.findById(notification.getTargetId())
                .orElseThrow(() -> new RuntimeException("친구 요청 정보를 찾을 수 없습니다."));

        // 친구 요청 상태 변경 (PENDING -> ACCEPTED)
        friend.acceptRequest();

        // 알림 읽음 처리
        notification.markAsRead();
    }

    // 친구 요청 거절
    public void rejectFriendRequest(Member loginMember, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없습니다."));

        validateOwner(loginMember, notification);

        // 친구 요청 레코드 삭제 및 알림 삭제
        friendRepository.deleteById(notification.getTargetId());
        notificationRepository.delete(notification);
    }

    private void validateOwner(Member loginMember, Notification notification) {
        if (!notification.getReceiver().getId().equals(loginMember.getId())) {
            throw new RuntimeException("본인의 알림만 처리할 수 있습니다.");
        }
    }
}