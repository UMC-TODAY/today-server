package com.example.todayserver.domain.notification.dto;

import com.example.todayserver.domain.notification.entity.Notification;
import com.example.todayserver.domain.notification.entity.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponseDTO {
    private Long notificationId;
    private String content;
    private NotificationType type;
    private Long targetId;
    private boolean isRead;
    private LocalDateTime createdAt;

    public static NotificationResponseDTO from(Notification notification) {
        return NotificationResponseDTO.builder()
                .notificationId(notification.getId())
                .content(notification.getContent())
                .type(notification.getType())
                .targetId(notification.getTargetId())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}