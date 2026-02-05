-- liquibase formatted sql

-- changeset Joe-Park:83
CREATE INDEX idx_challenge_hashtag_challenge_id ON challenge_hashtag(challenge_id);

-- changeset Joe-Park:84
CREATE INDEX idx_challenge_hashtag_hashtag_id ON challenge_hashtag(hashtag_id);
