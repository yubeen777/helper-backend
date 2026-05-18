// domain/exercise/service/ExerciseService.java
package com.helper.backend.domain.exercise.service;

import com.helper.backend.domain.exercise.dto.ExerciseResponse;
import com.helper.backend.domain.exercise.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseService {

  private final ExerciseRepository exerciseRepository;

  // 전체 종목 조회
  @Transactional(readOnly = true)
  public List<ExerciseResponse> getAllExercises() {
    return exerciseRepository.findAll()
        .stream()
        .map(ExerciseResponse::new)
        .collect(Collectors.toList());
  }
}