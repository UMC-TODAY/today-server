package com.example.todayserver.domain.friend.entity;

public enum FriendStatus {
    PENDING,  // 친구 요청 중 (대기)
    ACCEPTED,  // 친구 수락 완료 (친구 사이)
    NONE // 관계 없음
}