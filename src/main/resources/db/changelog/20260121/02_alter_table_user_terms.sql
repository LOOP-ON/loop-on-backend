-- liquibase formatted sql

-- changeset Seungwon-Choi:73
ALTER TABLE user_terms DROP FOREIGN KEY fk_user_terms_user_id;
ALTER TABLE user_terms DROP FOREIGN KEY fk_user_terms_terms_id;
ALTER TABLE user_terms DROP INDEX ux_user_terms_user_terms;

RENAME TABLE user_terms TO user_term_agreements;

ALTER TABLE user_term_agreements CHANGE user_terms_id agreement_id BIGINT AUTO_INCREMENT COMMENT '동의 내역 PK';

ALTER TABLE user_term_agreements CHANGE terms_id term_id BIGINT NOT NULL COMMENT '약관 PK';

ALTER TABLE user_term_agreements CHANGE agreed_at created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '동의 일시 (생성일)';

ALTER TABLE user_term_agreements DROP COLUMN agreed;

ALTER TABLE user_term_agreements ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NULL COMMENT '수정일';


-- changeset Seungwon-Choi:74
ALTER TABLE user_term_agreements
    ADD CONSTRAINT fk_user_term_agreements_user_id
        FOREIGN KEY (user_id) REFERENCES users (user_id);

ALTER TABLE user_term_agreements
    ADD CONSTRAINT fk_user_term_agreements_term_id
        FOREIGN KEY (term_id) REFERENCES terms (term_id);

ALTER TABLE user_term_agreements
    ADD CONSTRAINT ux_user_term_agreements_mapping
        UNIQUE (user_id, term_id);
