package com.basic.buyornot.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("PENDING_OAUTH_INFO") != null) {
            // 신규 사용자: 인증 상태 제거 후 OAuth 회원가입 폼으로 이동
            session.removeAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
            SecurityContextHolder.clearContext();
            response.sendRedirect(request.getContextPath() + "/signup/oauth");
            return;
        }

        clearAuthenticationAttributes(request);
        response.sendRedirect(request.getContextPath() + "/");
    }
}