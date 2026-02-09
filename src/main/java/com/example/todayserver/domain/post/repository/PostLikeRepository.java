package com.example.todayserver.domain.post.repository;

import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.post.entity.Post;
import com.example.todayserver.domain.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByMemberAndPost(Member member, Post post);

    @Query("SELECT COUNT(pl) FROM PostLike pl WHERE pl.post.member = :member")
    Long countAllLikesByPostMember(@Param("member") Member member);
}