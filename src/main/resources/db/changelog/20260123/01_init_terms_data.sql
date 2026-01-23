-- liquibase formatted sql

-- changeset Seungwon-Choi:init-terms-data
INSERT INTO terms (code, title, content, mandatory, version, created_at, updated_at)
VALUES ('TERMS_OF_SERVICE', 'LOOP:ON 이용약관 동의', '제1조(목적) 본 약관은...', 1, '1.0', NOW(), NOW());

INSERT INTO terms (code, title, content, mandatory, version, created_at, updated_at)
VALUES ('PRIVACY_POLICY_REQUIRED', '개인정보 수집·이용 동의', '1. 수집하는 개인정보 항목...', 1, '1.0', NOW(), NOW());

INSERT INTO terms (code, title, content, mandatory, version, created_at, updated_at)
VALUES ('SERVICE_NATURE_NOTICE', '서비스 성격 고지', '본 서비스는 위치 기반 매칭을 포함하며...', 1, '1.0', NOW(), NOW());

INSERT INTO terms (code, title, content, mandatory, version, created_at, updated_at)
VALUES ('PRIVACY_POLICY_OPTIONAL', '개인정보 수집·이용 동의 (선택)', '선택적 수집 항목: 프로필 이미지...', 0, '1.0', NOW(), NOW());

INSERT INTO terms (code, title, content, mandatory, version, created_at, updated_at)
VALUES ('THIRD_PARTY_SHARING', '개인정보 제 3자 제공 동의', '제공받는 자: OO파트너스...', 0, '1.0', NOW(), NOW());

INSERT INTO terms (code, title, content, mandatory, version, created_at, updated_at)
VALUES ('MARKETING_CONSENT', '마케팅 정보 수신 동의', '이벤트 및 프로모션 알림...', 0, '1.0', NOW(), NOW());
