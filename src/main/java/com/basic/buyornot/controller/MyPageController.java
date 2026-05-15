package com.basic.buyornot.controller;

import com.basic.buyornot.dto.MemberDeleteDTO;
import com.basic.buyornot.entity.Member;
import com.basic.buyornot.service.MemberService;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MyPageController {

    private final MemberService memberService;

    @GetMapping("/mypage")
    public String myPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Member member = memberService.findByUsername(userDetails.getUsername());
        model.addAttribute("member", member);
        model.addAttribute("consumerTypes", Member.ConsumerType.values());
        return "member/mypage";
    }

    @PostMapping("/mypage/consumer-type")
    public String updateConsumerType(@RequestParam Member.ConsumerType consumerType,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        memberService.updateConsumerType(userDetails.getUsername(), consumerType);
        return "redirect:/mypage";
    }

    @GetMapping("/member/delete")
    public String deleteForm(Model model) {
        model.addAttribute("deleteForm", new MemberDeleteDTO());
        return "member/delete";
    }

    @PostMapping("/member/delete")
    public String deleteMember(@Valid @ModelAttribute("deleteForm") MemberDeleteDTO dto,
                               BindingResult bindingResult,
                               @AuthenticationPrincipal UserDetails userDetails,
                               HttpServletRequest request,
                               Model model) {
        if (bindingResult.hasErrors()) {
            return "member/delete";
        }

        try {
            memberService.deleteMember(userDetails.getUsername(), dto.getPassword());
            request.getSession().invalidate();
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/delete";
        }

        return "redirect:/";
    }
}
