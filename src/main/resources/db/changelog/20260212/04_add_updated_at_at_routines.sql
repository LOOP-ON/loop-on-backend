--liquibase formatted sql

--changeset Joe-Park:136
ALTER TABLE routines ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP NULL COMMENT '수정일';