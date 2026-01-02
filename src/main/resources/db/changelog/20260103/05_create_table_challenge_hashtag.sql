-- liquibase formatted sql

-- changeset Seungwon-Choi:42
CREATE TABLE challenge_hashtag
(
    challenge_id         BIGINT                              NOT NULL COMMENT '챌린지 PK',
    hashtag_id           BIGINT                              NOT NULL COMMENT '해시태그 PK'
) COMMENT '챌린지 해시태그 테이블';

-- changeset Seungwon-Choi:43
ALTER TABLE challenge_hashtag
    ADD CONSTRAINT fk_challenge_hashtag_challenge_id FOREIGN KEY (challenge_id) REFERENCES challenges (challenge_id);

-- changeset Seungwon-Choi:44
ALTER TABLE challenge_hashtag
    ADD CONSTRAINT fk_challenge_hashtag_hashtag_id FOREIGN KEY (hashtag_id) REFERENCES hashtags (hashtag_id);

-- changeset Seungwon-Choi:45
ALTER TABLE challenge_hashtag
    ADD CONSTRAINT ux_challenge_hashtag_challenge_hashtag UNIQUE (challenge_id, hashtag_id);
