package com.basic.buyornot.repository;

import com.basic.buyornot.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    // 특정 질문의 삭제되지 않은 답변 목록 조회 (member FETCH JOIN)
    @Query("SELECT a FROM Answer a JOIN FETCH a.member WHERE a.question.questionId = :questionId AND a.deleted = false ORDER BY a.createdAt ASC")
    List<Answer> findByQuestionIdWithMember(@Param("questionId") Long questionId);

    // 단건 조회 (수정/삭제용, member + question FETCH JOIN)
    @Query("SELECT a FROM Answer a JOIN FETCH a.member JOIN FETCH a.question WHERE a.answerId = :id")
    Optional<Answer> findByIdWithMember(@Param("id") Long id);
}
