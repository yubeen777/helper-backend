package com.helper.backend.domain.workout.entity;

import com.helper.backend.common.entity.BaseEntity;
import com.helper.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "workouts")
public class Workout extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private LocalDate workoutDate;

  @Column(length = 255)
  private String memo;

  @OneToMany(mappedBy = "workout", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<WorkoutSet> workoutSets = new ArrayList<>();

  @Builder
  public Workout(User user, LocalDate workoutDate, String memo) {
    this.user = user;
    this.workoutDate = workoutDate;
    this.memo = memo;
  }

  public void updateMemo(String memo) {
    this.memo = memo;
  }
}