-- liquibase formatted sql

-- changeset Seungwon-Choi:16
CREATE TABLE friends
(
    friend_id    BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '친구 관계 PK',
    requester_id BIGINT                              NOT NULL COMMENT '회원 PK',
    receiver_id  BIGINT                              NOT NULL COMMENT '친구 회원 PK',
    status       VARCHAR(50)                         NOT NULL COMMENT '친구 관계 상태',
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일'
) COMMENT '친구 관계 테이블';

-- changeset Seungwon-Choi:17
ALTER TABLE friends
    ADD CONSTRAINT fk_friends_requester_id FOREIGN KEY (requester_id) REFERENCES users (user_id);

-- changeset Seungwon-Choi:18
ALTER TABLE friends
    ADD CONSTRAINT fk_friends_receiver_id FOREIGN KEY (receiver_id) REFERENCES users (user_id);

-- changeset Seungwon-Choi:ck-friends-status
ALTER TABLE friends
    ADD CONSTRAINT ck_friends_status
        CHECK (status IN (
                          'PENDING',
                          'ACCEPTED',
                          'REJECTED',
                          'BLOCKED'
            ));

-- changeset Seungwon-Choi:19
ALTER TABLE friends
    ADD COLUMN user_low_id  BIGINT
        GENERATED ALWAYS AS (LEAST(requester_id, receiver_id)) STORED,
    ADD COLUMN user_high_id BIGINT
        GENERATED ALWAYS AS (GREATEST(requester_id, receiver_id)) STORED;

-- changeset Seungwon-Choi:20
CREATE UNIQUE INDEX ux_friends_pair
    ON friends (user_low_id, user_high_id);
