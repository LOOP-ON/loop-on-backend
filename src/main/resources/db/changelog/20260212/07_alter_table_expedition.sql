--liquibase formatted sql

--changeset Joe-Park:141
ALTER TABLE expeditions DROP CONSTRAINT ck_expeditions_category;

--changeset Joe-Park:142
ALTER TABLE expeditions ADD CONSTRAINT ck_expeditions_category
    CHECK (category IN ('GROWTH', 'ROUTINE', 'MENTAL'));