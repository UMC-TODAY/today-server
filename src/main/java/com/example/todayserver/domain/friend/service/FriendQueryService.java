package com.example.todayserver.domain.friend.service;

import com.example.todayserver.domain.friend.dto.FriendResponseDTO;
import com.example.todayserver.domain.friend.entity.Friend;
import com.example.todayserver.domain.friend.entity.FriendStatus;
import com.example.todayserver.domain.friend.repository.FriendRepository;
import com.example.todayserver.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendQueryService {

    private final FriendRepository friendRepository;

    public FriendResponseDTO.FriendListDTO getFriendList(Member loginMember) {
        // 내가 보낸 요청 중 수락된 것 + 내가 받은 요청 중 수락된 것 조회
        List<Friend> friendsAsRequester = friendRepository.findAllByRequesterAndStatus(loginMember, FriendStatus.ACCEPTED);
        List<Friend> friendsAsReceiver = friendRepository.findAllByReceiverAndStatus(loginMember, FriendStatus.ACCEPTED);

        // 상대방 정보 추출
        List<FriendResponseDTO.FriendInfoDTO> friendInfos = new java.util.ArrayList<>();

        // 내가 요청자인 경우 -> 상대방은 receiver
        friendsAsRequester.forEach(f -> friendInfos.add(mapToInfo(f, f.getReceiver())));
        // 내가 수신자인 경우 -> 상대방은 requester
        friendsAsReceiver.forEach(f -> friendInfos.add(mapToInfo(f, f.getRequester())));

        return FriendResponseDTO.FriendListDTO.builder()
                .friends(friendInfos)
                .friendCount(friendInfos.size())
                .build();
    }

    private FriendResponseDTO.FriendInfoDTO mapToInfo(Friend friend, Member opponent) {
        return FriendResponseDTO.FriendInfoDTO.builder()
                .friendRecordId(friend.getId())
                .memberId(opponent.getId())
                .nickname(opponent.getNickname())
                .profileImageUrl(opponent.getProfileImage())
                .isSharingCalendar(friend.isSharingCalendar())
                .build();
    }
}