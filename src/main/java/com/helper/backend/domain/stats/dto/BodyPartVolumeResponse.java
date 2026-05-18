// domain/stats/dto/BodyPartVolumeResponse.java
package com.helper.backend.domain.stats.dto;

import com.helper.backend.domain.exercise.entity.BodyPart;
import lombok.Getter;

@Getter
public class BodyPartVolumeResponse {

  private String bodyPart;
  private double volume;

  public BodyPartVolumeResponse(BodyPart bodyPart, Double volume) {
    this.bodyPart = bodyPart.name();
    this.volume = volume != null ? volume : 0.0;
  }
}