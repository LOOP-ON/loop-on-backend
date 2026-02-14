--liquibase formatted sql

--changeset Joe-Park:139
ALTER TABLE journeys DROP CONSTRAINT ck_journeys_category;

--changeset Joe-Park:140
ALTER TABLE journeys ADD CONSTRAINT ck_journeys_category
    CHECK (category IN ('GROWTH', 'ROUTINE', 'MENTAL'));