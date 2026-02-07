-- liquibase formatted sql

-- changeset Joe-Park:86
CREATE TABLE comment_likes
(
    comment_like_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '댓글 좋아요 PK',
    user_id           BIGINT                              NOT NULL COMMENT '회원 PK',
    comment_id      BIGINT                              NOT NULL COMMENT '댓글 PK',
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일'
) COMMENT '댓글 좋아요 테이블';

-- changeset Joe-Park:87
ALTER TABLE comment_likes
    ADD CONSTRAINT fk_comment_likes_user_id FOREIGN KEY (user_id) REFERENCES users (user_id);

-- changeset Joe-Park:88
ALTER TABLE comment_likes
    ADD CONSTRAINT fk_comment_likes_comment_id FOREIGN KEY (comment_id) REFERENCES comments (comment_id);

-- changeset Joe-Park:89
ALTER TABLE comment_likes
    ADD CONSTRAINT ux_comment_likes_user_comment UNIQUE (user_id, comment_id);
