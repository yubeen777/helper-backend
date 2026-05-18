// domain/stats/dto/ConsistencyResponse.java
package com.helper.backend.domain.stats.dto;

import lombok.Getter;
import java.time.LocalDate;
import java.util.List;

@Getter
public class ConsistencyResponse {

  private LocalDate startDate;
  private LocalDate endDate;
  private List<LocalDate> workoutDates;
  private int totalWorkouts;

  public ConsistencyResponse(LocalDate startDate, LocalDate endDate,
                             List<LocalDate> workoutDates) {
    this.startDate = startDate;
    this.endDate = endDate;
    this.workoutDates = workoutDates;
    this.totalWorkouts = workoutDates.size();
  }
}