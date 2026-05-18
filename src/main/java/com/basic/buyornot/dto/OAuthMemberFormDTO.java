package com.basic.buyornot.dto;

import com.basic.buyornot.entity.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OAuthMemberFormDTO {

    @NotBlank(message = "아이디를 입력하세요.")
    @Size(min = 4, message = "아이디는 4자 이상이어야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "아이디는 영문자와 숫자만 사용할 수 있습니다.")
    private String username;

    @NotBlank(message = "닉네임을 입력하세요.")
    private String nickname;

    @NotNull(message = "소비패턴을 선택하세요.")
    private Member.ConsumerType consumerType;
}