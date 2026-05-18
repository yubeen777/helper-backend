// domain/workout/controller/WorkoutController.java
package com.helper.backend.domain.workout.controller;

import com.helper.backend.domain.workout.dto.*;
import com.helper.backend.domain.workout.service.WorkoutService;
import com.helper.backend.global.jwt.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workouts")
@RequiredArgsConstructor
public class WorkoutController {

  private final WorkoutService workoutService;
  private final JwtUtil jwtUtil;

  // 운동 세션 생성
  @PostMapping
  public ResponseEntity<WorkoutResponse> createWorkout(
      @RequestHeader("Authorization") String token,
      @Valid @RequestBody WorkoutCreateRequest request) {

    Long userId = jwtUtil.getUserId(token.substring(7));
    WorkoutResponse response = workoutService.createWorkout(userId, request);
    return ResponseEntity.ok(response);
  }

  // 운동 세션 목록 조회
  @GetMapping
  public ResponseEntity<Page<WorkoutResponse>> getWorkouts(
      @RequestHeader("Authorization") String token,
      @PageableDefault(size = 10, sort = "workoutDate", direction = Sort.Direction.DESC) Pageable pageable) {

    Long userId = jwtUtil.getUserId(token.substring(7));
    Page<WorkoutResponse> response = workoutService.getWorkouts(userId, pageable);
    return ResponseEntity.ok(response);
  }

  // 운동 세션 단건 조회
  @GetMapping("/{workoutId}")
  public ResponseEntity<WorkoutResponse> getWorkout(
      @RequestHeader("Authorization") String token,
      @PathVariable Long workoutId) {

    Long userId = jwtUtil.getUserId(token.substring(7));
    WorkoutResponse response = workoutService.getWorkout(userId, workoutId);
    return ResponseEntity.ok(response);
  }

  // 운동 세션 수정
  @PatchMapping("/{workoutId}")
  public ResponseEntity<WorkoutResponse> updateWorkout(
      @RequestHeader("Authorization") String token,
      @PathVariable Long workoutId,
      @Valid @RequestBody WorkoutUpdateRequest request) {

    Long userId = jwtUtil.getUserId(token.substring(7));
    WorkoutResponse response = workoutService.updateWorkout(userId, workoutId, request);
    return ResponseEntity.ok(response);
  }

  // 운동 세션 삭제
  @DeleteMapping("/{workoutId}")
  public ResponseEntity<Void> deleteWorkout(
      @RequestHeader("Authorization") String token,
      @PathVariable Long workoutId) {

    Long userId = jwtUtil.getUserId(token.substring(7));
    workoutService.deleteWorkout(userId, workoutId);
    return ResponseEntity.noContent().build();
  }

  // 세트 추가
  @PostMapping("/{workoutId}/sets")
  public ResponseEntity<WorkoutSetResponse> addWorkoutSet(
      @RequestHeader("Authorization") String token,
      @PathVariable Long workoutId,
      @Valid @RequestBody WorkoutSetCreateRequest request) {

    Long userId = jwtUtil.getUserId(token.substring(7));
    WorkoutSetResponse response = workoutService.addWorkoutSet(userId, workoutId, request);
    return ResponseEntity.ok(response);
  }

  // 세트 수정
  @PatchMapping("/{workoutId}/sets/{setId}")
  public ResponseEntity<WorkoutSetResponse> updateWorkoutSet(
      @RequestHeader("Authorization") String token,
      @PathVariable Long workoutId,
      @PathVariable Long setId,
      @Valid @RequestBody WorkoutSetUpdateRequest request) {

    Long userId = jwtUtil.getUserId(token.substring(7));
    WorkoutSetResponse response = workoutService.updateWorkoutSet(userId, workoutId, setId, request);
    return ResponseEntity.ok(response);
  }

  // 세트 삭제
  @DeleteMapping("/{workoutId}/sets/{setId}")
  public ResponseEntity<Void> deleteWorkoutSet(
      @RequestHeader("Authorization") String token,
      @PathVariable Long workoutId,
      @PathVariable Long setId) {

    Long userId = jwtUtil.getUserId(token.substring(7));
    workoutService.deleteWorkoutSet(userId, workoutId, setId);
    return ResponseEntity.noContent().build();
  }
}