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

    // 이미 친구이거나 요청 중인지 확인
    Optional<Friend> findByRequesterAndReceiver(Member requester, Member receiver);

    // 내 친구 목록 조회
    List<Friend> findAllByRequesterAndStatus(Member requester, FriendStatus status);
    List<Friend> findAllByReceiverAndStatus(Member receiver, FriendStatus status);

    // 검색용 (상대방의 닉네임에 키워드가 포함되어 있는지 확인)
    List<Friend> findAllByRequesterAndStatusAndReceiverNicknameContaining(Member requester, FriendStatus status, String keyword);
    List<Friend> findAllByReceiverAndStatusAndRequesterNicknameContaining(Member receiver, FriendStatus status, String keyword);
    // 내 친구 중 닉네임에 검색어가 포함된 유저 조회
    @Query("SELECT f FROM Friend f JOIN f.receiver m " +
            "WHERE f.requester = :member AND f.status = 'ACCEPTED' AND m.nickname LIKE %:keyword% " +
            "UNION " +
            "SELECT f FROM Friend f JOIN f.requester m " +
            "WHERE f.receiver = :member AND f.status = 'ACCEPTED' AND m.nickname LIKE %:keyword%")
    List<Friend> findFriendsByNickname(@Param("member") Member member, @Param("keyword") String keyword);
}
