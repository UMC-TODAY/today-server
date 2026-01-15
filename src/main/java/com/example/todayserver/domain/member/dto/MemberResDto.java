package com.example.todayserver.domain.member.dto;

import lombok.Builder;
import lombok.Data;

public class MemberResDto {
    @Data
    @Builder
    public static class LoginDto{
        Long memberId;
        String accessToken;
    }
}
