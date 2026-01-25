-- liquibase formatted sql

-- changeset Seungwon-Choi:77
ALTER TABLE users ADD COLUMN social_id VARCHAR(200) NOT NULL COMMENT '소셜 로그인 ID';

-- rollback ALTER TABLE users DROP COLUMN social_id;
