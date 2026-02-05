-- liquibase formatted sql

-- changeset Joe-Park:95
ALTER TABLE comment_likes DROP FOREIGN KEY fk_comment_likes_comment_id;

-- changeset Joe-Park:96
ALTER TABLE comment_likes
    ADD CONSTRAINT fk_comment_likes_comment_id
        FOREIGN KEY (comment_id) REFERENCES comments (comment_id)
            ON DELETE CASCADE;
