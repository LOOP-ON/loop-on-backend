-- liquibase formatted sql

-- changeset Seungwon-Choi:6
CREATE TABLE user_terms
(
    user_terms_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '회원 약관 동의 PK',
    user_id       BIGINT                              NOT NULL COMMENT '회원 PK',
    terms_id      BIGINT                              NOT NULL COMMENT '약관 PK',
    agreed        BOOLEAN                             NOT NULL COMMENT '동의 여부',
    agreed_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '동의 일시',
    revoked_at    TIMESTAMP                           NULL COMMENT '동의 철회 일시'
) COMMENT '회원 약관 동의 테이블';

-- changeset Seungwon-Choi:7
ALTER TABLE user_terms
    ADD CONSTRAINT fk_user_terms_user_id
        FOREIGN KEY (user_id) REFERENCES users (user_id);

-- changeset Seungwon-Choi:8
ALTER TABLE user_terms
    ADD CONSTRAINT fk_user_terms_terms_id
        FOREIGN KEY (terms_id) REFERENCES terms (terms_id);

-- changeset Seungwon-Choi:9
ALTER TABLE user_terms
    ADD CONSTRAINT ux_user_terms_user_terms
        UNIQUE (user_id, terms_id);
