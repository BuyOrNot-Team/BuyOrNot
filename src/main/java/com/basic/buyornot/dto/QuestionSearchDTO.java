package com.basic.buyornot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
@AllArgsConstructor
public class QuestionSearchDTO {
    private Long totalItemCount;
    private Integer totalPageCount;
    private Pageable pageable;
    private List<SimpleQuestionDTO> questions;
}


