package com.loopon.domain.journey.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "routines")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Routine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "routine_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journey_id", nullable = false)
    private Journey journey;

    @Column(nullable = false, length = 255)
    private String content;

    @Column(name = "notification_time")
    private LocalDateTime notificationTime;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
