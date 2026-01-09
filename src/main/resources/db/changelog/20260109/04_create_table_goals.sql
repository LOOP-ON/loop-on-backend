-- Liquibase formatted sql

-- changeset yeeun:67
CREATE TABLE goals
(
    goal_id    BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '목표 PK',
    content    VARCHAR(255)                        NOT NULL COMMENT '목표 내용',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일'
) COMMENT '목표 테이블';
