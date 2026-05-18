package com.basic.buyornot.service;

import com.basic.buyornot.config.PrincipalDetails;
import com.basic.buyornot.dto.MemberEditDTO;
import com.basic.buyornot.dto.MemberFormDTO;
import com.basic.buyornot.dto.OAuthMemberFormDTO;
import com.basic.buyornot.entity.Member;
import com.basic.buyornot.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않거나 탈퇴한 사용자입니다."));
        return new PrincipalDetails(member);
    }

    @Transactional
    public void signup(MemberFormDTO dto) {
        if (memberRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (memberRepository.existsByNickname(dto.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        Member member = Member.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .consumerType(dto.getConsumerType())
                .build();

        memberRepository.save(member);
    }

    @Transactional
    public void oauthSignup(OAuthMemberFormDTO dto, String email, String providerId) {
        if (memberRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (memberRepository.existsByNickname(dto.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        Member member = Member.builder()
                .username(dto.getUsername())
                .nickname(dto.getNickname())
                .email(email)
                .consumerType(dto.getConsumerType())
                .provider(Member.Provider.GOOGLE)
                .providerId(providerId)
                .build();

        memberRepository.save(member);
    }

    public Member findByUsername(String username) {
        return memberRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    @Transactional
    public void updateConsumerType(String username, Member.ConsumerType consumerType) {
        Member member = memberRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        member.setConsumerType(consumerType);
        memberRepository.save(member);
    }

    public void verifyPassword(String username, String rawPassword) {
        Member member = memberRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        if (member.getPassword() == null || !passwordEncoder.matches(rawPassword, member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    @Transactional
    public void updateMemberInfo(String username, MemberEditDTO dto) {
        Member member = memberRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!member.getNickname().equals(dto.getNickname())
                && memberRepository.existsByNickname(dto.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        member.setNickname(dto.getNickname());
        member.setEmail(dto.getEmail());

        if (dto.getNewPassword() != null && !dto.getNewPassword().isBlank()) {
            member.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        }

        memberRepository.save(member);
    }

    @Transactional
    public void deleteMember(String username, String rawPassword) {
        Member member = memberRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (member.getPassword() == null || !passwordEncoder.matches(rawPassword, member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        member.setDeleted(true);
        memberRepository.save(member);
    }
}