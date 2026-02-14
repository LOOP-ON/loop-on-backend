--liquibase formatted sql

--changeset Gang:89
ALTER TABLE device_tokens
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일';

--changeset Gang:90
ALTER TABLE device_tokens
    MODIFY COLUMN updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일';