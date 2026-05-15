package com.basic.buyornot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "question")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long questionId;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column
    private Integer price;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String pros;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String cons;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    // FK: member_id → Member
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "view_count", nullable = false)
    private int viewCount = 0;

    // 순환 FK: accepted_answer_id → Answer (단방향)
    @Column(name = "accepted_answer_id")
    private Long acceptedAnswerId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 조회수 +1
    public void increaseViewCount() {
        this.viewCount++;
    }

    // 등록용 정적 팩토리
    public static Question create(Member member, String title, String content, String productName, Integer price, String pros, String cons, String imageUrl) {
        Question q = new Question();
        q.member = member;
        q.title = title;
        q.content = content;
        q.productName = productName;
        q.price = price;
        q.pros = pros;
        q.cons = cons;
        q.imageUrl = imageUrl;
        q.viewCount = 0;
        return q;
    }

    // 수정용 메서드
    public void update(String title, String content, String productName, Integer price, String pros, String cons, String imageUrl) {
        this.title = title;
        this.content = content;
        this.productName = productName;
        this.price = price;
        this.pros = pros;
        this.cons = cons;
        this.imageUrl = imageUrl;
    }
}
