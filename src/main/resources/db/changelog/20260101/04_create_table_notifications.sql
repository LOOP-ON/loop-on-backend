-- liquibase formatted sql

-- changeset Seungwon-Choi:10
CREATE TABLE notifications
(
    notification_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '알림 PK',
    user_id         BIGINT                              NOT NULL COMMENT '회원 PK',
    type            VARCHAR(50)                         NOT NULL COMMENT '알림 이벤트 유형',
    message         VARCHAR(255)                        NOT NULL COMMENT '알림 메시지',
    is_read         BOOLEAN   DEFAULT FALSE             NOT NULL COMMENT '읽음 여부',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    related_id      BIGINT                              NULL COMMENT '관련 엔티티 ID'
) COMMENT '알림 로그 테이블';

-- changeset Seungwon-Choi:11
ALTER TABLE notifications
    ADD CONSTRAINT fk_notifications_user_id FOREIGN KEY (user_id) REFERENCES users (user_id);

-- changeset Seungwon-Choi:ck-notifications-type
ALTER TABLE notifications
    ADD CONSTRAINT ck_notifications_type
        CHECK (type IN (
                        'COMMENT_CREATED',
                        'FRIEND_REQUEST_RECEIVED',
                        'JOURNEY_REMIND_TRIGGERED'
            ));
