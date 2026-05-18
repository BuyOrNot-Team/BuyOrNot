package com.basic.buyornot.repository;

import com.basic.buyornot.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsernameAndDeletedFalse(String username);

    Optional<Member> findByEmailAndDeletedFalse(String email);

    boolean existsByUsername(String username);

    boolean existsByNickname(String nickname);
}
