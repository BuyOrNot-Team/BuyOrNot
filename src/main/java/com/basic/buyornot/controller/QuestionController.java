package com.basic.buyornot.controller;

import com.basic.buyornot.dto.QuestionDetailDTO;
import com.basic.buyornot.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    // 상세
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        QuestionDetailDTO question = questionService.getQuestion(id);
        model.addAttribute("question", question);
        return "question/detail";
    }
}
