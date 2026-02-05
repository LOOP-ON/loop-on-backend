--liquibase formatted sql

--changeset Gang:01_create_table_device_token:81
CREATE TABLE device_token (
                              id BIGINT NOT NULL AUTO_INCREMENT,
                              user_id BIGINT NOT NULL,
                              token VARCHAR(255) NOT NULL,
                              environment_type VARCHAR(20) NOT NULL,
                              updated_at DATETIME NOT NULL,
                              PRIMARY KEY (id),
                              CONSTRAINT uk_device_token_token_env UNIQUE (token, environment_type),
                              CONSTRAINT fk_device_token_user
                                  FOREIGN KEY (user_id)
                                      REFERENCES users (user_id)
                                      ON DELETE CASCADE
);
--changeset Gang:02_create_table_notification_settings:82
CREATE TABLE notification_settings (
                                       notification_setting_id BIGINT NOT NULL AUTO_INCREMENT,

                                       user_id BIGINT NOT NULL,

                                       all_enabled BOOLEAN NOT NULL,
                                       routine_enabled BOOLEAN NOT NULL,
                                       routine_alert_mode VARCHAR(20) NOT NULL,

                                       unfinished_goal_reminder_enabled BOOLEAN NOT NULL,
                                       unfinished_goal_reminder_time TIME NOT NULL DEFAULT '23:00:00',

                                       day_end_journey_reminder_enabled BOOLEAN NOT NULL,
                                       day_end_journey_reminder_time TIME NULL,

                                       journey_complete_enabled BOOLEAN NOT NULL,

                                       friend_request_enabled BOOLEAN NOT NULL,
                                       like_enabled BOOLEAN NOT NULL,
                                       comment_enabled BOOLEAN NOT NULL,
                                       notice_enabled BOOLEAN NOT NULL,
                                       marketing_enabled BOOLEAN NOT NULL,

                                       updated_at DATETIME NOT NULL,

                                       PRIMARY KEY (notification_setting_id),

                                       CONSTRAINT uk_notification_settings_user UNIQUE (user_id),

                                       CONSTRAINT fk_notification_settings_user
                                           FOREIGN KEY (user_id)
                                               REFERENCES users (user_id)
                                               ON DELETE CASCADE
);