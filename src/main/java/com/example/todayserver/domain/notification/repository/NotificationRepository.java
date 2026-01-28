package com.example.todayserver.domain.notification.repository;

import com.example.todayserver.domain.notification.entity.Notification;
import com.example.todayserver.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // 특정 사용자의 알림을 최신순으로 가져오는 쿼리
    List<Notification> findAllByReceiverOrderByCreatedAtDesc(Member receiver);
}