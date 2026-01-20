package com.example.todayserver.domain.notification.service;

import com.example.todayserver.domain.notification.dto.NotificationResponseDTO;
import com.example.todayserver.domain.notification.entity.Notification;
import com.example.todayserver.domain.notification.repository.NotificationRepository;
import com.example.todayserver.domain.member.entity.Member;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationQueryService {

    private final NotificationRepository notificationRepository;

    public List<NotificationResponseDTO> getNotifications(Member loginMember) {
        List<Notification> notifications = notificationRepository.findAllByReceiverOrderByCreatedAtDesc(loginMember);

        return notifications.stream()
                .map(NotificationResponseDTO::from)
                .collect(Collectors.toList());
    }
}