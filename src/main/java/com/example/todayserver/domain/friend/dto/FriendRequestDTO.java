package com.example.todayserver.domain.friend.dto;

import lombok.Getter;

public class FriendRequestDTO {
    @Getter
    public static class RequestFriendDTO {
        private Long receiverId;
    }
}