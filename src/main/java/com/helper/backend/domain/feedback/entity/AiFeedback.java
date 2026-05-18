// domain/feedback/entity/AiFeedback.java
package com.helper.backend.domain.feedback.entity;

import com.helper.backend.common.entity.BaseEntity;
import com.helper.backend.domain.user.entity.User;
import com.helper.backend.domain.workout.entity.Workout;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "ai_feedbacks")
public class AiFeedback extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "workout_id")
  private Workout workout;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private FeedbackType feedbackType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private FeedbackStatus status;

  @Column(columnDefinition = "TEXT")
  private String summary;

  @Column(columnDefinition = "TEXT")
  private String analysis;

  @Column(columnDefinition = "TEXT")
  private String routine;

  @Column(columnDefinition = "TEXT")
  private String nutrition;

  @Column(columnDefinition = "TEXT")
  private String nextWeekGoal;

  @Column(columnDefinition = "TEXT")
  private String errorMessage;

  @Builder
  public AiFeedback(User user, Workout workout, FeedbackType feedbackType) {
    this.user = user;
    this.workout = workout;
    this.feedbackType = feedbackType;
    this.status = FeedbackStatus.PENDING;
  }

  public void complete(String summary, String analysis,
                       String routine, String nutrition, String nextWeekGoal) {
    this.summary = summary;
    this.analysis = analysis;
    this.routine = routine;
    this.nutrition = nutrition;
    this.nextWeekGoal = nextWeekGoal;
    this.status = FeedbackStatus.COMPLETED;
  }

  public void fail(String errorMessage) {
    this.errorMessage = errorMessage;
    this.status = FeedbackStatus.FAILED;
  }
}