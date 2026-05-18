// domain/workout/dto/WorkoutUpdateRequest.java
//메모 수정할 때
package com.helper.backend.domain.workout.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WorkoutUpdateRequest {

  private String memo;
}