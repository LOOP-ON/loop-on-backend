-- liquibase formatted sql

-- changeset Yeeun-Jang:78
ALTER TABLE routines
    MODIFY COLUMN notification_time TIME NULL COMMENT '루틴 알림 시간';

-- rollback ALTER TABLE routines
-- rollback MODIFY COLUMN notification_time DATETIME NULL;
