-- liquibase formatted sql

-- changeset Seungwon-Choi:54
CREATE TABLE expeditions
(
    expedition_id  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '탐험대 PK',
    admin_id     BIGINT                              NOT NULL COMMENT '관리자 PK',
    title        VARCHAR(200)                        NOT NULL COMMENT '탐험대 제목',
    user_limit INT                                 NOT NULL COMMENT '탐험대 인원 제한',
    category     VARCHAR(20)                         NOT NULL COMMENT '여정 카테고리',
    visibility   VARCHAR(20)                         NOT NULL COMMENT '탐험대 공개 여부',
    password     VARCHAR(100)                        NULL COMMENT '탐험대 비밀번호',
    status       VARCHAR(20)                         NOT NULL COMMENT '탐험대 상태',
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일'
) COMMENT '탐험대 테이블';

-- changeset Seungwon-Choi:55
ALTER TABLE expeditions
    ADD CONSTRAINT fk_expeditions_admin_id FOREIGN KEY (admin_id) REFERENCES users (user_id);

-- changeset Seungwon-Choi:ck-expeditions-category
ALTER TABLE expeditions
    ADD CONSTRAINT ck_expeditions_category
        CHECK (category IN (
                            'SKILL',
                            'ROUTINE',
                            'MENTAL'
            ));

-- changeset Seungwon-Choi:ck-expeditions-visibility
ALTER TABLE expeditions
    ADD CONSTRAINT ck_expeditions_visibility
        CHECK (visibility IN (
                              'PUBLIC',
                              'PRIVATE'
            ));

-- changeset Seungwon-Choi:ck-expeditions-status
ALTER TABLE expeditions
    ADD CONSTRAINT ck_expeditions_status
        CHECK (status IN (
                          'IN_PROGRESS',
                          'COMPLETED',
                          'DELETED'
            ));
