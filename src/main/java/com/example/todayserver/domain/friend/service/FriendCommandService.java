package com.example.todayserver.domain.friend.service;

import com.example.todayserver.domain.notification.entity.Notification;
import com.example.todayserver.domain.notification.entity.NotificationType;
import com.example.todayserver.domain.friend.entity.Friend;
import com.example.todayserver.domain.friend.entity.FriendStatus;
import com.example.todayserver.domain.friend.repository.FriendRepository;
import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.member.repository.MemberRepository;
import com.example.todayserver.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendCommandService {

    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;

    public String requestOrCancelFriend(Member requester, Long receiverId) {
        // 자신에게 요청 확인
        if (requester.getId().equals(receiverId)) {
            throw new RuntimeException("자기 자신에게는 친구 요청을 보낼 수 없습니다.");
        }

        // 상대방 확인
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));


        Optional<Friend> existingFriend = friendRepository.findRelation(requester, receiver);

        if (existingFriend.isPresent()) {
            Friend friend = existingFriend.get();

            // 이미 친구인 상태면 취소 불가
            if (friend.getStatus() == FriendStatus.ACCEPTED) {
                return "ACCEPTED";
            }

            // 대기 중(PENDING)일 때만 요청 취소(삭제)
            friendRepository.delete(friend);
            return "NONE";

        } else {
            // 관계가 전혀 없는 상태 (NONE) -> 새로 요청 생성
            Friend friend = Friend.builder()
                    .requester(requester)
                    .receiver(receiver)
                    .status(FriendStatus.PENDING)
                    .isSharingCalendar(true)
                    .build();
            Friend savedFriend = friendRepository.save(friend);

            Notification notification = Notification.builder()
                    .receiver(receiver)
                    .content(requester.getNickname() + "님이 친구 요청을 보냈습니다.")
                    .type(NotificationType.FRIEND_REQUEST)
                    .targetId(savedFriend.getId())
                    .build();
            notificationRepository.save(notification);

            return "PENDING";
        }
    }

    @Transactional
    public void deleteFriend(Member loginMember, Long friendRecordId) {
        Friend friend = friendRepository.findById(friendRecordId)
                .orElseThrow(() -> new RuntimeException("친구 관계를 찾을 수 없습니다."));

        // 권한 확인 -> 본인이 관계의 당사자인가
        if (!friend.getRequester().getId().equals(loginMember.getId()) &&
                !friend.getReceiver().getId().equals(loginMember.getId())) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        friendRepository.delete(friend);
    }

    @Transactional
    public String toggleCalendarSharing(Member loginMember, Long friendRecordId) {
        Friend friend = friendRepository.findById(friendRecordId)
                .orElseThrow(() -> new RuntimeException("친구 관계를 찾을 수 없습니다."));

        // 일정 공유 권한 확인 -> 이 친구 관계에 내가 포함되어 있는지 확인(본인인지)
        if (!friend.getRequester().getId().equals(loginMember.getId()) &&
                !friend.getReceiver().getId().equals(loginMember.getId())) {
            throw new RuntimeException("설정 권한이 없습니다.");
        }

        friend.toggleCalendarSharing();

        return friend.isSharingCalendar() ? "일정 공유 On" : "일정 공유 Off";
    }
}