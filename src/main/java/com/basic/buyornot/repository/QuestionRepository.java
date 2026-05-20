package com.basic.buyornot.repository;

import com.basic.buyornot.dto.SimpleQuestionDTO;
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

    @Query(
            value = "SELECT new com.basic.buyornot.dto.SimpleQuestionDTO(" +
                    "  q.id, " +
                    "  q.title, " +
                    "  q.price, " +
                    "  COUNT(a.id), " +
                    "  COUNT(CASE WHEN a.recommendation = 'BUY' THEN 1 END), " +    // <-- 💡 패키지 경로 대신 단순 문자열 비교
                    "  COUNT(CASE WHEN a.recommendation = 'DONT_BUY' THEN 1 END), " +
                    "  q.createdAt" +
                    ") " +
                    "FROM Question q " +
                    "LEFT JOIN Answer a ON a.question = q " +
                    "WHERE q.title LIKE CONCAT('%', :title, '%') " +
                    "GROUP BY q.id, q.title, q.price, q.createdAt",
            countQuery = "SELECT COUNT(q) FROM Question q WHERE q.title LIKE CONCAT('%', :title, '%')"
    )
    Page<SimpleQuestionDTO> findByTitleContainingWithVoteCounts(
            @Param("title") String title,
            Pageable pageable
    );

    List<Question> findByMemberOrderByCreatedAtDesc(Member member);
}
