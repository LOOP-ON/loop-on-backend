package com.loopon.domain.challenge.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "challenge_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChallengeImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
