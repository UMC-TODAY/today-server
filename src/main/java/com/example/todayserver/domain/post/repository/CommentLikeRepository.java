package com.example.todayserver.domain.post.repository;

import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.post.entity.Comment;
import com.example.todayserver.domain.post.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByMemberAndComment(Member member, Comment comment);
}