-- liquibase formatted sql

-- changeset Seungwon-Choi:54
CREATE TABLE verifications
(
    verification_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '인증 PK',
    channel         VARCHAR(20)                         NOT NULL COMMENT '인증 채널',
    purpose         VARCHAR(30)                         NOT NULL COMMENT '인증 목적',
    target          VARCHAR(100)                        NOT NULL COMMENT '인증 대상 (이메일/전화번호)',
    code            VARCHAR(6)                          NOT NULL COMMENT '인증 코드',
    status          VARCHAR(20)                         NOT NULL COMMENT '인증 상태',
    attempt_count   INT        DEFAULT 0                NOT NULL COMMENT '인증 시도 횟수',
    expires_at      TIMESTAMP                           NOT NULL COMMENT '만료 시각',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일'
) COMMENT '인증 테이블';

-- changeset Seungwon-Choi:55
ALTER TABLE verifications
    ADD CONSTRAINT ck_verifications_channel
        CHECK (channel IN (
                            'EMAIL',
                            'SMS'
            ));

-- changeset Seungwon-Choi:56
ALTER TABLE verifications
    ADD CONSTRAINT ck_verifications_purpose
        CHECK (purpose IN (
                           'SIGN_UP',
                           'FIND_EMAIL',
                           'FIND_PASSWORD'
            ));

-- changeset Seungwon-Choi:57
ALTER TABLE verifications
    ADD CONSTRAINT ck_verifications_status
        CHECK (status IN (
                           'PENDING',
                           'VERIFIED',
                           'EXPIRED',
                           'FAILED'
            ));
