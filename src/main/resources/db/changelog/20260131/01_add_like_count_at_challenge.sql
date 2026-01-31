-- liquibase formatted sql

-- changeset Joe-Park:80
ALTER TABLE challenges ADD COLUMN like_count INT NOT NULL DEFAULT 0;
