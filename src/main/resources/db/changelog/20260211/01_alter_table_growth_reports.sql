--liquibase formatted sql

--changeset Gang:03_rename_growth_reports_to_routine_report:83
ALTER TABLE growth_reports
    RENAME TO routine_report;

--changeset Gang:04_add_journey_id_to_routine_report:84
ALTER TABLE routine_report
    ADD COLUMN journey_id BIGINT NOT NULL;

--changeset Gang:05_add_fk_routine_report_journey:85
ALTER TABLE routine_report
    ADD CONSTRAINT fk_routine_report_journey
        FOREIGN KEY (journey_id)
            REFERENCES journeys (journey_id)
            ON DELETE CASCADE;
