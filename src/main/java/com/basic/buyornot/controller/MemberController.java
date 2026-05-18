package com.basic.buyornot.controller;

import com.basic.buyornot.dto.MemberFormDTO;
import com.basic.buyornot.dto.OAuthMemberFormDTO;
import com.basic.buyornot.entity.Member;
import com.basic.buyornot.service.MemberService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

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

    @GetMapping("/signup/oauth")
    public String oauthSignupForm(HttpSession session, Model model) {
        @SuppressWarnings("unchecked")
        Map<String, String> pendingInfo = (Map<String, String>) session.getAttribute("PENDING_OAUTH_INFO");
        if (pendingInfo == null) {
            return "redirect:/login";
        }
        OAuthMemberFormDTO form = new OAuthMemberFormDTO();
        form.setNickname(pendingInfo.get("name"));
        model.addAttribute("oauthForm", form);
        model.addAttribute("consumerTypes", Member.ConsumerType.values());
        model.addAttribute("oauthEmail", pendingInfo.get("email"));
        return "member/oauth_signup";
    }

    @PostMapping("/signup/oauth")
    public String oauthSignup(@Valid @ModelAttribute("oauthForm") OAuthMemberFormDTO dto,
                              BindingResult bindingResult,
                              HttpSession session,
                              Model model) {
        @SuppressWarnings("unchecked")
        Map<String, String> pendingInfo = (Map<String, String>) session.getAttribute("PENDING_OAUTH_INFO");
        if (pendingInfo == null) {
            return "redirect:/login";
        }

        model.addAttribute("consumerTypes", Member.ConsumerType.values());
        model.addAttribute("oauthEmail", pendingInfo.get("email"));

        if (bindingResult.hasErrors()) {
            return "member/oauth_signup";
        }

        try {
            memberService.oauthSignup(dto, pendingInfo.get("email"), pendingInfo.get("providerId"));
            session.removeAttribute("PENDING_OAUTH_INFO");
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/oauth_signup";
        }

        return "redirect:/login?oauth=signup";
    }
}