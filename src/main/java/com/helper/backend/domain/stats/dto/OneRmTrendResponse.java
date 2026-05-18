// domain/stats/dto/OneRmTrendResponse.java
package com.helper.backend.domain.stats.dto;

import lombok.Getter;
import java.time.LocalDate;

@Getter
public class OneRmTrendResponse {

  private LocalDate date;
  private double oneRm;

  public OneRmTrendResponse(LocalDate date, Double oneRm) {
    this.date = date;
    this.oneRm = oneRm != null ? oneRm : 0.0;
  }
}