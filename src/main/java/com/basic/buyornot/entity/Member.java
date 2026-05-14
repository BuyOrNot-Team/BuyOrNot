package com.basic.buyornot.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(nullable = false, length = 50, unique = true)
    private String username;

    @Column(length = 255)
    private String password;                  // OAuth 가입자 NULL

    @Column(nullable = false, length = 30, unique = true)
    private String nickname;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(nullable = false, length = 20)
    private String role;                      // ROLE_USER / ROLE_ADMIN

    @Column(name = "consumer_type", nullable = false, length = 20)
    private String consumerType;              // SAVER / PLANNER / IMPULSIVE / VALUE / PREMIUM / MINIMALIST

    @Column(nullable = false, length = 20)
    private String provider;                  // LOCAL / GOOGLE

    @Column(name = "provider_id", length = 255)
    private String providerId;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
