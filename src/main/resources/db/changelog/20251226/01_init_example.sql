-- liquibase formatted sql

-- changeset Seungwon-Choi:1 runInTransaction:false
CREATE TABLE IF NOT EXISTS users (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT   NOT NULL COMMENT '회원 PK',
    name       VARCHAR(50)                         NOT NULL COMMENT '회원 이름',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '데이터 생성일자',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '데이터 수정일자'
);

CREATE UNIQUE INDEX idx_users_name ON users (name);
