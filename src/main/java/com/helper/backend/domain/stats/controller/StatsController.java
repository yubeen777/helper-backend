// domain/stats/controller/StatsController.java
package com.helper.backend.domain.stats.controller;

import com.helper.backend.domain.stats.dto.*;
import com.helper.backend.domain.stats.service.StatsService;
import com.helper.backend.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

  private final StatsService statsService;
  private final JwtUtil jwtUtil;

  // 주간 통계
  @GetMapping("/weekly")
  public ResponseEntity<WeeklyStatsResponse> getWeeklyStats(
      @RequestHeader("Authorization") String token,
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

    Long userId = jwtUtil.getUserId(token.substring(7));
    if (date == null) date = LocalDate.now();
    return ResponseEntity.ok(statsService.getWeeklyStats(userId, date));
  }

  // 부위별 볼륨
  @GetMapping("/volume")
  public ResponseEntity<List<BodyPartVolumeResponse>> getVolumeByBodyPart(
      @RequestHeader("Authorization") String token,
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

    Long userId = jwtUtil.getUserId(token.substring(7));
    if (date == null) date = LocalDate.now();
    return ResponseEntity.ok(statsService.getVolumeByBodyPart(userId, date));
  }

  // 세션 분석
  @GetMapping("/workout/{workoutId}")
  public ResponseEntity<WorkoutAnalysisResponse> getWorkoutAnalysis(
      @RequestHeader("Authorization") String token,
      @PathVariable Long workoutId) {

    Long userId = jwtUtil.getUserId(token.substring(7));
    return ResponseEntity.ok(statsService.getWorkoutAnalysis(userId, workoutId));
  }

  // 1RM 추세
  @GetMapping("/exercise/{exerciseId}/1rm-trend")
  public ResponseEntity<List<OneRmTrendResponse>> getOneRmTrend(
      @RequestHeader("Authorization") String token,
      @PathVariable Long exerciseId) {

    Long userId = jwtUtil.getUserId(token.substring(7));
    return ResponseEntity.ok(statsService.getOneRmTrend(userId, exerciseId));
  }

  // 운동 일관성
  @GetMapping("/consistency")
  public ResponseEntity<ConsistencyResponse> getConsistency(
      @RequestHeader("Authorization") String token,
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

    Long userId = jwtUtil.getUserId(token.substring(7));
    if (date == null) date = LocalDate.now();
    return ResponseEntity.ok(statsService.getConsistency(userId, date));
  }
}