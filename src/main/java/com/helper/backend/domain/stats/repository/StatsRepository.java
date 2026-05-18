// domain/stats/repository/StatsRepository.java
package com.helper.backend.domain.stats.repository;

import com.helper.backend.domain.stats.dto.BodyPartVolumeResponse;
import com.helper.backend.domain.stats.dto.OneRmTrendResponse;
import com.helper.backend.domain.workout.entity.WorkoutSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface StatsRepository extends JpaRepository<WorkoutSet, Long> {

  // 주간 통계 - 특정 기간 내 운동 세트 목록 (볼륨, 세트 수 계산용)
  @Query("""
        SELECT ws FROM WorkoutSet ws
        JOIN FETCH ws.workout w
        JOIN FETCH ws.exercise e
        WHERE w.user.id = :userId
        AND w.workoutDate BETWEEN :startDate AND :endDate
        AND ws.deletedAt IS NULL
        AND w.deletedAt IS NULL
    """)
  List<WorkoutSet> findSetsForWeeklyStats(
      @Param("userId") Long userId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate
  );

  // 부위별 볼륨
  @Query("""
        SELECT new com.helper.backend.domain.stats.dto.BodyPartVolumeResponse(
            e.bodyPart,
            SUM(ws.weight * ws.reps)
        )
        FROM WorkoutSet ws
        JOIN ws.workout w
        JOIN ws.exercise e
        WHERE w.user.id = :userId
        AND w.workoutDate BETWEEN :startDate AND :endDate
        AND ws.deletedAt IS NULL
        AND w.deletedAt IS NULL
        GROUP BY e.bodyPart
    """)
  List<BodyPartVolumeResponse> findVolumeByBodyPart(
      @Param("userId") Long userId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate
  );

  // 특정 운동 세션의 세트 목록 (세션 분석용)
  @Query("""
        SELECT ws FROM WorkoutSet ws
        JOIN FETCH ws.exercise e
        WHERE ws.workout.id = :workoutId
        AND ws.deletedAt IS NULL
    """)
  List<WorkoutSet> findSetsForWorkoutAnalysis(@Param("workoutId") Long workoutId);

  // 특정 종목 1RM 추세
  @Query("""
        SELECT new com.helper.backend.domain.stats.dto.OneRmTrendResponse(
            w.workoutDate,
            MAX(ws.weight * (1 + ws.reps / 30.0))
        )
        FROM WorkoutSet ws
        JOIN ws.workout w
        WHERE w.user.id = :userId
        AND ws.exercise.id = :exerciseId
        AND ws.deletedAt IS NULL
        AND w.deletedAt IS NULL
        GROUP BY w.workoutDate
        ORDER BY w.workoutDate ASC
    """)
  List<OneRmTrendResponse> findOneRmTrend(
      @Param("userId") Long userId,
      @Param("exerciseId") Long exerciseId
  );

  // 운동 일관성 - 운동한 날짜 목록
  @Query("""
        SELECT DISTINCT w.workoutDate
        FROM Workout w
        WHERE w.user.id = :userId
        AND w.workoutDate BETWEEN :startDate AND :endDate
        AND w.deletedAt IS NULL
        ORDER BY w.workoutDate ASC
    """)
  List<LocalDate> findWorkoutDates(
      @Param("userId") Long userId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate
  );
}