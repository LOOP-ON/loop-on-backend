-- liquibase formatted sql

-- changeset Seungwon-Choi:23
CREATE TABLE journey_goals
(
    goal_id           BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '여정 목표 PK',
    journey_id        BIGINT                              NOT NULL COMMENT '여정 PK',
    content           VARCHAR(255)                        NOT NULL COMMENT '목표 내용',
    notification_time TIMESTAMP                           NULL COMMENT '알림 시각',
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일'
) COMMENT '여정 목표 테이블';

-- changeset Seungwon-Choi:24
ALTER TABLE journey_goals
    ADD CONSTRAINT fk_journey_goals_journey_id FOREIGN KEY (journey_id) REFERENCES journeys (journey_id);
