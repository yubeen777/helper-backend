// domain/feedback/dto/AiFeedbackRequest.java
package com.helper.backend.domain.feedback.dto;

import com.helper.backend.domain.feedback.entity.FeedbackType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AiFeedbackRequest {

  @NotNull(message = "피드백 타입은 필수입니다.")
  private FeedbackType feedbackType;

  private Long workoutId; // SESSION 타입일 때만 필요
}