--liquibase formatted sql

--changeset Joe-Park:101
ALTER TABLE journeys ADD COLUMN journey_order INT NOT NULL;

--changeset Joe-Park:102
CREATE UNIQUE INDEX idx_journey_user_order ON journeys (user_id, journey_order);