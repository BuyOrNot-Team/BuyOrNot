package com.basic.buyornot.dto;

import com.basic.buyornot.entity.Question;
import lombok.Getter;

import java.time.LocalDateTime;

// 질문 상세 화면용 DTO - 전체 필드 포함
@Getter
public class QuestionDetailDTO {

    private final Long questionId;
    private final String title;
    private final String content;
    private final String productName;
    private final Integer price;
    private final String pros;
    private final String cons;
    private final String imageUrl;
    private final Long memberId;
    private final String nickname;         // member.nickname
    private final int viewCount;
    private final Long acceptedAnswerId;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public QuestionDetailDTO(Question q) {
        this.questionId = q.getQuestionId();
        this.title = q.getTitle();
        this.content = q.getContent();
        this.productName = q.getProductName();
        this.price = q.getPrice();
        this.pros = q.getPros();
        this.cons = q.getCons();
        this.imageUrl = q.getImageUrl();
        this.memberId = q.getMember().getMemberId();
        this.nickname = q.getMember().getNickname();
        this.viewCount = q.getViewCount();
        this.acceptedAnswerId = q.getAcceptedAnswerId();
        this.createdAt = q.getCreatedAt();
        this.updatedAt = q.getUpdatedAt();
    }
}
