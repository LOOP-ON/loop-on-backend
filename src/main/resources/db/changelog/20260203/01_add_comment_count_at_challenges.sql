-- liquibase formatted sql

-- changeset Joe-Park:100
ALTER TABLE challenges ADD COLUMN comment_count INT NOT NULL DEFAULT 0;
