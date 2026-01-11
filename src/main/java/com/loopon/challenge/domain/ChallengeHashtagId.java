package com.loopon.challenge.domain;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChallengeHashtagId implements Serializable {

    @Column(name = "challenge_id")
    private Long challengeId;

    @Column(name = "hashtag_id")
    private Long hashtagId;
}
