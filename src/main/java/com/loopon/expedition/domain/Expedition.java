package com.loopon.expedition.domain;

import com.loopon.global.domain.BaseTimeEntity;
import com.loopon.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "expeditions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Expedition extends BaseTimeEntity {

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
    @Builder.Default
    private int currentUsers = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExpeditionCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExpeditionVisibility visibility;

    @Column(length = 100)
    private String password;

    public void modify(String title, ExpeditionVisibility visibility, String password, Integer userLimit) {
        this.title = title;
        this.visibility = visibility;
        this.password = password;
        this.userLimit = userLimit;
    }

    public void addToCurrentUsers(int currentUsers) {
        this.currentUsers += currentUsers;
    }
}
