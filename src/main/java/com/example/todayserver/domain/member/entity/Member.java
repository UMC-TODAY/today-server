package com.example.todayserver.domain.member.entity;

import com.example.todayserver.domain.member.enums.SocialType;
import com.example.todayserver.domain.member.enums.Status;
import com.example.todayserver.global.common.entity.BaseEntity;
import com.example.todayserver.global.oauth.info.OAuth2UserInfo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Map;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", length = 30, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "nickname", length = 20, unique = true)
    private String nickname;

    @Column(name = "birth")
    private LocalDate birth;

    @Column(name = "profile_image")
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_type", nullable = false)
    private SocialType socialType;

    @Column(name = "provider_user_id")
    private String providerUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.ACTIVATE;

    @Column(name = "inactivate_date")
    private LocalDate inactivateDate;

    public void updateFromOAuth(OAuth2UserInfo userInfo) {
        this.profileImage = userInfo.getProfileImage();
    }
}
