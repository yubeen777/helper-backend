package com.helper.backend.domain.stats.service;

import com.helper.backend.domain.stats.dto.*;
import com.helper.backend.domain.stats.repository.StatsRepository;
import com.helper.backend.domain.workout.entity.Workout;
import com.helper.backend.domain.workout.entity.WorkoutSet;
import com.helper.backend.domain.workout.repository.WorkoutRepository;
import com.helper.backend.global.exception.CustomException;
import com.helper.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {

  private final StatsRepository statsRepository;
  private final WorkoutRepository workoutRepository;

  // 주간 통계
  @Transactional(readOnly = true)
  public WeeklyStatsResponse getWeeklyStats(Long userId, LocalDate date) {
    LocalDate weekStart = date.with(DayOfWeek.MONDAY);
    LocalDate weekEnd = date.with(DayOfWeek.SUNDAY);

    List<WorkoutSet> sets = statsRepository.findSetsForWeeklyStats(userId, weekStart, weekEnd);

    // 볼륨 계산: 무게 × 횟수 합산
    double totalVolume = sets.stream()
        .mapToDouble(ws -> ws.getWeight() * ws.getReps())
        .sum();

    // 운동 세션 수 계산 (중복 제거)
    long totalWorkouts = sets.stream()
        .map(ws -> ws.getWorkout().getId())
        .distinct()
        .count();

    return new WeeklyStatsResponse(weekStart, weekEnd,
        (int) totalWorkouts, totalVolume, sets.size());
  }

  // 부위별 볼륨
  @Transactional(readOnly = true)
  public List<BodyPartVolumeResponse> getVolumeByBodyPart(Long userId, LocalDate date) {
    LocalDate weekStart = date.with(DayOfWeek.MONDAY);
    LocalDate weekEnd = date.with(DayOfWeek.SUNDAY);

    return statsRepository.findVolumeByBodyPart(userId, weekStart, weekEnd);
  }

  // 세션 분석
  @Transactional(readOnly = true)
  public WorkoutAnalysisResponse getWorkoutAnalysis(Long userId, Long workoutId) {
    Workout workout = workoutRepository.findByIdAndDeletedAtIsNull(workoutId)
        .orElseThrow(() -> new CustomException(ErrorCode.WORKOUT_NOT_FOUND));

    // 소유자 검증
    if (!workout.getUser().getId().equals(userId)) {
      throw new CustomException(ErrorCode.WORKOUT_ACCESS_DENIED);
    }

    List<WorkoutSet> sets = statsRepository.findSetsForWorkoutAnalysis(workoutId);

    // 세션 총 볼륨
    double totalVolume = sets.stream()
        .mapToDouble(ws -> ws.getWeight() * ws.getReps())
        .sum();

    // 종목별로 그룹핑
    Map<Long, List<WorkoutSet>> byExercise = sets.stream()
        .collect(Collectors.groupingBy(ws -> ws.getExercise().getId()));

    List<WorkoutAnalysisResponse.ExerciseAnalysis> exercises = byExercise.values().stream()
        .map(exerciseSets -> {
          WorkoutSet first = exerciseSets.get(0);
          String exerciseName = first.getExercise().getName();
          String bodyPart = first.getExercise().getBodyPart().name();

          // 종목별 최고 1RM (Epley 공식: weight × (1 + reps/30))
          double bestOneRm = exerciseSets.stream()
              .mapToDouble(ws -> ws.getWeight() * (1 + ws.getReps() / 30.0))
              .max()
              .orElse(0.0);

          // 종목별 총 볼륨
          double exerciseVolume = exerciseSets.stream()
              .mapToDouble(ws -> ws.getWeight() * ws.getReps())
              .sum();

          return new WorkoutAnalysisResponse.ExerciseAnalysis(
              exerciseName, bodyPart, bestOneRm, exerciseVolume);
        })
        .collect(Collectors.toList());

    return new WorkoutAnalysisResponse(workoutId, workout.getWorkoutDate(),
        totalVolume, exercises);
  }

  // 1RM 추세
  @Transactional(readOnly = true)
  public List<OneRmTrendResponse> getOneRmTrend(Long userId, Long exerciseId) {
    return statsRepository.findOneRmTrend(userId, exerciseId);
  }

  // 운동 일관성 (최근 6개월)
  @Transactional(readOnly = true)
  public ConsistencyResponse getConsistency(Long userId, LocalDate date) {
    LocalDate startDate = date.minusMonths(6);
    LocalDate endDate = date;

    List<LocalDate> workoutDates = statsRepository.findWorkoutDates(
        userId, startDate, endDate);

    return new ConsistencyResponse(startDate, endDate, workoutDates);
  }
}