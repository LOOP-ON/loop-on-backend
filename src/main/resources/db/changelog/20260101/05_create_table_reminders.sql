-- liquibase formatted sql

-- changeset Seungwon-Choi:13
CREATE TABLE reminders
(
    reminder_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '리마인드 PK',
    user_id     BIGINT               NOT NULL COMMENT '회원 PK',
    target_type VARCHAR(50)          NOT NULL COMMENT '리마인드 대상 타입',
    target_id   BIGINT               NOT NULL COMMENT '리마인드 대상 ID',
    remind_at   TIMESTAMP            NOT NULL COMMENT '리마인드 예정 시각',
    enabled     BOOLEAN DEFAULT TRUE NOT NULL COMMENT '리마인드 활성 여부'
) COMMENT '리마인드 테이블';

-- changeset Seungwon-Choi:14
ALTER TABLE reminders
    ADD CONSTRAINT fk_reminders_user_id FOREIGN KEY (user_id) REFERENCES users (user_id);

-- changeset Seungwon-Choi:ck-reminders-target-type
ALTER TABLE reminders
    ADD CONSTRAINT ck_notification_reminders_target_type
        CHECK (target_type IN (
                               'JOURNEY',
                               'GOAL'
            ));

-- changeset Seungwon-Choi:15
CREATE UNIQUE INDEX ux_reminders_user_target ON reminders (user_id, target_type, target_id);
