// domain/workout/repository/WorkoutSetRepository.java
package com.helper.backend.domain.workout.repository;

import com.helper.backend.domain.workout.entity.WorkoutSet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, Long> {

  // 특정 workout의 세트 목록 (삭제 안 된 것만)
  List<WorkoutSet> findByWorkoutIdAndDeletedAtIsNull(Long workoutId);

  // 특정 workout의 현재 최대 setNumber
  int countByWorkoutIdAndDeletedAtIsNull(Long workoutId);

  // 특정 세트 조회 (삭제 안 된 것만)
  Optional<WorkoutSet> findByIdAndDeletedAtIsNull(Long id);
}