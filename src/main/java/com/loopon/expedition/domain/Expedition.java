package com.loopon.expedition.domain;

import com.loopon.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "expeditions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Expedition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expedition_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "user_limit", nullable = false)
    private int userLimit;

    @Column(name = "current_users", nullable = false)
    private int currentUsers;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExpeditionCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExpeditionVisibility visibility;

    @Column(length = 100)
    private String password;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
