--liquibase formatted sql

--changeset Joe-Park:135
ALTER TABLE routine_report ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP NULL COMMENT '수정일';