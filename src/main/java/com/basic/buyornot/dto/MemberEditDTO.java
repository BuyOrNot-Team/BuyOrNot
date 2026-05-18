package com.basic.buyornot.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MemberEditDTO {

    @NotBlank(message = "닉네임을 입력하세요.")
    private String nickname;

    @NotBlank(message = "이메일을 입력하세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    private String newPassword;

    private String newPasswordConfirm;
}