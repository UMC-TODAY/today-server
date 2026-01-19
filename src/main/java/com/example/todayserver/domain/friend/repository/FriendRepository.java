package com.example.todayserver.domain.friend.repository;

import com.example.todayserver.domain.friend.entity.Friend;
import com.example.todayserver.domain.friend.entity.FriendStatus;
import com.example.todayserver.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    // 이미 친구이거나 요청 중인지 확인
    Optional<Friend> findByRequesterAndReceiver(Member requester, Member receiver);

    // 내 친구 목록 조회
    List<Friend> findAllByRequesterAndStatus(Member requester, FriendStatus status);
    List<Friend> findAllByReceiverAndStatus(Member receiver, FriendStatus status);
}