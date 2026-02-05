-- liquibase formatted sql

-- changeset Joe-Park:97
ALTER TABLE challenge_likes DROP FOREIGN KEY fk_challenge_likes_challenge_id;

-- changeset Joe-Park:98
ALTER TABLE challenge_likes
    ADD CONSTRAINT fk_challenge_likes_challenge_id
        FOREIGN KEY (challenge_id) REFERENCES challenges (challenge_id)
            ON DELETE CASCADE;
