package com.basic.buyornot.dto;

import com.basic.buyornot.entity.Member;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MemberFormDTO {

    @NotBlank(message = "아이디를 입력하세요.")
    @Size(min = 4, message = "아이디는 4자 이상이어야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "아이디는 영문자와 숫자만 사용할 수 있습니다.")
    private String username;

    @NotBlank(message = "비밀번호를 입력하세요.")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인을 입력하세요.")
    private String passwordConfirm;

    @NotBlank(message = "닉네임을 입력하세요.")
    private String nickname;

    @NotBlank(message = "이메일을 입력하세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotNull(message = "소비패턴을 선택하세요.")
    private Member.ConsumerType consumerType;
}
