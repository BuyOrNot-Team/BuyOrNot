package com.basic.buyornot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordVerifyDTO {

    @NotBlank(message = "비밀번호를 입력하세요.")
    private String password;
}