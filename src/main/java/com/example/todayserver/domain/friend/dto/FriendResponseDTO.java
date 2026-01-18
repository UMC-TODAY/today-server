package com.example.todayserver.domain.friend.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

public class FriendResponseDTO {

    @Getter
    @Builder
    public static class FriendListDTO {
        private List<FriendInfoDTO> friends;
        private Integer friendCount;
    }

    @Getter
    @Builder
    public static class FriendInfoDTO {
        private Long friendRecordId; // 친구 관계 자체의 ID -> 삭제/수정에 사용
        private Long memberId;       // 친구 ID
        private String nickname;
        private String profileImageUrl;
        private boolean isSharingCalendar;
    }
}