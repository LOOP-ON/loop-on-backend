-- liquibase formatted sql

-- changeset Seungwon-Choi:4
CREATE TABLE terms
(
    terms_id   BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '약관 PK',
    code       VARCHAR(50)                         NOT NULL COMMENT '약관 코드 (약관 타입)',
    title      VARCHAR(100)                        NOT NULL COMMENT '약관 제목',
    required   BOOLEAN   DEFAULT TRUE              NOT NULL COMMENT '동의 필수 여부',
    content    TEXT                                NOT NULL COMMENT '약관 내용',
    version    VARCHAR(20)                         NOT NULL COMMENT '약관 버전',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일'
) COMMENT '약관 테이블';

-- changeset Seungwon-Choi:5
CREATE UNIQUE INDEX ux_terms_code_version ON terms (code, version);

-- changeset Seungwon-Choi:ck-terms-code
ALTER TABLE terms
    ADD CONSTRAINT ck_terms_code
        CHECK (code IN (
                        'TERMS_OF_SERVICE',
                        'PRIVACY_POLICY_REQUIRED',
                        'SERVICE_NATURE_NOTICE',
                        'PRIVACY_POLICY_OPTIONAL',
                        'THIRD_PARTY_SHARING',
                        'MARKETING_CONSENT'
            ));
