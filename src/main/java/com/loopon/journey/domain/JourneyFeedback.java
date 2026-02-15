package com.loopon.journey.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "journey_feedbacks")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JourneyFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journey_id", nullable = false)
    private Journey journey;

    // 성장 추이 그래프를 위한 데이터 값
    @Column
    private Integer day1Rate;

    @Column
    private Integer day2Rate;

    @Column
    private Integer day3Rate;

    @Column
    private Integer totalRate;
    

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void complete(int totalRate) {
        this.totalRate = totalRate;
    }

    @PrePersist
    private void prePersist() {
        LocalDateTime today = LocalDateTime.now();

        this.createdAt = today;
    }

    public void updateDailyRate(int dayIndex, int rate) {
        switch (dayIndex) {
            case 1 -> this.day1Rate = rate;
            case 2 -> this.day2Rate = rate;
            case 3 -> this.day3Rate = rate;
        }
    }
}
