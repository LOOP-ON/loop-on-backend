-- liquibase formatted sql

-- changeset Joe-Park:73
ALTER TABLE challenges ADD COLUMN expedition_id BIGINT;

-- changeset Joe-Park:74
ALTER TABLE challenges
ADD CONSTRAINT fk_challenge_expedition
FOREIGN KEY (expedition_id) REFERENCES expeditions (expedition_id);