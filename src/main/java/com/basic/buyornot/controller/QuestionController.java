package com.basic.buyornot.controller;

import com.basic.buyornot.dto.QuestionDetailDTO;
import com.basic.buyornot.dto.QuestionFormDTO;
import com.basic.buyornot.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("questionFormDTO", new QuestionFormDTO());
        return "question/form";
    }

    // 등록 처리
    @PostMapping("/new")
    public String create(@Valid @ModelAttribute QuestionFormDTO questionFormDTO,
                         BindingResult bindingResult,
                         Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            return "question/form";
        }
        Long savedId = questionService.create(questionFormDTO, TEMP_MEMBER_ID);
        return "redirect:/questions/" + savedId;
    }
}
