package com.loopon.routine.domain;

import com.loopon.journey.domain.ProgressStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "routine_progress")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RoutineProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id", nullable = false)
    private Routine routine;

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

    public void postpone(String reason) {
        this.status = ProgressStatus.POSTPONED;
        this.postponedReason = reason;
        this.completedAt = LocalDateTime.now();
    }

    @PrePersist
    private void prePersist() {
        this.status = ProgressStatus.IN_PROGRESS;
    }

    // 루틴 프로그레스 생성을 위핸 create 메서드 추가
    public static RoutineProgress create(
            Routine routine,
            LocalDate progressDate
    ) {
        RoutineProgress progress = new RoutineProgress();
        progress.routine = routine;
        progress.progressDate = progressDate;
        return progress;
    }
}
