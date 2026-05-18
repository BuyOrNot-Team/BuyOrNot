package com.basic.buyornot.controller;

import com.basic.buyornot.dto.QuestionDetailDTO;
import com.basic.buyornot.dto.QuestionFormDTO;
import com.basic.buyornot.dto.QuestionSearchDTO;
import com.basic.buyornot.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    // TODO: Spring Security 연동 후 로그인 사용자 ID로 교체
    private static final Long TEMP_MEMBER_ID = 1L;

    // 상세
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        QuestionDetailDTO question = questionService.getQuestion(id);
        model.addAttribute("question", question);
        return "question/detail";
    }

    // 작성 폼
    @GetMapping("/form")
    public String createForm(Model model) {
        model.addAttribute("questionFormDTO", new QuestionFormDTO());
        return "question/form";
    }

    // 등록 처리
    @PostMapping("/form")
    public String create(@Valid @ModelAttribute QuestionFormDTO questionFormDTO,
                         BindingResult bindingResult,
                         Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            return "question/form";
        }
        Long savedId = questionService.create(questionFormDTO, TEMP_MEMBER_ID);
        return "redirect:/questions/" + savedId;
    }

    // 수정 폼
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        QuestionDetailDTO question = questionService.getQuestion(id);

        QuestionFormDTO dto = QuestionFormDTO.builder()
                .title(question.getTitle())
                .content(question.getContent())
                .productName(question.getProductName())
                .price(question.getPrice())
                .pros(question.getPros())
                .cons(question.getCons())
                .build();

        model.addAttribute("questionFormDTO", dto);
        model.addAttribute("questionId", id);
        model.addAttribute("existingImageUrl", question.getImageUrl());
        return "question/edit";
    }

    // 수정 처리
    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       @Valid @ModelAttribute QuestionFormDTO questionFormDTO,
                       BindingResult bindingResult,
                       Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            model.addAttribute("questionId", id);
            return "question/edit";
        }
        questionService.update(id, questionFormDTO, TEMP_MEMBER_ID);
        return "redirect:/questions/" + id;
    }

    // 삭제 처리
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        questionService.delete(id, TEMP_MEMBER_ID);
        return "redirect:/questions";
    }

    @GetMapping
    public String getQuestions(
            Model model,
            @RequestParam(name = "q", required = false, defaultValue = "") String searchText,
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        QuestionSearchDTO questionsDTO = questionService.searchQuestions(searchText, pageable);
        model.addAttribute("pageable", pageable);
        model.addAttribute("sortType", pageable.getSort().stream().findFirst().get().getProperty());
        model.addAttribute("questions", questionsDTO);
        model.addAttribute("searchText", searchText);
        return "question/list";
    }
}
