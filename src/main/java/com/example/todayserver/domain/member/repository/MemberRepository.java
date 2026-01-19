package com.example.todayserver.domain.member.repository;

import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.member.enums.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);
    Optional<Member> findByEmail(String email);
    Optional<Member> findBySocialTypeAndProviderUserId(SocialType socialType, String providerId);
    boolean existsByNickname(String nickname);
    @Query(
            value = """
              select *
              from member
              where email = :email
                and status = 'DELETED'
              """,
            nativeQuery = true
    )
    Optional<Member> findByDeletedStatus(String email);

        @Modifying
        @Query(
                value = """
            UPDATE member
            SET email = NULL
            WHERE id = :id
            """,
                nativeQuery = true
        )
        void deletePrevEmail(@Param("id") Long id);
}