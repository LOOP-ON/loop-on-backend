-- liquibase formatted sql

-- changeset Joe-Park:85
ALTER TABLE comments ADD COLUMN like_count INT NOT NULL DEFAULT 0;
