// domain/workout/dto/WorkoutSetUpdateRequest.java
//무게/횟수 수정할 때
package com.helper.backend.domain.workout.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WorkoutSetUpdateRequest {

  @NotNull
  @Positive
  private Double weight;

  @NotNull
  @Positive
  private Integer reps;

  private Integer restTime;
  private Integer rpe;
}