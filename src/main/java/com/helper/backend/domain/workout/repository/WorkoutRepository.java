// domain/workout/repository/WorkoutRepository.java
package com.helper.backend.domain.workout.repository;

import com.helper.backend.domain.workout.entity.Workout;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {

  // 특정 유저의 운동 목록 (삭제 안 된 것만, 최신순 페이징)
  Page<Workout> findByUserIdAndDeletedAtIsNull(Long userId, Pageable pageable);

  // 특정 운동 세션 조회 (삭제 안 된 것만)
  Optional<Workout> findByIdAndDeletedAtIsNull(Long id);
}