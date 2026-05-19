package com.basic.buyornot.controller;

import com.basic.buyornot.dto.MemberDeleteDTO;
import com.basic.buyornot.dto.MemberEditDTO;
import com.basic.buyornot.entity.Member;
import com.basic.buyornot.entity.Question;
import com.basic.buyornot.service.MemberService;
import com.basic.buyornot.service.QuestionService;

import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class MyPageController {

    private final MemberService memberService;
    private final QuestionService questionService;

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

    @GetMapping("/mypage/activity")
    public String activityPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Member member = memberService.findByUsername(userDetails.getUsername());
        List<Question> myQuestions = questionService.getMyQuestions(member);
        model.addAttribute("myQuestions", myQuestions);
        model.addAttribute("questionCount", myQuestions.size());
        // TODO: 답변 구현 후 활성화
        // List<Answer> myAnswers = answerService.getMyAnswers(member);
        // model.addAttribute("myAnswers", myAnswers);
        // model.addAttribute("answerCount", myAnswers.size());
        model.addAttribute("answerCount", 0);
        return "member/activity";
    }

    // 내 정보 수정

    @GetMapping("/mypage/edit/verify")
    public String verifyForm(@AuthenticationPrincipal UserDetails userDetails, HttpSession session) {
        Member member = memberService.findByUsername(userDetails.getUsername());
        if (member.getPassword() == null || session.getAttribute("EDIT_VERIFIED") != null) {
            return "redirect:/mypage/edit";
        }
        return "member/edit_verify";
    }

    @PostMapping("/mypage/edit/verify")
    public String verifyPassword(@AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam String password,
                                 HttpSession session,
                                 Model model) {
        try {
            memberService.verifyPassword(userDetails.getUsername(), password);
            session.setAttribute("EDIT_VERIFIED", true);
            return "redirect:/mypage/edit";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/edit_verify";
        }
    }

    @GetMapping("/mypage/edit")
    public String editForm(@AuthenticationPrincipal UserDetails userDetails, HttpSession session, Model model) {
        Member member = memberService.findByUsername(userDetails.getUsername());

        if (member.getPassword() != null && session.getAttribute("EDIT_VERIFIED") == null) {
            return "redirect:/mypage/edit/verify";
        }

        MemberEditDTO dto = new MemberEditDTO();
        dto.setNickname(member.getNickname());
        dto.setEmail(member.getEmail());
        model.addAttribute("editForm", dto);
        model.addAttribute("member", member);
        return "member/edit";
    }

    @PostMapping("/mypage/edit")
    public String updateMemberInfo(@Valid @ModelAttribute("editForm") MemberEditDTO dto,
                                   BindingResult bindingResult,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   HttpSession session,
                                   Model model) {
        Member member = memberService.findByUsername(userDetails.getUsername());
        model.addAttribute("member", member);

        if (dto.getNewPassword() != null && !dto.getNewPassword().isBlank()) {
            if (dto.getNewPassword().length() < 8) {
                bindingResult.rejectValue("newPassword", "size", "비밀번호는 8자 이상이어야 합니다.");
            } else if (!dto.getNewPassword().equals(dto.getNewPasswordConfirm())) {
                bindingResult.rejectValue("newPasswordConfirm", "mismatch", "비밀번호가 일치하지 않습니다.");
            }
        }

        if (bindingResult.hasErrors()) {
            return "member/edit";
        }

        try {
            memberService.updateMemberInfo(userDetails.getUsername(), dto);
            session.removeAttribute("EDIT_VERIFIED");
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/edit";
        }

        return "redirect:/mypage";
    }

    // 회원 탈퇴
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