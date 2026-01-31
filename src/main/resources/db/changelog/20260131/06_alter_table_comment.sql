-- liquibase formatted sql

-- changeset Joe-Park:90
CREATE INDEX idx_comments_parent_id ON comments (parent_id);