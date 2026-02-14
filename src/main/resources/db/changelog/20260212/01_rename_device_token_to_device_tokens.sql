--liquibase formatted sql

--changeset Gang:88
ALTER TABLE device_token
    RENAME TO device_tokens;