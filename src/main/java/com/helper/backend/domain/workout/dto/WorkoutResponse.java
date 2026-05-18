// domain/workout/dto/WorkoutResponse.java
//서버가 클라이언트에게 돌려주는 것
package com.helper.backend.domain.workout.dto;

import com.helper.backend.domain.workout.entity.Workout;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class WorkoutResponse {

  private Long id;
  private LocalDate workoutDate;
  private String memo;
  private LocalDateTime createdAt;
  private List<WorkoutSetResponse> workoutSets;

  public WorkoutResponse(Workout workout, List<WorkoutSetResponse> workoutSets) {
    this.id = workout.getId();
    this.workoutDate = workout.getWorkoutDate();
    this.memo = workout.getMemo();
    this.createdAt = workout.getCreatedAt();
    this.workoutSets = workoutSets;
  }
}