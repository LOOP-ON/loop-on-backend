-- liquibase formatted sql

-- changeset yeeun:62
ALTER TABLE journeys
    CHANGE COLUMN goal routine TEXT NULL COMMENT '목표';
