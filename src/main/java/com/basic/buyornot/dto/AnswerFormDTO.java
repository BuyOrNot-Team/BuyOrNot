package com.basic.buyornot.dto;

import com.basic.buyornot.entity.Answer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerFormDTO {

    @NotNull(message = "살까/말까를 선택해주세요.")
    private Answer.Recommendation recommendation;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;
}
