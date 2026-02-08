package com.example.todayserver.domain.member.repository;

import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.member.enums.SocialType;
import com.example.todayserver.domain.member.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);
    Optional<Member> findByEmail(String email);
    Optional<Member> findBySocialTypeAndProviderUserIdAndStatus(SocialType socialType, String providerId, Status status);
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
    Optional<Member> findByDeletedStatus(@Param("email") String email);

    @Modifying
    @Query("""
        UPDATE Member m
        SET m.password = :pw
        WHERE m.id = :id
    """)
    void updatePassword(@Param("pw") String password,@Param("id") Long id);

    @Modifying
    @Query("""
        UPDATE Member m
        SET m.nickname = :nickname,
            m.profileImage = :profileImage
        WHERE m.id = :id
    """)
    void updateProfile(@Param("profileImage") String profileImage, @Param("nickname") String nickname, @Param("id") Long id);

}