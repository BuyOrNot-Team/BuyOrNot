package com.basic.buyornot.config;

import com.basic.buyornot.entity.Member;
import com.basic.buyornot.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {

    private final MemberService memberService;

    @ModelAttribute("navMember")
    public Member addNavMember() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserDetails userDetails)) {
            return null;
        }
        try {
            return memberService.findByUsername(userDetails.getUsername());
        } catch (Exception e) {
            return null;
        }
    }
}