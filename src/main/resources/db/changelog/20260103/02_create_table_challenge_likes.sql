-- liquibase formatted sql

-- changeset Seungwon-Choi:31
CREATE TABLE challenge_likes
(
    challenge_like_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '챌린지 좋아요 PK',
    user_id           BIGINT                              NOT NULL COMMENT '회원 PK',
    challenge_id      BIGINT                              NOT NULL COMMENT '챌린지 PK',
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일'
) COMMENT '챌린지 좋아요 테이블';

-- changeset Seungwon-Choi:32
ALTER TABLE challenge_likes
    ADD CONSTRAINT fk_challenge_likes_user_id FOREIGN KEY (user_id) REFERENCES users (user_id);

-- changeset Seungwon-Choi:33
ALTER TABLE challenge_likes
    ADD CONSTRAINT fk_challenge_likes_challenge_id FOREIGN KEY (challenge_id) REFERENCES challenges (challenge_id);

-- changeset Seungwon-Choi:34
ALTER TABLE challenge_likes
    ADD CONSTRAINT ux_challenge_likes_user_challenge UNIQUE (user_id, challenge_id);
