-- liquibase formatted sql

-- changeset Seungwon-Choi:77
ALTER TABLE users ADD COLUMN social_id VARCHAR(200) NULL COMMENT '소셜 로그인 ID';
UPDATE users SET social_id = email WHERE social_id IS NULL;
ALTER TABLE users MODIFY COLUMN social_id VARCHAR(200) NOT NULL;
ALTER TABLE users ADD CONSTRAINT ux_users_provider_social_id UNIQUE (provider, social_id);
ALTER TABLE users MODIFY COLUMN password VARCHAR(255) NULL;
ALTER TABLE users DROP COLUMN birth_date;

-- rollback ALTER TABLE users ADD COLUMN birth_date DATE NULL;
-- rollback ALTER TABLE users MODIFY COLUMN password VARCHAR(255) NOT NULL;
-- rollback ALTER TABLE users DROP INDEX ux_users_provider_social_id;
-- rollback ALTER TABLE users DROP COLUMN social_id;
