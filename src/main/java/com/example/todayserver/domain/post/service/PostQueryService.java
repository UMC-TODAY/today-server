package com.example.todayserver.domain.post.service;

import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.post.dto.PostResponseDTO;
import com.example.todayserver.domain.post.entity.Comment;
import com.example.todayserver.domain.post.entity.Post;
import com.example.todayserver.domain.post.repository.CommentRepository;
import com.example.todayserver.domain.post.repository.PostRepository;
import com.example.todayserver.global.common.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQueryService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    // 최근 피드 목록 조회
    public PostResponseDTO.PostListResponse getPostFeed(Member loginMember, Long lastPostId, int size) {
        PageRequest pageRequest = PageRequest.of(0, size);
        Slice<Post> postSlice = postRepository.findPostsForFeed(lastPostId, pageRequest);

        // 공통 변환 로직 호출
        return buildPostListResponse(postSlice, loginMember);
    }

    // 내 활동 목록 조회
    public PostResponseDTO.MyActivityResponse getMyActivity(Member member, Long lastPostId, int size) {
        PageRequest pageRequest = PageRequest.of(0, size);

        // 내가 쓴 글만 필터링해서 조회
        Slice<Post> postSlice = postRepository.findMyPosts(member, lastPostId, pageRequest);

        // 상단 요약 통계 정보 조회 (좋아요, 게시물 개수, 댓글)
        Long totalPostCount = postRepository.countByMember(member);

        // TODO: Like, Comment 테이블 구현 후 실제 누적 count 로직으로 변경 예정
        Long totalLikeCount = 0L;
        Long totalCommentCount = 0L;

        // 피드 목록 변환 (공통)
        PostResponseDTO.PostListResponse postListResponse = buildPostListResponse(postSlice, member);

        // 내 활동 전용
        return PostResponseDTO.MyActivityResponse.builder()
                .totalPostCount(totalPostCount)
                .totalLikeCount(totalLikeCount)
                .totalCommentCount(totalCommentCount)
                .posts(postListResponse.posts())
                .hasNext(postListResponse.hasNext())
                .lastPostId(postListResponse.lastPostId())
                .build();
    }

    // 피드 댓글 목록 조회
    public PostResponseDTO.CommentListResponse getComments(Long postId, Member loginMember) {
        // 해당 게시글의 댓글들 조회
        List<Comment> comments = commentRepository.findAllByPostIdOrderByCreatedAtDesc(postId);

        // 엔티티 -> DTO 변환
        List<PostResponseDTO.CommentInfo> commentInfos = comments.stream()
                .map(comment -> PostResponseDTO.CommentInfo.builder()
                        .commentId(comment.getId())
                        .content(comment.getContent())
                        .author(PostResponseDTO.AuthorInfo.builder()
                                .userId(comment.getMember().getId())
                                .nickname(comment.getMember().getNickname())
                                .profileImageUrl(comment.getMember().getProfileImage())
                                .build())
                        .createdAt(TimeUtil.formatTimeAgo(comment.getCreatedAt()))
                        .likeCount(comment.getCommentLikes() != null ? comment.getCommentLikes().size() : 0)
                        .build())
                .collect(Collectors.toList());

        return PostResponseDTO.CommentListResponse.builder()
                .comments(commentInfos)
                .commentCount(commentInfos.size())
                .build();
    }

    // 피드를 리스트 형식으로 변경하는 공통 로직
    private PostResponseDTO.PostListResponse buildPostListResponse(Slice<Post> postSlice, Member loginMember) {
        List<PostResponseDTO.PostInfo> postInfos = postSlice.getContent().stream()
                .map(post -> {
                    boolean isLiked = loginMember != null && post.getPostLikes() != null &&
                        post.getPostLikes().stream()
                                .anyMatch(like -> like.getMember().getId().equals(loginMember.getId()));

                    return PostResponseDTO.PostInfo.builder()
                            .postId(post.getId())
                            .content(post.getContent())
                            .author(post.getMember() != null ? PostResponseDTO.AuthorInfo.builder()
                                    .userId(post.getMember().getId())
                                    .nickname(post.getMember().getNickname())
                                    .profileImageUrl(post.getMember().getProfileImage())
                                    .build() : null) // 유저 정보가 없으면 author 자체를 null
                            .createdAt(TimeUtil.formatTimeAgo(post.getCreatedAt()))
                            .likeCount(post.getPostLikes() != null ? post.getPostLikes().size() : 0)
                            .commentCount(post.getComments() != null ? post.getComments().size() : 0)
                            .isLiked(isLiked)
                            .isBlocked(false)
                            .build();
                })
                .collect(Collectors.toList());

        // 다음 조회를 위한 마지막 ID 추출
        Long nextLastPostId = postInfos.isEmpty() ? null : postInfos.get(postInfos.size() - 1).postId();

        return PostResponseDTO.PostListResponse.builder()
                .posts(postInfos)
                .hasNext(postSlice.hasNext())
                .lastPostId(nextLastPostId)
                .build();
    }
}
