package com.basic.buyornot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionFormDTO {

  @NotBlank(message = "제목을 입력해주세요.")
  @Size(max = 200, message = "제목은 200자 이내로 입력해주세요.")
  private String title;

  @NotBlank(message = "내용을 입력해주세요.")
  private String content;

  @NotBlank(message = "상품명을 입력해주세요.")
  @Size(max = 100, message = "상품명은 100자 이내로 입력해주세요.")
  private String productName;

  @PositiveOrZero(message = "가격은 0 이상이어야 합니다.")
  private Integer price;

  private String pros;

  private String cons;

  // 파일 업로드용 - 폼에서 multipart로 전송, 없으면 null
  private MultipartFile imageFile;
}