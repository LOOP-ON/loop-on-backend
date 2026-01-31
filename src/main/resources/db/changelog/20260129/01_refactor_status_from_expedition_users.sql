-- liquibase formatted sql

-- changeset Joe-Park:77
ALTER TABLE expedition_users
DROP CHECK ck_expedition_users_status;

-- changeset Joe-Park:78
ALTER TABLE expedition_users
    ADD CONSTRAINT ck_expedition_users_status
        CHECK (status IN ('APPROVED', 'EXPELLED'));