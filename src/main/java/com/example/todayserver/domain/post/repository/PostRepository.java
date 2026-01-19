package com.example.todayserver.domain.post.repository;

import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.post.entity.Post;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {
    // 무한 스크롤 쿼리
    @Query("SELECT p FROM Post p WHERE (:lastPostId IS NULL OR p.id < :lastPostId) ORDER BY p.id DESC")
    Slice<Post> findPostsForFeed(@Param("lastPostId") Long lastPostId, PageRequest pageRequest);

    @Query("SELECT p FROM Post p WHERE p.member = :member AND (:lastPostId IS NULL OR p.id < :lastPostId) ORDER BY p.id DESC")
    Slice<Post> findMyPosts(@Param("member") Member member, @Param("lastPostId") Long lastPostId, Pageable pageable);

    Long countByMember(Member member);
}