package com.basic.buyornot.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column
    private String password;

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(nullable = false, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Role role = Role.ROLE_USER;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Provider provider = Provider.LOCAL;

    // Google OAuth 구현 시 사용 (현재 LOCAL 전용)
    @Column(name = "provider_id")
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "consumer_type", nullable = false, length = 20)
    private ConsumerType consumerType;

    public enum Role {
        ROLE_USER, ROLE_ADMIN
    }

    public enum Provider {
        LOCAL, GOOGLE
    }

    public enum ConsumerType {
        MINIMALIST("무소비 인간", "진짜 필요한 거 아니면 지갑 안연다. 장바구니에 넣어두고 한 달 뒤에도 고민하는 타입", "#e74c3c"),
        SAVER("조금 짠내남", "아낄 수 있는만큼 최대한 아끼자! 작은 지출도 꼼꼼하게 따지고 할인 쿠폰, 중고거래 이용", "#e67e22"),
        VALUE("가성비 충실", "가격 대비 만족도가 제일 중요함. 물건 살때도 무조건 효율적이게", "#f1c40f"),
        PLANNER("계획한 소비만", "계획된 소비는 괜찮아. 예산과 소비계획에 따라 움직이고 충동구매는 안함", "#27ae60"),
        IMPULSIVE("충동구매왕", "잠깐, 일단 사고 생각하자! 결제할때와 택배 왔을때가 가장 큰 행복이지", "#3498db"),
        PREMIUM("프리미엄 컬렉터", "좋은건 비싸도 이유가 있다. 브랜드, 품질, 감성 중요시 여기는 프리미엄 소비자", "#7d3c98");

        private final String note;
        private final String description;
        private final String color;

        ConsumerType(String note, String description, String color) {
            this.note = note;
            this.description = description;
            this.color = color;
        }

        public String getNote() { return note; }
        public String getDescription() { return description; }
        public String getColor() { return color; }
    }
}
