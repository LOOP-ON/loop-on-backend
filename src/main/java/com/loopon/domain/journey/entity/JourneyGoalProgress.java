package com.loopon.domain.journey.entity;

import com.loopon.domain.journey.enums.ProgressStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "journey_goal_progress")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JourneyGoalProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    private JourneyGoal goal;

    @Column(name = "progress_date", nullable = false)
    private LocalDate progressDate;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProgressStatus status;

    @Column(name = "postponed_reason", length = 100)
    private String postponedReason;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
