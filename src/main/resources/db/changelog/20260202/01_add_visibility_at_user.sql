-- liquibase formatted sql

-- changeset Joe-Park:99
ALTER TABLE users
    ADD COLUMN visibility VARCHAR(20) NOT NULL DEFAULT 'PUBLIC';