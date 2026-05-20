package com.basic.buyornot.repository;

import com.basic.buyornot.entity.Member;
import com.basic.buyornot.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    // 단건 조회 - member FETCH JOIN (상세 화면용)
    @Query("SELECT q FROM Question q JOIN FETCH q.member WHERE q.questionId = :id")
    Optional<Question> findByIdWithMember(@Param("id") Long id);

    // 조회수 +1
    @Modifying
    @Query("UPDATE Question q SET q.viewCount = q.viewCount + 1 WHERE q.questionId = :id")
    void incrementViewCount(@Param("id") Long id);

    Page<Question> findByTitleContaining(String searchText, Pageable pageable);

    List<Question> findByMemberOrderByCreatedAtDesc(Member member);
}
