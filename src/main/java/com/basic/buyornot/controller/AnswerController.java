package com.basic.buyornot.controller;

import com.basic.buyornot.config.PrincipalDetails;
import com.basic.buyornot.dto.AnswerDetailDTO;
import com.basic.buyornot.dto.AnswerFormDTO;
import com.basic.buyornot.service.AnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    // 등록
    @PostMapping("/new/{questionId}")
    public String create(@PathVariable Long questionId,
                         @Valid @ModelAttribute AnswerFormDTO answerFormDTO,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal PrincipalDetails principal) {
        if (bindingResult.hasErrors()) {
            return "redirect:/questions/" + questionId;
        }
        answerService.create(questionId, principal.getMember().getMemberId(), answerFormDTO);
        return "redirect:/questions/" + questionId;
    }

    // 수정 폼
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        AnswerDetailDTO answer = answerService.getAnswer(id);
        AnswerFormDTO form = new AnswerFormDTO();
        form.setContent(answer.getContent());
        form.setRecommendation(answer.getRecommendation()); // 기존 살까/말까 값 유지
        model.addAttribute("answer", answer);
        model.addAttribute("answerFormDTO", form);
        return "answer/edit";
    }

    // 수정 처리
    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       @Valid @ModelAttribute AnswerFormDTO answerFormDTO,
                       BindingResult bindingResult,
                       @AuthenticationPrincipal PrincipalDetails principal,
                       Model model) {
        if (bindingResult.hasErrors()) {
            AnswerDetailDTO answer = answerService.getAnswer(id);
            model.addAttribute("answer", answer);
            return "answer/edit";
        }
        AnswerDetailDTO answer = answerService.getAnswer(id);
        answerService.update(id, principal.getMember().getMemberId(), answerFormDTO);
        return "redirect:/questions/" + answer.getQuestionId();
    }

    // 답변 채택 (채택/취소 토글)
    @PostMapping("/{answerId}/accept/{questionId}")
    public String accept(@PathVariable Long answerId,
                         @PathVariable Long questionId,
                         @AuthenticationPrincipal PrincipalDetails principal) {
        answerService.accept(questionId, answerId, principal.getMember().getMemberId());
        return "redirect:/questions/" + questionId;
    }

    // 삭제 (소프트 딜리트)
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal PrincipalDetails principal) {
        Long questionId = answerService.delete(id, principal.getMember().getMemberId());
        return "redirect:/questions/" + questionId;
    }
}
