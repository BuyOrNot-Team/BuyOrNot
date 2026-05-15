package com.basic.buyornot.controller;

import com.basic.buyornot.dto.MemberFormDTO;
import com.basic.buyornot.entity.Member;
import com.basic.buyornot.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("memberForm", new MemberFormDTO());
        model.addAttribute("consumerTypes", Member.ConsumerType.values());
        return "member/signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("memberForm") MemberFormDTO dto,
                         BindingResult bindingResult,
                         Model model) {
        model.addAttribute("consumerTypes", Member.ConsumerType.values());
        // 비밀번호 일치 여부 검사 (password가 null이면 @NotBlank가 이미 에러 추가)
        if (dto.getPassword() != null && !dto.getPassword().equals(dto.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "passwordMismatch", "비밀번호가 일치하지 않습니다.");
        }

        if (bindingResult.hasErrors()) {
            return "member/signup";
        }

        try {
            memberService.signup(dto);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/signup";
        }

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "member/login";
    }
}
