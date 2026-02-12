--liquibase formatted sql

--changeset Joe-Park:137
ALTER TABLE routine_progress ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    NOT NULL COMMENT '생성일';

--changeset Joe-Park:138
ALTER TABLE routine_progress ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP NULL COMMENT '수정일';