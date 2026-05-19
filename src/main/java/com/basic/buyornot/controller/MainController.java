package com.basic.buyornot.controller;

import com.basic.buyornot.dto.QuestionSearchDTO;
import com.basic.buyornot.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final QuestionService questionService;

    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(name = "q", required = false, defaultValue = "") String searchText,
                        // 하단 목록 사이즈를 6으로 설정, 기본 최신순 정렬
                        @PageableDefault(size = 8, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        model.addAttribute("hideNavbar", false);

        // 최신 등록 질문 상단 2개 추출
        QuestionSearchDTO latestResult = questionService.searchQuestions(
                "",
                PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt")));
        model.addAttribute("latestQuestions", latestResult.getQuestions());

        // 인기 질문 상단 2개 추출
        QuestionSearchDTO topResult = questionService.searchQuestions(
                "",
                PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "viewCount")));
        model.addAttribute("topQuestions", topResult.getQuestions());

        // 전체 목록에서 8개만 가져옴
        QuestionSearchDTO questionsDTO = questionService.searchQuestions(searchText, pageable);

        model.addAttribute("pageable", pageable);
        String sortType = pageable.getSort().stream()
                .findFirst()
                .map(Sort.Order::getProperty)
                .orElse("createdAt");

        model.addAttribute("sortType", sortType);
        model.addAttribute("questions", questionsDTO);
        model.addAttribute("searchText", searchText);

        return "main/index";
    }
}