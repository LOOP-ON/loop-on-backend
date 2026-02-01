-- liquibase formatted sql

-- changeset Joe-Park:77
ALTER TABLE expeditions ADD COLUMN current_users INT NOT NULL COMMENT '현재 탐험대 인원 수' DEFAULT 0;