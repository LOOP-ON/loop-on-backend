-- Liquibase formatted sql

-- changeset Seungwon-Choi:56
CREATE TABLE expedition_users
(
    expedition_user_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '사용자 탐험가 PK',
    user_id            BIGINT COMMENT '사용자 PK',
    expedition_id      BIGINT                              NOT NULL COMMENT '탐험대 PK',
    status             VARCHAR(20)                         NOT NULL COMMENT '상태',
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일'
) COMMENT '사용자 탐험가 테이블';

-- changeset Seungwon-Choi:57
ALTER TABLE expedition_users
    ADD CONSTRAINT fk_expedition_users_user_id FOREIGN KEY (user_id) REFERENCES users (user_id);

-- changeset Seungwon-Choi:58
ALTER TABLE expedition_users
    ADD CONSTRAINT fk_expedition_users_expedition_id FOREIGN KEY (expedition_id) REFERENCES expeditions (expedition_id);

-- changeset Seungwon-Choi:ck-expedition-users-status
ALTER TABLE expedition_users
    ADD CONSTRAINT ck_expedition_users_status
        CHECK (status IN (
                          'PENDING',
                          'APPROVED',
                          'REJECTED'
            ));
