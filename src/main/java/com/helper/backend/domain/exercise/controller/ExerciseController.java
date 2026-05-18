// domain/exercise/controller/ExerciseController.java
package com.helper.backend.domain.exercise.controller;

import com.helper.backend.domain.exercise.dto.ExerciseResponse;
import com.helper.backend.domain.exercise.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class ExerciseController {

  private final ExerciseService exerciseService;

  @GetMapping
  public ResponseEntity<List<ExerciseResponse>> getAllExercises() {
    return ResponseEntity.ok(exerciseService.getAllExercises());
  }
}