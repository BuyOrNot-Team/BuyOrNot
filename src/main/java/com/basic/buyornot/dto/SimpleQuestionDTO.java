package com.basic.buyornot.dto;

import com.basic.buyornot.util.StringFormatter;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SimpleQuestionDTO {
    private Long questionId;
    private String productCategory;
    private String title;
    private String price;
    private Integer buyCount;
    private Integer notBuyCount;
    private Integer answerCount;
    private LocalDateTime createAt;

    public String getBuyRatioPercentage() {
        return String.format("%.1f", getBuyCount()*100.0 / getVoteCount());
    }

    public String getNotBuyRatioPercentage() {return String.format("%.1f", getNotBuyCount()*100.0 / getVoteCount());}

    public Integer getVoteCount() {
        return getBuyCount() + getNotBuyCount();
    }

    public String getTimeAgo() {
        return StringFormatter.formatTimeAgo(createAt);
    }

    public SimpleQuestionDTO(Long questionId, String productCategory, String title, Integer price, Integer buyCount, Integer notBuyCount, Integer answerCount, LocalDateTime createAt) {
        this.questionId = questionId;
        this.productCategory = productCategory;
        this.title = title;
        this.price = StringFormatter.formatPrice(price);
        this.buyCount = buyCount;
        this.notBuyCount = notBuyCount;
        this.answerCount = answerCount;
        this.createAt = createAt;
    }
}
