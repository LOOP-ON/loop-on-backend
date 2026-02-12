--liquibase formatted sql

--changeset Gang:06_add_rate_columns_to_journey_feedbacks:86
ALTER TABLE journey_feedbacks
    ADD COLUMN day1_rate INT,
    ADD COLUMN day2_rate INT,
    ADD COLUMN day3_rate INT,
    ADD COLUMN total_rate INT;
