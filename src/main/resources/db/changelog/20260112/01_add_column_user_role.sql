-- liquibase formatted sql

-- changeset Seungwon-Choi:71
ALTER TABLE users
    ADD COLUMN role VARCHAR(20) DEFAULT 'ROLE_USER' NOT NULL COMMENT '회원 역할';

-- changeset Seungwon-Choi:72
ALTER TABLE users
    ADD CONSTRAINT ck_users_role CHECK (role IN ('ROLE_USER', 'ROLE_ADMIN'));
