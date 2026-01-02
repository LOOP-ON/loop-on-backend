-- liquibase formatted sql

-- changeset Seungwon-Choi:46
CREATE TABLE comments
(
    comment_id   BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '댓글 PK',
    parent_id    BIGINT                              NULL COMMENT '부모 댓글 PK',
    user_id      BIGINT                              NOT NULL COMMENT '회원 PK',
    challenge_id BIGINT                              NOT NULL COMMENT '챌린지 PK',
    content      VARCHAR(500)                        NOT NULL COMMENT '댓글 내용',
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일'

) COMMENT '댓글 테이블';

-- changeset Seungwon-Choi:47
ALTER TABLE comments
    ADD CONSTRAINT fk_comments_parent_id FOREIGN KEY (parent_id) REFERENCES comments (comment_id);

-- changeset Seungwon-Choi:48
ALTER TABLE comments
    ADD CONSTRAINT fk_comments_user_id FOREIGN KEY (user_id) REFERENCES users (user_id);

-- changeset Seungwon-Choi:49
ALTER TABLE comments
    ADD CONSTRAINT fk_comments_challenge_id FOREIGN KEY (challenge_id) REFERENCES challenges (challenge_id);

-- changeset Seungwon-Choi:50
ALTER TABLE comments
    ADD CONSTRAINT ux_comments_parent_user_challenge_content UNIQUE (parent_id, user_id, challenge_id, content);
