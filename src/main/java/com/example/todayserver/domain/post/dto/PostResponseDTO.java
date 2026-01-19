package com.example.todayserver.domain.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class PostResponseDTO {
    // 최근 피드 목록
    @Builder
    @Schema(description = "피드 목록 응답")
    public record PostListResponse(
            List<PostInfo> posts,
            @Schema(description = "다음 페이지 존재 여a부", example = "true")
            boolean hasNext,
            @Schema(description = "마지막으로 조회된 게시글 ID", example = "1024")
            Long lastPostId
    ) {}

    // 내 활동
    @Builder
    @Schema(description = "내 활동 페이지 응답")
    public record MyActivityResponse(
            @Schema(description = "총 게시글 수") Long totalPostCount,
            @Schema(description = "총 받은 좋아요 수") Long totalLikeCount,
            @Schema(description = "총 받은 댓글 수") Long totalCommentCount,
            List<PostInfo> posts, // 내가 쓴 글 목록
            boolean hasNext,
            Long lastPostId
    ) {}

    // 피드 정보
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

    // 게시물 작성자 정보
    @Builder
    public record AuthorInfo(
            Long userId,
            String nickname,
            String profileImageUrl
    ) {}

    // 피드 댓글 목록 조회
    @Builder
    public record CommentListResponse(
            List<CommentInfo> comments,
            Integer commentCount
    ) {}

    @Builder
    public record CommentInfo(
            Long commentId,
            AuthorInfo author,
            String content,
            String createdAt,
            Integer likeCount // 댓글 좋아요는 추후 CommentLike 엔티티와 연동
    ) {}
}