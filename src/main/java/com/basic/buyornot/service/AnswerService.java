package com.basic.buyornot.service;

import com.basic.buyornot.dto.AnswerDetailDTO;
import com.basic.buyornot.dto.AnswerFormDTO;
import com.basic.buyornot.entity.Answer;
import com.basic.buyornot.entity.Member;
import com.basic.buyornot.entity.Question;
import com.basic.buyornot.repository.AnswerRepository;
import com.basic.buyornot.repository.MemberRepository;
import com.basic.buyornot.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final MemberRepository memberRepository;

    // 답변 목록 조회
    public List<AnswerDetailDTO> getAnswers(Long questionId) {
        return answerRepository.findByQuestionIdWithMember(questionId)
                .stream()
                .map(AnswerDetailDTO::new)
                .collect(Collectors.toList());
    }

    // 단건 조회 (수정 폼용)
    public AnswerDetailDTO getAnswer(Long answerId) {
        Answer answer = answerRepository.findByIdWithMember(answerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 답변입니다. id=" + answerId));
        return new AnswerDetailDTO(answer);
    }

    // 등록
    @Transactional
    public void create(Long questionId, Long memberId, AnswerFormDTO dto) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 질문입니다. id=" + questionId));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. id=" + memberId));

        answerRepository.save(Answer.create(question, member, dto.getContent(), dto.getRecommendation()));
    }

    // 수정
    @Transactional
    public void update(Long answerId, Long memberId, AnswerFormDTO dto) {
        Answer answer = answerRepository.findByIdWithMember(answerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 답변입니다. id=" + answerId));

        if (!answer.getMember().getMemberId().equals(memberId)) {
            throw new SecurityException("수정 권한이 없습니다.");
        }

        answer.update(dto.getContent());
    }

    // 삭제 (소프트 딜리트) - questionId 반환하여 리다이렉트에 활용
    @Transactional
    public Long delete(Long answerId, Long memberId) {
        Answer answer = answerRepository.findByIdWithMember(answerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 답변입니다. id=" + answerId));

        if (!answer.getMember().getMemberId().equals(memberId)) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }

        Long questionId = answer.getQuestion().getQuestionId();
        answer.softDelete();
        return questionId;
    }

    // 답변 채택
    @Transactional
    public void accept(Long questionId, Long answerId, Long memberId) {
        Question question = questionRepository.findByIdWithMember(questionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 질문입니다. id=" + questionId));

        // 질문 작성자만 채택 가능
        if (!question.getMember().getMemberId().equals(memberId)) {
            throw new SecurityException("채택 권한이 없습니다.");
        }

        // 이미 채택된 답변이면 취소, 아니면 채택
        if (answerId.equals(question.getAcceptedAnswerId())) {
            question.cancelAccept();
        } else {
            // 채택하려는 답변이 해당 질문의 답변인지 검증
            answerRepository.findByIdWithMember(answerId)
                    .filter(a -> a.getQuestion().getQuestionId().equals(questionId))
                    .orElseThrow(() -> new IllegalArgumentException("해당 질문의 답변이 아닙니다."));
            question.acceptAnswer(answerId);
        }
    }

    // 사용자의 답변 조회
    public List<Answer> getMyAnswers(Member member) {
        return answerRepository.findByMemberOrderByCreatedAtDesc(member);
    }
}
