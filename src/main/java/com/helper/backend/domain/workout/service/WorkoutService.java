package com.helper.backend.domain.workout.service;

import com.helper.backend.domain.exercise.entity.Exercise;
import com.helper.backend.domain.exercise.repository.ExerciseRepository;
import com.helper.backend.domain.user.entity.User;
import com.helper.backend.domain.user.repository.UserRepository;
import com.helper.backend.domain.workout.dto.*;
import com.helper.backend.domain.workout.entity.Workout;
import com.helper.backend.domain.workout.entity.WorkoutSet;
import com.helper.backend.domain.workout.repository.WorkoutRepository;
import com.helper.backend.domain.workout.repository.WorkoutSetRepository;
import com.helper.backend.global.exception.CustomException;
import com.helper.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutService {

  private final WorkoutRepository workoutRepository;
  private final WorkoutSetRepository workoutSetRepository;
  private final UserRepository userRepository;
  private final ExerciseRepository exerciseRepository;

  // 운동 세션 생성
  @Transactional
  public WorkoutResponse createWorkout(Long userId, WorkoutCreateRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    Workout workout = Workout.builder()
        .user(user)
        .workoutDate(request.getWorkoutDate())
        .memo(request.getMemo())
        .build();

    workoutRepository.save(workout);
    return new WorkoutResponse(workout, List.of());
  }

  // 운동 세션 목록 조회
  @Transactional(readOnly = true)
  public Page<WorkoutResponse> getWorkouts(Long userId, Pageable pageable) {
    return workoutRepository.findByUserIdAndDeletedAtIsNull(userId, pageable)
        .map(workout -> {
          List<WorkoutSetResponse> sets = getWorkoutSetResponses(workout.getId());
          return new WorkoutResponse(workout, sets);
        });
  }

  // 운동 세션 단건 조회
  @Transactional(readOnly = true)
  public WorkoutResponse getWorkout(Long userId, Long workoutId) {
    Workout workout = findWorkoutByIdAndUser(workoutId, userId);
    List<WorkoutSetResponse> sets = getWorkoutSetResponses(workoutId);
    return new WorkoutResponse(workout, sets);
  }

  // 운동 세션 수정 (메모)
  @Transactional
  public WorkoutResponse updateWorkout(Long userId, Long workoutId, WorkoutUpdateRequest request) {
    Workout workout = findWorkoutByIdAndUser(workoutId, userId);
    workout.updateMemo(request.getMemo());
    return new WorkoutResponse(workout, getWorkoutSetResponses(workoutId));
  }

  // 운동 세션 삭제
  @Transactional
  public void deleteWorkout(Long userId, Long workoutId) {
    Workout workout = findWorkoutByIdAndUser(workoutId, userId);
    workout.softDelete();
  }

  // 세트 추가
  @Transactional
  public WorkoutSetResponse addWorkoutSet(Long userId, Long workoutId, WorkoutSetCreateRequest request) {
    Workout workout = findWorkoutByIdAndUser(workoutId, userId);

    Exercise exercise = exerciseRepository.findById(request.getExerciseId())
        .orElseThrow(() -> new CustomException(ErrorCode.EXERCISE_NOT_FOUND));

    // setNumber 서버 자동 부여
    int nextSetNumber = workoutSetRepository.countByWorkoutIdAndDeletedAtIsNull(workoutId) + 1;

    WorkoutSet workoutSet = WorkoutSet.builder()
        .workout(workout)
        .exercise(exercise)
        .setNumber(nextSetNumber)
        .weight(request.getWeight())
        .reps(request.getReps())
        .restTime(request.getRestTime())
        .rpe(request.getRpe())
        .build();

    workoutSetRepository.save(workoutSet);
    return new WorkoutSetResponse(workoutSet);
  }

  // 세트 수정
  @Transactional
  public WorkoutSetResponse updateWorkoutSet(Long userId, Long workoutId, Long setId, WorkoutSetUpdateRequest request) {
    findWorkoutByIdAndUser(workoutId, userId); // 내 운동인지 확인

    WorkoutSet workoutSet = workoutSetRepository.findByIdAndDeletedAtIsNull(setId)
        .orElseThrow(() -> new CustomException(ErrorCode.WORKOUT_SET_NOT_FOUND));

    workoutSet.updateSet(request.getWeight(), request.getReps(),
        request.getRestTime(), request.getRpe());

    return new WorkoutSetResponse(workoutSet);
  }

  // 세트 삭제
  @Transactional
  public void deleteWorkoutSet(Long userId, Long workoutId, Long setId) {
    findWorkoutByIdAndUser(workoutId, userId); // 내 운동인지 확인

    WorkoutSet workoutSet = workoutSetRepository.findByIdAndDeletedAtIsNull(setId)
        .orElseThrow(() -> new CustomException(ErrorCode.WORKOUT_SET_NOT_FOUND));

    workoutSet.softDelete();
  }

  // 공통: workout 조회 + 내 것인지 확인
  private Workout findWorkoutByIdAndUser(Long workoutId, Long userId) {
    Workout workout = workoutRepository.findByIdAndDeletedAtIsNull(workoutId)
        .orElseThrow(() -> new CustomException(ErrorCode.WORKOUT_NOT_FOUND));

    if (!workout.getUser().getId().equals(userId)) {
      throw new CustomException(ErrorCode.WORKOUT_ACCESS_DENIED);
    }

    return workout;
  }

  // 공통: 세트 목록 → DTO 변환
  private List<WorkoutSetResponse> getWorkoutSetResponses(Long workoutId) {
    return workoutSetRepository.findByWorkoutIdAndDeletedAtIsNull(workoutId)
        .stream()
        .map(WorkoutSetResponse::new)
        .collect(Collectors.toList());
  }
}