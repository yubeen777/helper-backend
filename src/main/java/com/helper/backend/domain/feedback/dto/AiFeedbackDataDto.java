// domain/feedback/dto/AiFeedbackDataDto.java
package com.helper.backend.domain.feedback.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

// AI API에 넘길 데이터 구조
@Getter
@Builder
public class AiFeedbackDataDto {

  private UserInfo user;
  private WeeklyStats weeklyStats;
  private List<BodyPartVolume> volumeByBodyPart;
  private List<RecentWorkout> recentWorkouts;

  @Getter
  @Builder
  public static class UserInfo {
    private String nickname;
  }

  @Getter
  @Builder
  public static class WeeklyStats {
    private int totalWorkouts;
    private double totalVolume;
    private int totalSets;
  }

  @Getter
  @Builder
  public static class BodyPartVolume {
    private String bodyPart;
    private double volume;
  }

  @Getter
  @Builder
  public static class RecentWorkout {
    private String date;
    private List<ExerciseInfo> exercises;
  }

  @Getter
  @Builder
  public static class ExerciseInfo {
    private String name;
    private String bodyPart;
    private double bestOneRm;
    private double totalVolume;
  }
}