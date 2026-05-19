package com.basic.buyornot.controller;

import com.basic.buyornot.config.PrincipalDetails;
import com.basic.buyornot.dto.QuestionDetailDTO;
import com.basic.buyornot.dto.QuestionFormDTO;
import com.basic.buyornot.dto.QuestionSearchDTO;
import com.basic.buyornot.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    // 상세
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model,
                         @AuthenticationPrincipal PrincipalDetails principal) {
        QuestionDetailDTO question = questionService.getQuestion(id);
        model.addAttribute("question", question);

        // 로그인 상태면 현재 사용자 ID를 넘겨서 작성자 여부 판단에 사용
        if (principal != null) {
            model.addAttribute("currentMemberId", principal.getMember().getMemberId());
        }
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
                         @AuthenticationPrincipal PrincipalDetails principal,
                         Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            return "question/form";
        }
        Long memberId = principal.getMember().getMemberId();
        Long savedId = questionService.create(questionFormDTO, memberId);
        return "redirect:/questions/" + savedId;
    }

    // 수정 폼
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model,
                           @AuthenticationPrincipal PrincipalDetails principal) {
        QuestionDetailDTO question = questionService.getQuestion(id);

        // 작성자 본인이 아니면 상세 페이지로 돌려보냄
        if (principal == null || !question.getMemberId().equals(principal.getMember().getMemberId())) {
            return "redirect:/questions/" + id;
        }

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
                       @AuthenticationPrincipal PrincipalDetails principal,
                       Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            model.addAttribute("questionId", id);
            return "question/edit";
        }
        Long memberId = principal.getMember().getMemberId();
        questionService.update(id, questionFormDTO, memberId);
        return "redirect:/questions/" + id;
    }

    // 삭제 처리
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal PrincipalDetails principal) {
        Long memberId = principal.getMember().getMemberId();
        questionService.delete(id, memberId);
        return "redirect:/questions";
    }

    // 목록
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
