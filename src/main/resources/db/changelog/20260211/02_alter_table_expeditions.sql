-- liquibase formatted sql

-- changeset Seungwon-Choi:fix-expedition-category-enum
-- comment: 탐험대 카테고리 Enum 불일치 수정 (SKILL -> GROWTH)

ALTER TABLE expeditions DROP CONSTRAINT ck_expeditions_category;

UPDATE expeditions
SET category = 'GROWTH'
WHERE category = 'SKILL';

ALTER TABLE expeditions
    ADD CONSTRAINT ck_expeditions_category
        CHECK (category IN (
                            'ROUTINE',
                            'GROWTH',
                            'MENTAL'
            ));
