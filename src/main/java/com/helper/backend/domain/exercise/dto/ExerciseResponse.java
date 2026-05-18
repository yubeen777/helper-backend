// domain/exercise/dto/ExerciseResponse.java
package com.helper.backend.domain.exercise.dto;

import com.helper.backend.domain.exercise.entity.Exercise;
import lombok.Getter;

@Getter
public class ExerciseResponse {

  private Long id;
  private String name;
  private String bodyPart;

  public ExerciseResponse(Exercise exercise) {
    this.id = exercise.getId();
    this.name = exercise.getName();
    this.bodyPart = exercise.getBodyPart().name();
  }
}