package com.example.todayserver.domain.friend.repository;

import com.example.todayserver.domain.friend.entity.Friend;
import com.example.todayserver.domain.friend.entity.FriendStatus;
import com.example.todayserver.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    // 두 유저 사이의 모든 관계(누가 보냈든 상관없이) 조회
    @Query("SELECT f FROM Friend f WHERE (f.requester = :m1 AND f.receiver = :m2) OR (f.requester = :m2 AND f.receiver = :m1)")
    Optional<Friend> findRelation(@Param("m1") Member m1, @Param("m2") Member m2);

    // 내 친구 목록 조회
    List<Friend> findAllByRequesterAndStatus(Member requester, FriendStatus status);
    List<Friend> findAllByReceiverAndStatus(Member receiver, FriendStatus status);

}
