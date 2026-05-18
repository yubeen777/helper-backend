// domain/feedback/repository/AiFeedbackRepository.java
package com.helper.backend.domain.feedback.repository;

import com.helper.backend.domain.feedback.entity.AiFeedback;
import com.helper.backend.domain.feedback.entity.FeedbackStatus;
import com.helper.backend.domain.feedback.entity.FeedbackType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AiFeedbackRepository extends JpaRepository<AiFeedback, Long> {

  // 유저의 피드백 목록 조회 (최신순)
  List<AiFeedback> findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long userId);

  // 유저의 특정 타입 피드백 목록
  List<AiFeedback> findByUserIdAndFeedbackTypeAndDeletedAtIsNull(
      Long userId, FeedbackType feedbackType);

  // 특정 피드백 단건 조회
  Optional<AiFeedback> findByIdAndDeletedAtIsNull(Long id);

  // PENDING 상태인 피드백 목록 (재시도 로직용)
  List<AiFeedback> findByStatus(FeedbackStatus status);
}