-- liquibase formatted sql

-- changeset Seungwon-Choi:76
ALTER TABLE users ADD COLUMN bio VARCHAR(100) NULL COMMENT '한 줄 소개';
ALTER TABLE users ADD COLUMN status_message VARCHAR(100) NULL COMMENT '상태 메시지';

-- rollback ALTER TABLE users DROP COLUMN status_message;
-- rollback ALTER TABLE users DROP COLUMN bio;
