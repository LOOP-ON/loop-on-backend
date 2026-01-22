-- Liquibase formatted sql

-- changeset yeeun:69
CREATE TABLE insights
(
    insight_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '인사이트 PK',
    goal_id    BIGINT                              NOT NULL COMMENT '목표 PK',
    content    TEXT                                NOT NULL COMMENT '인사이트 내용',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일'
) COMMENT '인사이트';

-- changeset yeeun:70
ALTER TABLE insights
    ADD CONSTRAINT fk_insights_goal_id
        FOREIGN KEY (goal_id)
            REFERENCES goals (goal_id);
