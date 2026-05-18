package com.basic.buyornot.service;

import com.basic.buyornot.dto.QuestionDetailDTO;
import com.basic.buyornot.dto.QuestionFormDTO;
import com.basic.buyornot.entity.Member;
import com.basic.buyornot.entity.Question;
import com.basic.buyornot.repository.MemberRepository;
import com.basic.buyornot.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final MemberRepository memberRepository;

    @Value("${file.upload.path}")
    private String uploadPath;

    @Autowired
    public QuestionService(QuestionRepository questionRepository, MemberRepository memberRepository) {
        this.questionRepository = questionRepository;
        this.memberRepository = memberRepository;
    }

    // 상세 조회 (조회수 +1 포함)
    @Transactional
    public QuestionDetailDTO getQuestion(Long id) {
        questionRepository.incrementViewCount(id);
        Question question = questionRepository.findByIdWithMember(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 질문입니다. id=" + id));
        return new QuestionDetailDTO(question);
    }

    // 등록
    @Transactional
    public Long create(QuestionFormDTO dto, Long memberId) throws IOException {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. id=" + memberId));

        // 이미지 파일이 있으면 저장 후 경로를 imageUrl로 사용, 없으면 null
        String imageUrl = null;
        MultipartFile imageFile = dto.getImageFile();
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = saveImage(imageFile);
        }

        Question question = Question.create(
                member,
                dto.getTitle(),
                dto.getContent(),
                dto.getProductName(),
                dto.getPrice(),
                dto.getPros(),
                dto.getCons(),
                imageUrl
        );

        return questionRepository.save(question).getQuestionId();
    }

    // 수정
    @Transactional
    public void update(Long id, QuestionFormDTO dto, Long memberId) throws IOException {
        Question question = questionRepository.findByIdWithMember(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 질문입니다. id=" + id));

        if (!question.getMember().getMemberId().equals(memberId)) {
            throw new SecurityException("수정 권한이 없습니다.");
        }

        // 새 이미지가 있으면 교체, 없으면 기존 imageUrl 유지
        String imageUrl = question.getImageUrl();
        MultipartFile imageFile = dto.getImageFile();
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = saveImage(imageFile);
        }

        question.update(
                dto.getTitle(),
                dto.getContent(),
                dto.getProductName(),
                dto.getPrice(),
                dto.getPros(),
                dto.getCons(),
                imageUrl
        );
    }

    // 삭제
    @Transactional
    public void delete(Long id, Long memberId) {
        Question question = questionRepository.findByIdWithMember(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 질문입니다. id=" + id));

        if (!question.getMember().getMemberId().equals(memberId)) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }

        questionRepository.delete(question);
    }

    public List<Question> searchQuestions(Pageable pageable) {
        return questionRepository.findAll(pageable).toList();
    }

    // 이미지 파일 저장 (내부 전용)
    private String saveImage(MultipartFile image) throws IOException {
        String fileName = UUID.randomUUID().toString().replace("-", "")
                + "-" + image.getOriginalFilename();

        Path path = Paths.get(uploadPath + fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, image.getBytes());

        return "/uploads/productImages/" + fileName;  // DB 저장용 경로
    }
}
