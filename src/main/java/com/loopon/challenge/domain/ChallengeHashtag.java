package com.loopon.challenge.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "challenge_hashtag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChallengeHashtag {

    //복합 pk 설정
    @EmbeddedId
    private ChallengeHashtagId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("challengeId")
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("hashtagId")
    @JoinColumn(name = "hashtag_id")
    private Hashtag hashtag;

    public ChallengeHashtag(Challenge challenge, Hashtag hashtag) {
        this.challenge = challenge;
        this.hashtag = hashtag;
        this.id = new ChallengeHashtagId(
                challenge.getId(),
                hashtag.getId()
        );
    }
}
