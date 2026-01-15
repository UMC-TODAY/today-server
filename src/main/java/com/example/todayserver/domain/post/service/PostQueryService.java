package com.example.todayserver.domain.post.service;

import com.example.todayserver.domain.post.dto.PostResponseDTO;
import com.example.todayserver.domain.post.entity.Post;
import com.example.todayserver.domain.post.repository.PostRepository;
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

    public PostResponseDTO.PostListResponse getPostFeed(Long lastPostId, int size) {
        PageRequest pageRequest = PageRequest.of(0, size);
        Slice<Post> postSlice = postRepository.findPostsForFeed(lastPostId, pageRequest);

        List<PostResponseDTO.PostInfo> postInfos = postSlice.getContent().stream()
                .map(post -> PostResponseDTO.PostInfo.builder()
                        .postId(post.getId())
                        .content(post.getContent())
                        .author(PostResponseDTO.AuthorInfo.builder()
                                .userId(post.getMember().getId())
                                .nickname(post.getMember().getNickname())
                                .profileImageUrl(post.getMember().getProfileImage())
                                .build())
                        .createdAt(post.getCreatedAt().toString()) // 나중에 "n시간 전" 로직 추가
                        .likeCount(0) // 연관 관계 설정 후 구현
                        .commentCount(0) // 연관 관계 설정 후 구현
                        .isLiked(false)
                        .isBlocked(false)
                        .build())
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