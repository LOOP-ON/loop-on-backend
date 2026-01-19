-- liquibase formatted sql

-- changeset Joe-Park:71
ALTER TABLE challenges ADD COLUMN expedition_id BIGINT;

-- changeset Joe-Park:72
ALTER TABLE challenges
ADD CONSTRAINT fk_challenge_expedition
FOREIGN KEY (expedition_id) REFERENCES expeditions (expedition_id);