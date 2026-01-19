package com.example.todayserver.domain.post.service;

import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.post.dto.PostRequestDTO;
import com.example.todayserver.domain.post.entity.*;
import com.example.todayserver.domain.post.repository.*;
import com.example.todayserver.global.common.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostCommandService {

    private final PostRepository postRepository;
    private final ReportRepository reportRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentLikeRepository commentLikeRepository;

    public Post createPost(PostRequestDTO.CreatePostDTO request, Member loginMember) {
        Post newPost = Post.builder()
                .content(request.getContent())
                .member(loginMember) // 작성자 정보 연결
                .viewCount(0)
                .build();
        return postRepository.save(newPost);
    }

    public void createComment(Member member, Long postId, PostRequestDTO.CreateCommentDTO request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .member(member)
                .post(post)
                .build();

        commentRepository.save(comment);
    }

    public void reportPost(Member member, Long postId) {
        // 신고 대상 게시글 존재 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("해당 게시글을 찾을 수 없습니다."));

        // 신고 객체 생성 및 저장
        Report report = Report.builder()
                .member(member)
                .post(post)
                .build();

        reportRepository.save(report);
    }

    // 피드 좋아요 토글
    public String togglePostLike(Member member, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 좋아요를 눌렀는지 확인
        Optional<PostLike> postLike = postLikeRepository.findByMemberAndPost(member, post);

        if (postLike.isPresent()) {
            // 이미 있다면 좋아요 취소 
            postLikeRepository.delete(postLike.get());
            return "좋아요 취소 성공";
        } else {
            // 좋아요 없다면 등록
            PostLike newLike = PostLike.builder()
                    .member(member)
                    .post(post)
                    .build();
            postLikeRepository.save(newLike);
            return "좋아요 성공";
        }
    }

    // 댓글 좋아요 토글
    public String toggleCommentLike(Member member, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        Optional<CommentLike> commentLike = commentLikeRepository.findByMemberAndComment(member, comment);

        if (commentLike.isPresent()) {
            commentLikeRepository.delete(commentLike.get());
            return "댓글 좋아요 취소 성공";
        } else {
            CommentLike newLike = CommentLike.builder()
                    .member(member)
                    .comment(comment)
                    .build();
            commentLikeRepository.save(newLike);
            return "댓글 좋아요 성공";
        }
    }
}
