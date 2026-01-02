-- liquibase formatted sql

-- changeset Seungwon-Choi:51
CREATE TABLE growth_reports
(
    report_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '성장 리포트 PK',
    user_id          BIGINT                              NOT NULL COMMENT '회원 PK',
    content          TEXT                                NOT NULL COMMENT '리포트 내용',
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일'
) COMMENT '성장 리포트 테이블';

-- changeset Seungwon-Choi:52
ALTER TABLE growth_reports
    ADD CONSTRAINT fk_growth_reports_user_id FOREIGN KEY (user_id) REFERENCES users (user_id);

-- changeset Seungwon-Choi:53
ALTER TABLE growth_reports
    ADD CONSTRAINT ux_growth_reports_user_created_at UNIQUE (user_id, created_at);
