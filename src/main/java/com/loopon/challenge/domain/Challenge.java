package com.loopon.challenge.domain;

import com.loopon.expedition.domain.Expedition;
import com.loopon.journey.domain.Journey;
import com.loopon.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "challenges")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journey_id", nullable = false)
    private Journey journey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expedition_id")
    @Builder.Default
    private Expedition expedition = null;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(name = "like_count", nullable = false)
    @Builder.Default
    private Integer likeCount = 0;

    @Column(name = "comment_count", nullable = false)
    @Builder.Default
    private Integer commentCount = 0;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<ChallengeImage> challengeImages = new ArrayList<>();

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChallengeHashtag> challengeHashtags = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    @CreatedDate
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    @LastModifiedDate
    private LocalDateTime updatedAt = LocalDateTime.now();

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateExpedition(Expedition expedition) {
        this.expedition = expedition;
    }

    public void updateCommentCount(int i) {
        this.commentCount += i;
    }

    public void updateLikeCount(int i) {
        this.likeCount += i;
    }
}
