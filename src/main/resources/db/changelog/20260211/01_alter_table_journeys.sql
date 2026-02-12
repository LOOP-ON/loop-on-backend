-- liquibase formatted sql

-- changeset Seungwon-Choi:fix-journey-category-enum
-- comment: 여정 카테고리 Enum 불일치 수정 (SKILL -> GROWTH)

ALTER TABLE journeys DROP CONSTRAINT ck_journeys_category;

UPDATE journeys
SET category = 'GROWTH'
WHERE category = 'SKILL';

ALTER TABLE journeys
    ADD CONSTRAINT ck_journeys_category
        CHECK (category IN (
                            'ROUTINE',
                            'GROWTH',
                            'MENTAL'
            ));
