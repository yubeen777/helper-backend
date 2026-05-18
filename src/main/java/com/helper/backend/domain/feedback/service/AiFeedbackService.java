package com.helper.backend.domain.feedback.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.helper.backend.domain.feedback.dto.AiFeedbackDataDto;
import com.helper.backend.domain.feedback.dto.AiFeedbackRequest;
import com.helper.backend.domain.feedback.dto.AiFeedbackResponse;
import com.helper.backend.domain.feedback.entity.AiFeedback;
import com.helper.backend.domain.feedback.entity.FeedbackType;
import com.helper.backend.domain.feedback.repository.AiFeedbackRepository;
import com.helper.backend.domain.stats.dto.BodyPartVolumeResponse;
import com.helper.backend.domain.stats.dto.WeeklyStatsResponse;
import com.helper.backend.domain.stats.service.StatsService;
import com.helper.backend.domain.user.entity.User;
import com.helper.backend.domain.user.repository.UserRepository;
import com.helper.backend.domain.workout.entity.Workout;
import com.helper.backend.domain.workout.entity.WorkoutSet;
import com.helper.backend.domain.workout.repository.WorkoutRepository;
import com.helper.backend.domain.workout.repository.WorkoutSetRepository;
import com.helper.backend.global.exception.CustomException;
import com.helper.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiFeedbackService {

  private final AiFeedbackRepository aiFeedbackRepository;
  private final UserRepository userRepository;
  private final WorkoutRepository workoutRepository;
  private final WorkoutSetRepository workoutSetRepository;
  private final StatsService statsService;
  private final ObjectMapper objectMapper;

  @Value("${ai.api.key}")
  private String apiKey;

  @Value("${ai.api.url}")
  private String apiUrl;

  @Value("${ai.api.model}")
  private String model;

  // 피드백 요청 (비동기)
  @Transactional
  public AiFeedbackResponse requestFeedback(Long userId, AiFeedbackRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    // SESSION 타입일 때만 workout 조회
    Workout workout = null;
    if (request.getFeedbackType() == FeedbackType.SESSION) {
      workout = workoutRepository.findByIdAndDeletedAtIsNull(request.getWorkoutId())
          .orElseThrow(() -> new CustomException(ErrorCode.WORKOUT_NOT_FOUND));
    }

    // PENDING 상태로 먼저 저장 후 즉시 응답 반환
    AiFeedback feedback = AiFeedback.builder()
        .user(user)
        .workout(workout)
        .feedbackType(request.getFeedbackType())
        .build();

    aiFeedbackRepository.save(feedback);

    // 비동기로 AI API 호출 (@Async)
    processAiFeedback(feedback.getId(), userId, request.getFeedbackType());

    return new AiFeedbackResponse(feedback);
  }

  // 비동기 AI API 호출 (별도 스레드에서 실행)
  @Async
  @Transactional
  public void processAiFeedback(Long feedbackId, Long userId, FeedbackType feedbackType) {
    AiFeedback feedback = aiFeedbackRepository.findById(feedbackId)
        .orElseThrow(() -> new CustomException(ErrorCode.FEEDBACK_NOT_FOUND));

    try {
      // 데이터 수집
      AiFeedbackDataDto data = collectData(userId, feedbackType, feedback.getWorkout());

      // 프롬프트 생성
      String prompt = buildPrompt(data);

      // AI API 호출
      String aiResponse = callAiApi(prompt);

      // 응답 파싱
      Map<String, String> parsed = parseAiResponse(aiResponse);

      // 완료 처리 → COMPLETED 상태로 업데이트
      feedback.complete(
          parsed.get("summary"),
          parsed.get("analysis"),
          parsed.get("routine"),
          parsed.get("nutrition"),
          parsed.get("nextWeekGoal")
      );

    } catch (Exception e) {
      // 실패 처리 → FAILED 상태로 업데이트
      log.error("AI 피드백 처리 실패. feedbackId: {}", feedbackId, e);
      feedback.fail(e.getMessage());
    }
  }

  // 데이터 수집 (AI에 넘길 운동 데이터 조합)
  private AiFeedbackDataDto collectData(Long userId, FeedbackType feedbackType, Workout workout) {
    LocalDate now = LocalDate.now();

    // 주간 통계
    WeeklyStatsResponse weeklyStats = statsService.getWeeklyStats(userId, now);

    // 부위별 볼륨
    List<BodyPartVolumeResponse> volumeList = statsService.getVolumeByBodyPart(userId, now);

    // 최근 운동 기록
    List<AiFeedbackDataDto.RecentWorkout> recentWorkouts;

    if (feedbackType == FeedbackType.SESSION && workout != null) {
      // SESSION: 특정 세션 데이터
      List<WorkoutSet> sets = workoutSetRepository.findByWorkoutIdAndDeletedAtIsNull(workout.getId());
      recentWorkouts = List.of(buildRecentWorkout(workout, sets));
    } else {
      // WEEKLY: 최근 7개 세션 데이터
      recentWorkouts = workoutRepository
          .findByUserIdAndDeletedAtIsNull(userId, null)
          .getContent()
          .stream()
          .limit(7)
          .map(w -> {
            List<WorkoutSet> sets = workoutSetRepository
                .findByWorkoutIdAndDeletedAtIsNull(w.getId());
            return buildRecentWorkout(w, sets);
          })
          .collect(Collectors.toList());
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    return AiFeedbackDataDto.builder()
        .user(AiFeedbackDataDto.UserInfo.builder()
            .nickname(user.getNickname())
            .build())
        .weeklyStats(AiFeedbackDataDto.WeeklyStats.builder()
            .totalWorkouts(weeklyStats.getTotalWorkouts())
            .totalVolume(weeklyStats.getTotalVolume())
            .totalSets(weeklyStats.getTotalSets())
            .build())
        .volumeByBodyPart(volumeList.stream()
            .map(v -> AiFeedbackDataDto.BodyPartVolume.builder()
                .bodyPart(v.getBodyPart())
                .volume(v.getVolume())
                .build())
            .collect(Collectors.toList()))
        .recentWorkouts(recentWorkouts)
        .build();
  }

  // RecentWorkout 빌드 (종목별 최고 1RM + 볼륨 계산)
  private AiFeedbackDataDto.RecentWorkout buildRecentWorkout(Workout workout, List<WorkoutSet> sets) {
    // 종목별로 그룹핑
    Map<Long, List<WorkoutSet>> byExercise = sets.stream()
        .collect(Collectors.groupingBy(ws -> ws.getExercise().getId()));

    List<AiFeedbackDataDto.ExerciseInfo> exercises = byExercise.values().stream()
        .map(exerciseSets -> {
          WorkoutSet first = exerciseSets.get(0);

          // 종목별 최고 1RM (Epley 공식: weight × (1 + reps/30))
          double bestOneRm = exerciseSets.stream()
              .mapToDouble(ws -> ws.getWeight() * (1 + ws.getReps() / 30.0))
              .max().orElse(0.0);

          // 종목별 총 볼륨
          double totalVolume = exerciseSets.stream()
              .mapToDouble(ws -> ws.getWeight() * ws.getReps())
              .sum();

          return AiFeedbackDataDto.ExerciseInfo.builder()
              .name(first.getExercise().getName())
              .bodyPart(first.getExercise().getBodyPart().name())
              .bestOneRm(Math.round(bestOneRm * 10.0) / 10.0)
              .totalVolume(totalVolume)
              .build();
        })
        .collect(Collectors.toList());

    return AiFeedbackDataDto.RecentWorkout.builder()
        .date(workout.getWorkoutDate().toString())
        .exercises(exercises)
        .build();
  }

  // 프롬프트 생성 (AI에게 역할 + 응답 형식 지정)
  private String buildPrompt(AiFeedbackDataDto data) throws Exception {
    String dataJson = objectMapper.writeValueAsString(data);

    return """
        당신은 10년 경력의 전문 퍼스널 트레이너입니다.
        아래 운동 데이터를 분석해서 전문적이고 구체적인 피드백을 한국어로 제공하세요.
        
        [분석 기준]
        - 부위별 볼륨 불균형을 수치로 지적하세요
        - 부족한 부위의 구체적인 운동명을 추천하세요
        - 근육 부위별 세부 설명을 포함하세요
          (예: 이두 장두/단두, 가슴 상부/중부/하부, 어깨 전면/측면/후면 등)
        - 다음 주 루틴을 구체적으로 제안하세요
        - 체중 기반 단백질/탄수화물 섭취량을 계산하세요
        
        [응답 형식 - 반드시 아래 JSON 형식으로만 응답하세요]
        {
            "summary": "이번 주 전반적인 총평 (2~3문장)",
            "analysis": "부위별 볼륨 분석 및 불균형 지적 (구체적 수치 포함)",
            "routine": "다음 주 추천 루틴 (요일별, 구체적인 운동명 포함)",
            "nutrition": "영양 가이드 (단백질/탄수화물/식품 추천)",
            "nextWeekGoal": "다음 주 목표 제안 (구체적인 수치 포함)"
        }
        
        [운동 데이터]
        """ + dataJson;
  }

  // AI API 호출
  private String callAiApi(String prompt) throws Exception {
    String requestBody;

    // ========== GPT 사용 시 (현재 활성화) ==========
    requestBody = objectMapper.writeValueAsString(Map.of(
        "model", model,
        "max_tokens", 2000,
        "messages", List.of(Map.of(
            "role", "user",
            "content", prompt
        ))
    ));

    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(apiUrl))
        .header("Content-Type", "application/json")
        .header("Authorization", "Bearer " + apiKey)  // GPT는 Bearer
        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
        .build();

    HttpResponse<String> response = client.send(request,
        HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() != 200) {
      throw new RuntimeException("AI API 호출 실패: " + response.statusCode());
    }

    // GPT 응답 파싱
    Map<String, Object> responseMap = objectMapper.readValue(response.body(), Map.class);
    List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
    return (String) message.get("content");

    // ========== Claude 사용 시 ==========
    // requestBody = objectMapper.writeValueAsString(Map.of(
    //         "model", model,
    //         "max_tokens", 2000,
    //         "messages", List.of(Map.of(
    //                 "role", "user",
    //                 "content", prompt
    //         ))
    // ));
    //
    // HttpClient client = HttpClient.newHttpClient();
    // HttpRequest request = HttpRequest.newBuilder()
    //         .uri(URI.create(apiUrl))
    //         .header("Content-Type", "application/json")
    //         .header("x-api-key", apiKey)           // Claude는 x-api-key
    //         .header("anthropic-version", "2023-06-01")
    //         .POST(HttpRequest.BodyPublishers.ofString(requestBody))
    //         .build();
    //
    // HttpResponse<String> response = client.send(request,
    //         HttpResponse.BodyHandlers.ofString());
    //
    // if (response.statusCode() != 200) {
    //     throw new RuntimeException("AI API 호출 실패: " + response.statusCode());
    // }
    //
    // // Claude 응답 파싱
    // Map<String, Object> responseMap = objectMapper.readValue(response.body(), Map.class);
    // List<Map<String, Object>> content = (List<Map<String, Object>>) responseMap.get("content");
    // return (String) content.get(0).get("text");
  }

  // AI 응답 파싱 (JSON 형식 강제 → 각 섹션 분리)
  private Map<String, String> parseAiResponse(String response) throws Exception {
    // JSON 부분만 추출
    int start = response.indexOf("{");
    int end = response.lastIndexOf("}") + 1;
    String json = response.substring(start, end);

    // Map<String, Object>로 받은 후 String으로 변환
    Map<String, Object> rawMap = objectMapper.readValue(json, Map.class);
    Map<String, String> result = new java.util.LinkedHashMap<>();

    for (Map.Entry<String, Object> entry : rawMap.entrySet()) {
      result.put(entry.getKey(), entry.getValue() != null ? entry.getValue().toString() : null);
    }

    return result;
  }

  // 피드백 목록 조회
  @Transactional(readOnly = true)
  public List<AiFeedbackResponse> getFeedbacks(Long userId) {
    return aiFeedbackRepository
        .findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId)
        .stream()
        .map(AiFeedbackResponse::new)
        .collect(Collectors.toList());
  }

  // 피드백 단건 조회
  @Transactional(readOnly = true)
  public AiFeedbackResponse getFeedback(Long userId, Long feedbackId) {
    AiFeedback feedback = aiFeedbackRepository.findByIdAndDeletedAtIsNull(feedbackId)
        .orElseThrow(() -> new CustomException(ErrorCode.FEEDBACK_NOT_FOUND));

    // 소유자 검증
    if (!feedback.getUser().getId().equals(userId)) {
      throw new CustomException(ErrorCode.FEEDBACK_ACCESS_DENIED);
    }

    return new AiFeedbackResponse(feedback);
  }
}