// domain/workout/dto/WorkoutCreateRequest.java
// 운동 세션 생성할 때 클라이언트가 보내는 것
package com.helper.backend.domain.workout.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class WorkoutCreateRequest {

  @NotNull(message = "날짜는 필수입니다.")
  private LocalDate workoutDate;

  private String memo;
}