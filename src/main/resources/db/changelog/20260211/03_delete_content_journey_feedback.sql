--liquibase formatted sql

--changeset Gang:07_drop_content_column_from_journey:87
ALTER TABLE journey_feedbacks
DROP COLUMN content;
