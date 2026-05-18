// domain/feedback/dto/AiFeedbackResponse.java
package com.helper.backend.domain.feedback.dto;

import com.helper.backend.domain.feedback.entity.AiFeedback;
import com.helper.backend.domain.feedback.entity.FeedbackStatus;
import com.helper.backend.domain.feedback.entity.FeedbackType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AiFeedbackResponse {

  private Long id;
  private FeedbackType feedbackType;
  private FeedbackStatus status;
  private String summary;
  private String analysis;
  private String routine;
  private String nutrition;
  private String nextWeekGoal;
  private String errorMessage;
  private LocalDateTime createdAt;

  public AiFeedbackResponse(AiFeedback feedback) {
    this.id = feedback.getId();
    this.feedbackType = feedback.getFeedbackType();
    this.status = feedback.getStatus();
    this.summary = feedback.getSummary();
    this.analysis = feedback.getAnalysis();
    this.routine = feedback.getRoutine();
    this.nutrition = feedback.getNutrition();
    this.nextWeekGoal = feedback.getNextWeekGoal();
    this.errorMessage = feedback.getErrorMessage();
    this.createdAt = feedback.getCreatedAt();
  }
}