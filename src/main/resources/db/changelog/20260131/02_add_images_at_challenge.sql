-- liquibase formatted sql

-- changeset Joe-Park:81
CREATE INDEX idx_challenge_images_challenge_id ON challenge_images(challenge_id);

-- changeset Joe-Park:82
CREATE INDEX idx_challenge_images_list ON challenge_images(challenge_id, display_order);