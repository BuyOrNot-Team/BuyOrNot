package com.basic.buyornot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "answer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long answerId;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "recommendation", nullable = false, length = 20)
    private Recommendation recommendation;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    public enum Recommendation {
        BUY, DONT_BUY
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 정적 팩토리
    public static Answer create(Question question, Member member, String content, Recommendation recommendation) {
        Answer a = new Answer();
        a.question = question;
        a.member = member;
        a.content = content;
        a.recommendation = recommendation;
        return a;
    }

    // 수정
    public void update(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    // 소프트 딜리트
    public void softDelete() {
        this.deleted = true;
    }
}
