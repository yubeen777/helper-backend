// domain/feedback/entity/FeedbackStatus.java
package com.helper.backend.domain.feedback.entity;

public enum FeedbackStatus {
  PENDING,    // 요청됨, 처리 중
  COMPLETED,  // 완료
  FAILED      // 실패
}