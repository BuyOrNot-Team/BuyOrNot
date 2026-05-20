package com.basic.buyornot.dto;

import com.basic.buyornot.entity.Answer;
import com.basic.buyornot.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class AnswerDetailDTO {

    private final Long answerId;
    private final String content;
    private final Answer.Recommendation recommendation;
    private final Long questionId;
    private final Long memberId;
    private final String nickname;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    // 답변자 소비 타입을 보여주기 위한 필드 추가
    private final Member.ConsumerType consumerType;

    public AnswerDetailDTO(Answer a) {
        this.answerId = a.getAnswerId();
        this.content = a.getContent();
        this.recommendation = a.getRecommendation();
        this.questionId = a.getQuestion().getQuestionId();
        this.memberId = a.getMember().getMemberId();
        this.nickname = a.getMember().getNickname();
        this.createdAt = a.getCreatedAt();
        this.updatedAt = a.getUpdatedAt();

        this.consumerType = a.getMember().getConsumerType();
    }
}
