--liquibase formatted sql

--changeset Gang:91
ALTER TABLE notification_settings
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일';

--changeset Gang:92
ALTER TABLE notification_settings
    MODIFY COLUMN updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일';