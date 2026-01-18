package com.example.todayserver.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

public class PostRequestDTO {
    public record ReportRequestDTO(
            Long postId
    ) {}

    // 피드 작성
    @Getter
    public static class CreatePostDTO {
        @NotBlank
        private String content;
    }

    // 댓글 작성
    @Getter
    public static class CreateCommentDTO {
        private String content;
    }
}
