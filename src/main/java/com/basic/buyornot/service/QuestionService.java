package com.basic.buyornot.service;

import com.basic.buyornot.dto.QuestionDetailDTO;
import com.basic.buyornot.entity.Question;
import com.basic.buyornot.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;

    @Autowired
    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    // 상세 조회 (조회수 +1 포함)
    @Transactional
    public QuestionDetailDTO getQuestion(Long id) {
        questionRepository.incrementViewCount(id);
        Question question = questionRepository.findByIdWithMember(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 질문입니다. id=" + id));
        return new QuestionDetailDTO(question);
    }
}
