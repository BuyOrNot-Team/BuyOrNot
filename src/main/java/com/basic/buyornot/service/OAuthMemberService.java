package com.basic.buyornot.service;

import com.basic.buyornot.config.PrincipalDetails;
import com.basic.buyornot.entity.Member;
import com.basic.buyornot.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuthMemberService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauthUser = super.loadUser(userRequest);
        Map<String, Object> attributes = oauthUser.getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String providerId = (String) attributes.get("sub");

        Optional<Member> memberOpt = memberRepository.findByEmailAndDeletedFalse(email);

        if (memberOpt.isEmpty()) {
            // 신규 사용자: 세션에 구글 정보 저장 후 회원가입 폼으로 이동 (성공 핸들러에서 처리)
            HttpSession session = getSession();
            Map<String, String> pendingInfo = new HashMap<>();
            pendingInfo.put("email", email);
            pendingInfo.put("name", name);
            pendingInfo.put("providerId", providerId);
            session.setAttribute("PENDING_OAUTH_INFO", pendingInfo);
            return oauthUser; // 성공 핸들러에서 pending 상태 감지
        }

        Member member = memberOpt.get();

        if (member.getProvider() == Member.Provider.LOCAL) {
            // 기존 로컬 계정 → 구글 자동 연동
            member.setProvider(Member.Provider.GOOGLE);
            member.setProviderId(providerId);
            memberRepository.save(member);
            getSession().setAttribute("FLASH_SUCCESS",
                    "구글 계정이 기존 계정과 자동으로 연동되었습니다. 이제 구글로도 로그인할 수 있습니다.");
        }

        return new PrincipalDetails(member, attributes);
    }

    private HttpSession getSession() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attrs.getRequest().getSession();
    }
}