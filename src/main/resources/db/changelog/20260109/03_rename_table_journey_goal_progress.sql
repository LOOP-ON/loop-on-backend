-- Liquibase formatted sql

-- changeset yeeun:62
ALTER TABLE journey_goal_progress
    RENAME TO routine_progress;

-- changeset yeeun:63
ALTER TABLE routine_progress
    CHANGE goal_id routine_id BIGINT NOT NULL COMMENT '루틴 PK';

-- changeset yeeun:64
ALTER TABLE routine_progress
DROP FOREIGN KEY fk_journey_goal_progress_goal_id;

-- changeset yeeun:65
ALTER TABLE routine_progress
    ADD CONSTRAINT fk_routine_progress_routine_id
        FOREIGN KEY (routine_id)
            REFERENCES routines (routine_id);

-- changeset yeeun:66
ALTER TABLE routine_progress
    COMMENT = '루틴 진행 기록';
