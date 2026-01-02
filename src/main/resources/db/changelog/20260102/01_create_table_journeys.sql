-- liquibase formatted sql

-- changeset Seungwon-Choi:21
CREATE TABLE journeys
(
    journey_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '여정 PK',
    user_id    BIGINT                              NOT NULL COMMENT '회원 PK',
    category   VARCHAR(30)                         NOT NULL COMMENT '여정 카테고리',
    content    TEXT                                NULL COMMENT '여정 내용',
    status     VARCHAR(20)                         NOT NULL COMMENT '여정 상태',
    start_date DATE                                NOT NULL COMMENT '여정 시작일',
    end_date   DATE                                NULL COMMENT '여정 종료일',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일'
) COMMENT '회원 여정 테이블';

-- changeset Seungwon-Choi:22
ALTER TABLE journeys
    ADD CONSTRAINT fk_journeys_user_id FOREIGN KEY (user_id) REFERENCES users (user_id);

-- changeset Seungwon-Choi:ck-journeys-category
ALTER TABLE journeys
    ADD CONSTRAINT ck_journeys_category
        CHECK (category IN (
                            'SKILL',
                            'ROUTINE',
                            'MENTAL'
            ));

-- changeset Seungwon-Choi:ck-journeys-status
ALTER TABLE journeys
    ADD CONSTRAINT ck_journeys_status
        CHECK (status IN (
                          'PLANNED',
                          'IN_PROGRESS',
                          'COMPLETED',
                          'CANCELLED'
            ));
