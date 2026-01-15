package com.example.todayserver.domain.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.util.List;

public class PostResponseDTO {

    @Builder
    @Schema(description = "피드 목록 응답")
    public record PostListResponse(
            List<PostInfo> posts,
            @Schema(description = "다음 페이지 존재 여a부", example = "true")
            boolean hasNext,
            @Schema(description = "마지막으로 조회된 게시글 ID", example = "1024")
            Long lastPostId
    ) {}

    @Builder
    public record PostInfo(
            Long postId,
            AuthorInfo author,
            String content,
            String createdAt,
            Integer likeCount,
            Integer commentCount,
            boolean isLiked,
            boolean isBlocked
    ) {}

    @Builder
    public record AuthorInfo(
            Long userId,
            String nickname,
            String profileImageUrl
    ) {}
}