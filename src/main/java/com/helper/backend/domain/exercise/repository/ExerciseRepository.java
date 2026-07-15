package com.helper.backend.domain.exercise.repository;

import com.helper.backend.domain.exercise.entity.BodyPart;
import com.helper.backend.domain.exercise.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

  List<Exercise> findByDeletedAtIsNull();

  List<Exercise> findByNameContainingIgnoreCaseAndDeletedAtIsNull(String keyword);

  List<Exercise> findByBodyPartAndDeletedAtIsNull(BodyPart bodyPart);

  List<Exercise> findByBodyPartAndNameContainingIgnoreCaseAndDeletedAtIsNull(BodyPart bodyPart, String keyword);
}
