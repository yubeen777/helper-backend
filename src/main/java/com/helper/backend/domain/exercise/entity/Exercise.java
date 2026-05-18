package com.helper.backend.domain.exercise.entity;

import com.helper.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "exercises")
public class Exercise extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 50)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BodyPart bodyPart;

  @Builder
  public Exercise(String name, BodyPart bodyPart) {
    this.name = name;
    this.bodyPart = bodyPart;
  }
}