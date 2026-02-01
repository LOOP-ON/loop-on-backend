-- liquibase formatted sql

-- changeset Seungwon-Choi:80
ALTER TABLE verifications
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL COMMENT '수정일';

-- rollback ALTER TABLE verifications DROP COLUMN updated_at;
