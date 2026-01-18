package com.example.todayserver.domain.friend.service;

import com.example.todayserver.domain.friend.entity.Friend;
import com.example.todayserver.domain.friend.entity.FriendStatus;
import com.example.todayserver.domain.friend.repository.FriendRepository;
import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.member.repository.MemberRepository;
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

    public String requestOrCancelFriend(Member requester, Long receiverId) {
        // 자신에게 요청 확인
        if (requester.getId().equals(receiverId)) {
            throw new RuntimeException("자기 자신에게는 친구 요청을 보낼 수 없습니다.");
        }

        // 상대방 확인
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        // 이미 보낸 요청이 있는지 확인
        Optional<Friend> existingFriend = friendRepository.findByRequesterAndReceiver(requester, receiver);

        // FriendCommandService.java (임시 테스트용)
        if (existingFriend.isPresent()) {
            // 기존에는 삭제(취소)였지만, 테스트를 위해 수락으로 잠시 변경!
            existingFriend.get().acceptRequest();
            return "강제 수락 완료!";

        } else {
            // 존재하지 않는다면 새로 요청
            Friend friend = Friend.builder()
                    .requester(requester)
                    .receiver(receiver)
                    .status(FriendStatus.PENDING)
                    .isSharingCalendar(true) // 기본값 ON
                    .build();
            friendRepository.save(friend);
            return "친구 요청 완료";
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