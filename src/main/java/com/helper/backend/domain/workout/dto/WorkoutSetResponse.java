//세트 응답
// domain/workout/dto/WorkoutSetResponse.java
package com.helper.backend.domain.workout.dto;

import com.helper.backend.domain.workout.entity.WorkoutSet;
import lombok.Getter;

@Getter
public class WorkoutSetResponse {

  private Long id;
  private Long exerciseId;
  private String exerciseName;
  private String bodyPart;
  private Integer setNumber;
  private Double weight;
  private Integer reps;
  private Integer restTime;
  private Integer rpe;

  public WorkoutSetResponse(WorkoutSet workoutSet) {
    this.id = workoutSet.getId();
    this.exerciseId = workoutSet.getExercise().getId();
    this.exerciseName = workoutSet.getExercise().getName();
    this.bodyPart = workoutSet.getExercise().getBodyPart().name();
    this.setNumber = workoutSet.getSetNumber();
    this.weight = workoutSet.getWeight();
    this.reps = workoutSet.getReps();
    this.restTime = workoutSet.getRestTime();
    this.rpe = workoutSet.getRpe();
  }
}