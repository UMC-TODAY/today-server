package com.example.todayserver.domain.post.repository;

import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.post.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostIdOrderByCreatedAtDesc(Long postId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.member = :member")
    Long countAllCommentsByPostMember(@Param("member") Member member);
}