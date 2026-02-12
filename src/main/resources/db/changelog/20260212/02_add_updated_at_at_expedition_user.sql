--liquibase formatted sql

--changeset Joe-Park:134
ALTER TABLE expedition_users ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP NULL COMMENT '수정일';