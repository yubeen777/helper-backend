// domain/workout/entity/WorkoutSet.java
package com.helper.backend.domain.workout.entity;

import com.helper.backend.common.entity.BaseEntity;
import com.helper.backend.domain.exercise.entity.Exercise;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "workout_sets",
    uniqueConstraints = @UniqueConstraint(columnNames = {"workout_id", "set_number"}))
public class WorkoutSet extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "workout_id", nullable = false)
  private Workout workout;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "exercise_id", nullable = false)
  private Exercise exercise;

  @Column(nullable = false)
  private Integer setNumber;

  @Column(nullable = false)
  private Double weight;

  @Column(nullable = false)
  private Integer reps;

  @Column
  private Integer restTime;   // 세트 후 휴식 시간 (초), 선택값

  @Column
  private Integer rpe;        // 운동 자각도 1~10, 선택값

  @Builder
  public WorkoutSet(Workout workout, Exercise exercise, Integer setNumber,
                    Double weight, Integer reps, Integer restTime, Integer rpe) {
    this.workout = workout;
    this.exercise = exercise;
    this.setNumber = setNumber;
    this.weight = weight;
    this.reps = reps;
    this.restTime = restTime;
    this.rpe = rpe;
  }

  public void updateSet(Double weight, Integer reps, Integer restTime, Integer rpe) {
    this.weight = weight;
    this.reps = reps;
    this.restTime = restTime;
    this.rpe = rpe;
  }
}