package com.example.todayserver.domain.post.controller;

import com.example.todayserver.domain.post.dto.PostResponseDTO;
import com.example.todayserver.domain.post.service.PostQueryService;
import com.example.todayserver.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
@Tag(name = "Post", description = "피드 API")
public class PostController {

    private final PostQueryService postQueryService;

    @Operation(
            summary = "피드 목록 조회",
            description = "무한 스크롤 방식으로 피드 목록을 조회합니다."
    )
    @GetMapping
    public ApiResponse<PostResponseDTO.PostListResponse> getPosts(
            @RequestParam(required = false) Long lastPostId,
            @RequestParam(defaultValue = "10") int size) {

        return ApiResponse.success(postQueryService.getPostFeed(lastPostId, size));
    }
}