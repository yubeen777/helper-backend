// domain/stats/dto/WorkoutAnalysisResponse.java
package com.helper.backend.domain.stats.dto;

import lombok.Getter;
import java.time.LocalDate;
import java.util.List;

@Getter
public class WorkoutAnalysisResponse {

  private Long workoutId;
  private LocalDate workoutDate;
  private double totalVolume;
  private List<ExerciseAnalysis> exercises;

  public WorkoutAnalysisResponse(Long workoutId, LocalDate workoutDate,
                                 double totalVolume, List<ExerciseAnalysis> exercises) {
    this.workoutId = workoutId;
    this.workoutDate = workoutDate;
    this.totalVolume = totalVolume;
    this.exercises = exercises;
  }

  @Getter
  public static class ExerciseAnalysis {
    private String exerciseName;
    private String bodyPart;
    private double bestOneRm;
    private double totalVolume;

    public ExerciseAnalysis(String exerciseName, String bodyPart,
                            double bestOneRm, double totalVolume) {
      this.exerciseName = exerciseName;
      this.bodyPart = bodyPart;
      this.bestOneRm = bestOneRm;
      this.totalVolume = totalVolume;
    }
  }
}