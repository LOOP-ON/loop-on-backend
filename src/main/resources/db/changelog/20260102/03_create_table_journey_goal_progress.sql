-- Liquibase formatted sql

-- changeset Seungwon-Choi:25
CREATE TABLE journey_goal_progress
(
    progress_id      BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '목표 진행 PK',
    goal_id          BIGINT       NOT NULL COMMENT '여정 목표 PK',
    progress_date    DATE         NOT NULL COMMENT '진행 날짜',
    image_url        TEXT         NULL COMMENT '완료 인증 이미지 URL',
    status           VARCHAR(20)  NOT NULL COMMENT '진행 상태',
    postponed_reason VARCHAR(100) NULL COMMENT '미룬 사유',
    completed_at     TIMESTAMP    NULL COMMENT '완료 시각'
) COMMENT '여정 목표 진행 기록';

-- changeset Seungwon-Choi:26
ALTER TABLE journey_goal_progress
    ADD CONSTRAINT fk_journey_goal_progress_goal_id FOREIGN KEY (goal_id) REFERENCES journey_goals (goal_id);

-- changeset Seungwon-Choi:ck-journey_goal_progress-status
ALTER TABLE journey_goal_progress
    ADD CONSTRAINT ck_journey_goal_progress_status
        CHECK (status IN (
                          'IN_PROGRESS',
                          'COMPLETED',
                          'POSTPONED'
            ));
