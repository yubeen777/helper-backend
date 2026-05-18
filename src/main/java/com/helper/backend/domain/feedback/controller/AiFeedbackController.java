// domain/feedback/controller/AiFeedbackController.java
package com.helper.backend.domain.feedback.controller;

import com.helper.backend.domain.feedback.dto.AiFeedbackRequest;
import com.helper.backend.domain.feedback.dto.AiFeedbackResponse;
import com.helper.backend.domain.feedback.service.AiFeedbackService;
import com.helper.backend.global.jwt.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class AiFeedbackController {

  private final AiFeedbackService aiFeedbackService;
  private final JwtUtil jwtUtil;

  // 피드백 요청
  @PostMapping
  public ResponseEntity<AiFeedbackResponse> requestFeedback(
      @RequestHeader("Authorization") String token,
      @Valid @RequestBody AiFeedbackRequest request) {

    Long userId = jwtUtil.getUserId(token.substring(7));
    return ResponseEntity.ok(aiFeedbackService.requestFeedback(userId, request));
  }

  // 피드백 목록 조회
  @GetMapping
  public ResponseEntity<List<AiFeedbackResponse>> getFeedbacks(
      @RequestHeader("Authorization") String token) {

    Long userId = jwtUtil.getUserId(token.substring(7));
    return ResponseEntity.ok(aiFeedbackService.getFeedbacks(userId));
  }

  // 피드백 단건 조회
  @GetMapping("/{feedbackId}")
  public ResponseEntity<AiFeedbackResponse> getFeedback(
      @RequestHeader("Authorization") String token,
      @PathVariable Long feedbackId) {

    Long userId = jwtUtil.getUserId(token.substring(7));
    return ResponseEntity.ok(aiFeedbackService.getFeedback(userId, feedbackId));
  }
}