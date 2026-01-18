package com.example.todayserver.domain.post.controller;

import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.post.dto.PostRequestDTO;
import com.example.todayserver.domain.post.dto.PostResponseDTO;
import com.example.todayserver.domain.post.service.PostCommandService;
import com.example.todayserver.domain.post.service.PostQueryService;
import com.example.todayserver.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@Tag(name = "Post", description = "피드 API")
public class PostController {

    private final PostQueryService postQueryService;
    private final PostCommandService postCommandService;

    @Operation(summary = "피드 작성", description = "새로운 피드를 등록합니다.")
    @PostMapping
    public ApiResponse<String> createPost(
            @RequestBody @Valid PostRequestDTO.CreatePostDTO request,
            @AuthenticationPrincipal Member loginMember
    ) {
        postCommandService.createPost(request, loginMember);
        return ApiResponse.success("피드가 성공적으로 등록되었습니다.");
    }

    @Operation(summary = "댓글 작성", description = "새로운 댓글을 등록합니다.")
    @PostMapping("/{postId}/comments")
    public ApiResponse<String> createComment(
            @PathVariable Long postId,
            @AuthenticationPrincipal Member loginMember,
            @RequestBody PostRequestDTO.CreateCommentDTO request
    ) {
        postCommandService.createComment(loginMember, postId, request);
        return ApiResponse.success("댓글이 등록되었습니다.");
    }

    @Operation(summary = "피드 목록 조회", description = "무한 스크롤 방식으로 피드 목록을 조회합니다.")
    @GetMapping
    public ApiResponse<PostResponseDTO.PostListResponse> getPosts(
            @AuthenticationPrincipal Member loginMember,
            @RequestParam(required = false) Long lastPostId,
            @RequestParam(defaultValue = "10") int size) {

        return ApiResponse.success(postQueryService.getPostFeed(loginMember, lastPostId, size));
    }

    @Operation(summary = "피드 신고", description = "특정 게시글을 신고합니다.")
    @PostMapping("/{postId}/report")
    public ApiResponse<String> reportPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Member loginMember // 필터가 넣어준 Member 객체 주입
    ) {
        System.out.println("신고 유저: " + (loginMember != null ? loginMember.getEmail() : "null"));
        postCommandService.reportPost(loginMember, postId);

        return ApiResponse.success("신고가 정상적으로 접수되었습니다.");
    }

    @Operation(summary = "내 활동 조회", description = "내가 쓴 글 목록과 누적 통계를 조회합니다.")
    @GetMapping("/my")
    public ApiResponse<PostResponseDTO.MyActivityResponse> getMyActivity(
            @AuthenticationPrincipal Member loginMember,
            @RequestParam(required = false) Long lastPostId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ApiResponse.success(postQueryService.getMyActivity(loginMember, lastPostId, pageable.getPageSize()));
    }

    @Operation(summary = "피드 댓글 목록 조회", description = "특정 피드의 댓글 리스트를 조회합니다.")
    @GetMapping("/{postId}/comments")
    public ApiResponse<PostResponseDTO.CommentListResponse> getComments(@PathVariable Long postId, @AuthenticationPrincipal Member loginMember) {
        return ApiResponse.success(postQueryService.getComments(postId, loginMember));
    }

    @Operation(summary = "피드 좋아요 토글", description = "좋아요를 등록하거나 취소합니다.")
    @PostMapping("/{postId}/likes")
    public ApiResponse<String> toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal Member loginMember) {
        return ApiResponse.success(postCommandService.togglePostLike(loginMember, postId));
    }

    @Operation(summary = "댓글 좋아요 토글", description = "댓글에 좋아요를 등록하거나 취소합니다.")
    @PostMapping("/comments/{commentId}/likes")
    public ApiResponse<String> toggleCommentLike(
            @PathVariable Long commentId,
            @AuthenticationPrincipal Member loginMember) {
        return ApiResponse.success(postCommandService.toggleCommentLike(loginMember, commentId));
    }
}


