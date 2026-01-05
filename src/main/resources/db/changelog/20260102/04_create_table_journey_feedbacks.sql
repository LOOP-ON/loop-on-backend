-- Liquibase formatted sql

-- changeset Seungwon-Choi:26
CREATE TABLE journey_feedbacks
(
    feedback_id   BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '여정 피드백 PK',
    journey_id    BIGINT      NOT NULL COMMENT '여정 PK',
    content       TEXT        NOT NULL COMMENT '피드백 내용',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일'
) COMMENT '여정 피드백 테이블';

-- changeset Seungwon-Choi:27
ALTER TABLE journey_feedbacks
    ADD CONSTRAINT fk_journey_feedbacks_journey_id FOREIGN KEY (journey_id) REFERENCES journeys (journey_id);
