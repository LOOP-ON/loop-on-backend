-- liquibase formatted sql

-- changeset Seungwon-Choi:1
CREATE TABLE users
(
    user_id           BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '회원 PK',
    provider          VARCHAR(20)                           NOT NULL COMMENT '회원 제공자',
    name              VARCHAR(20)                           NOT NULL COMMENT '회원 이름',
    nickname          VARCHAR(30)                           NOT NULL COMMENT '회원 닉네임',
    birth_date        DATE                                  NULL COMMENT '회원 생년월일',
    email             VARCHAR(254)                          NOT NULL COMMENT '회원 이메일',
    password          VARCHAR(255)                          NULL COMMENT '회원 비밀번호',
    profile_image_url TEXT                                  NULL COMMENT '회원 프로필 이미지 URL',
    user_status       VARCHAR(20) DEFAULT 'ACTIVE'          NOT NULL COMMENT '회원 상태',
    created_at        TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    updated_at        TIMESTAMP   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일'
) COMMENT '회원 테이블';

-- changeset Seungwon-Choi:2
CREATE UNIQUE INDEX ux_users_email ON users (email);

-- changeset Seungwon-Choi:3
CREATE UNIQUE INDEX ux_users_nickname ON users (nickname);

-- changeset Seungwon-Choi:ck-users-status
ALTER TABLE users
    ADD CONSTRAINT ck_users_status CHECK (user_status IN ('ACTIVE', 'INACTIVE', 'DELETED'));
