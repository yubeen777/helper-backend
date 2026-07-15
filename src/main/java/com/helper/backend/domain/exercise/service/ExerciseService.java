// domain/exercise/service/ExerciseService.java
package com.helper.backend.domain.exercise.service;

import com.helper.backend.domain.exercise.dto.ExerciseResponse;
import com.helper.backend.domain.exercise.entity.BodyPart;
import com.helper.backend.domain.exercise.entity.Exercise;
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

  // 종목 검색 (keyword, bodyPart 둘 다 선택사항)
  @Transactional(readOnly = true)
  public List<ExerciseResponse> searchExercises(String keyword, String bodyPart) {
    boolean hasKeyword = keyword != null && !keyword.isBlank();
    boolean hasBodyPart = bodyPart != null && !bodyPart.isBlank();

    List<Exercise> exercises;
    if (hasBodyPart && hasKeyword) {
      exercises = exerciseRepository.findByBodyPartAndNameContainingIgnoreCaseAndDeletedAtIsNull(
          BodyPart.valueOf(bodyPart.toUpperCase()), keyword);
    } else if (hasBodyPart) {
      exercises = exerciseRepository.findByBodyPartAndDeletedAtIsNull(BodyPart.valueOf(bodyPart.toUpperCase()));
    } else if (hasKeyword) {
      exercises = exerciseRepository.findByNameContainingIgnoreCaseAndDeletedAtIsNull(keyword);
    } else {
      exercises = exerciseRepository.findByDeletedAtIsNull();
    }

    return exercises.stream()
        .map(ExerciseResponse::new)
        .collect(Collectors.toList());
  }
}