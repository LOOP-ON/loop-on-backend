-- liquibase formatted sql

-- changeset Seungwon-Choi:37
CREATE TABLE challenge_images
(
    image_id      BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '챌린지 이미지 PK',
    challenge_id  BIGINT                              NOT NULL COMMENT '챌린지 PK',
    image_url     TEXT                                NOT NULL COMMENT '이미지 URL',
    display_order INT                                 NOT NULL COMMENT '이미지 표시 순서',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일'
) COMMENT '챌린지 이미지 테이블';

-- changeset Seungwon-Choi:38
ALTER TABLE challenge_images
    ADD CONSTRAINT fk_challenge_images_challenge_id FOREIGN KEY (challenge_id) REFERENCES challenges (challenge_id);

-- changeset Seungwon-Choi:39
ALTER TABLE challenge_images
    ADD CONSTRAINT ux_challenge_images_challenge_image_order UNIQUE (challenge_id, display_order);
