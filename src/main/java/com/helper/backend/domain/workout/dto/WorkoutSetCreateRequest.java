// domain/workout/dto/WorkoutSetCreateRequest.java
//세트 추가할 때
package com.helper.backend.domain.workout.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WorkoutSetCreateRequest {

  @NotNull(message = "운동 종목은 필수입니다.")
  private Long exerciseId;

  @NotNull(message = "무게는 필수입니다.")
  @Positive(message = "무게는 양수여야 합니다.")
  private Double weight;

  @NotNull(message = "횟수는 필수입니다.")
  @Positive(message = "횟수는 양수여야 합니다.")
  private Integer reps;

  private Integer restTime;
  private Integer rpe;
}