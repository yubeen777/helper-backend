// domain/stats/dto/WeeklyStatsResponse.java
package com.helper.backend.domain.stats.dto;

import lombok.Getter;
import java.time.LocalDate;

@Getter
public class WeeklyStatsResponse {

  private LocalDate weekStart;
  private LocalDate weekEnd;
  private int totalWorkouts;
  private double totalVolume;
  private int totalSets;

  public WeeklyStatsResponse(LocalDate weekStart, LocalDate weekEnd,
                             int totalWorkouts, double totalVolume, int totalSets) {
    this.weekStart = weekStart;
    this.weekEnd = weekEnd;
    this.totalWorkouts = totalWorkouts;
    this.totalVolume = totalVolume;
    this.totalSets = totalSets;
  }
}