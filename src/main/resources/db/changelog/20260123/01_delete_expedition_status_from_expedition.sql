-- liquibase formatted sql

-- changeset Joe-Park:75
ALTER TABLE expeditions DROP CONSTRAINT ck_expeditions_status;

-- changeset Joe-Park:76
ALTER TABLE expeditions DROP COLUMN status;