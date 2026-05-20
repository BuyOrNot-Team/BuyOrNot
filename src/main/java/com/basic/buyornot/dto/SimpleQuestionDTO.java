package com.basic.buyornot.dto;

import com.basic.buyornot.util.StringFormatter;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SimpleQuestionDTO {
    private Long questionId;
    private String title;
    private String price;
    private Long answerCount;
    private Long buyCount;
    private Long notBuyCount;
    private LocalDateTime createAt;

    public String getBuyRatioPercentage() {
        return getVoteCount() != 0 ? String.format("%.1f", getBuyCount()*100.0 / getVoteCount()) : "0";
    }

    public String getNotBuyRatioPercentage() {return getVoteCount() != 0 ? String.format("%.1f", getNotBuyCount()*100.0 / getVoteCount()) : "0";}

    public Long getVoteCount() {
        return getBuyCount() + getNotBuyCount();
    }

    public String getTimeAgo() {
        return StringFormatter.formatTimeAgo(createAt);
    }

    public SimpleQuestionDTO(Long questionId, String title, Integer price, Long answerCount, Long buyCount, Long notBuyCount, LocalDateTime createAt) {
        this.questionId = questionId;
        this.title = title;
        this.price = StringFormatter.formatPrice(price);
        this.buyCount = buyCount;
        this.notBuyCount = notBuyCount;
        this.answerCount = answerCount;
        this.createAt = createAt;
    }
}
