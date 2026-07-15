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

  // keyword, bodyPart 둘 다 선택사항 — 없으면 전체 반환
  @GetMapping
  public ResponseEntity<List<ExerciseResponse>> getExercises(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String bodyPart) {
    return ResponseEntity.ok(exerciseService.searchExercises(keyword, bodyPart));
  }
}