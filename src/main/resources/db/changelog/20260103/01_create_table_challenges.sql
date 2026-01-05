-- liquibase formatted sql

-- changeset Seungwon-Choi:29
CREATE TABLE challenges
(
    challenge_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '챌린지 PK',
    user_id      BIGINT                              NOT NULL COMMENT '회원 PK',
    journey_id   BIGINT                              NOT NULL COMMENT '여정 PK',
    content      VARCHAR(500)                        NOT NULL COMMENT '챌린지 설명',
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일'

) COMMENT '챌린지 테이블';

-- changeset Seungwon-Choi:30
ALTER TABLE challenges
    ADD CONSTRAINT fk_challenges_user_id FOREIGN KEY (user_id) REFERENCES users (user_id);

-- changeset Seungwon-Choi:31
ALTER TABLE challenges
    ADD CONSTRAINT fk_challenges_journey_id FOREIGN KEY (journey_id) REFERENCES journeys (journey_id);
