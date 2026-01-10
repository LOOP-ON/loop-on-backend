-- liquibase formatted sql

-- changeset yeeun:62
ALTER TABLE journeys
    CHANGE COLUMN content goal TEXT NULL COMMENT '목표';
