package com.loopon.journey.domain;
import com.loopon.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "journeys")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class Journey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "journey_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private JourneyCategory category;

    @Column(columnDefinition = "TEXT")
    private String goal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private JourneyStatus status;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void prePersist() {
        LocalDate today = LocalDate.now();

        if (this.status == null) {
            this.status = JourneyStatus.IN_PROGRESS;
        }
        if (this.startDate == null) {
            this.startDate = today;
        }
        if (this.endDate == null) {
            this.endDate = today.plusDays(3);
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
    
    public void updateGoal(String newGoal) {
        this.goal = newGoal;
    }
}
