package com.loopon.challenge.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
