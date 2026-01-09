-- liquibase formatted sql

-- changeset yeeun:59
ALTER TABLE journey_goals RENAME TO routines;

-- changeset yeeun:60
ALTER TABLE routines
    COMMENT = '루틴';

-- changeset yeeun:61
ALTER TABLE routines
    CHANGE COLUMN goal_id routine_id BIGINT
    NOT NULL AUTO_INCREMENT COMMENT '루틴 PK';
