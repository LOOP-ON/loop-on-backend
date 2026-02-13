package com.loopon.routine.domain;

import com.loopon.global.domain.BaseTimeEntity;
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
@Builder
public class RoutineProgress extends BaseTimeEntity {

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

    // 루틴 인증을 위한 certify 메서드 추가
    public void certify(String imageUrl) {

        if (!this.progressDate.equals(LocalDate.now())) {
            throw new IllegalArgumentException("오늘 인증할 수 있는 루틴이 아닙니다.");
        }

        if (this.status != ProgressStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("이미 완료된 루틴입니다.");
        }

        this.status = ProgressStatus.COMPLETED;
        this.imageUrl = imageUrl;
        this.completedAt = LocalDateTime.now();
    }

    public void validateCertifiable() {

        if (!this.progressDate.equals(LocalDate.now())) {
            throw new IllegalArgumentException("오늘 인증할 수 있는 루틴이 아닙니다.");
        }

        if (this.status != ProgressStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("이미 완료된 루틴입니다.");
        }
    }

    //루틴 미루기 사유 수정 메서드
    public void updatePostponeReason(String reason) {
        this.postponedReason = reason;
    }
}
