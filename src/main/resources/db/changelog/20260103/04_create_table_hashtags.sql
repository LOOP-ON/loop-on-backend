-- liquibase formatted sql

-- changeset Seungwon-Choi:38
CREATE TABLE hashtags
(
    hashtag_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '해시태그 PK',
    name       VARCHAR(100)                        NOT NULL COMMENT '해시태그 이름'
) COMMENT '해시태그 테이블';

-- changeset Seungwon-Choi:39
ALTER TABLE hashtags
    ADD CONSTRAINT ux_hashtags_name UNIQUE (name);
