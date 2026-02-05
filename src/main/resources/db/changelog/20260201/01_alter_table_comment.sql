-- liquibase formatted sql

-- changeset Joe-Park:90
CREATE INDEX idx_comments_parent_id ON comments (parent_id);

-- changeset Joe-Park:91
ALTER TABLE comments DROP FOREIGN KEY fk_comments_parent_id;

-- changeset Joe-Park:92
ALTER TABLE comments
    ADD CONSTRAINT fk_comments_parent_id
        FOREIGN KEY (parent_id) REFERENCES comments (comment_id)
            ON DELETE CASCADE;

-- changeset Joe-Park:93
ALTER TABLE comments DROP FOREIGN KEY fk_comments_challenge_id;

-- changeset Joe-Park:94
ALTER TABLE comments
    ADD CONSTRAINT fk_comments_challenge_id
        FOREIGN KEY (challenge_id) REFERENCES challenges (challenge_id)
            ON DELETE CASCADE;

