-- liquibase formatted sql

-- changeset Seungwon-Choi:51
CREATE TABLE growth_reports
(
    report_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '성장 리포트 PK',
    content          TEXT                                NOT NULL COMMENT '리포트 내용',
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일'
) COMMENT '성장 리포트 테이블';
