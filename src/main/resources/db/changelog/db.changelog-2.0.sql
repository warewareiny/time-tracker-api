--liquibase formatted sql

--changeset tatyana-zagaynova:001-add-user_id-to-time_entry
ALTER TABLE time_entry
ADD COLUMN user_id INT REFERENCES users(id);

--changeset tatyana-zagaynova:002-replace-duration-data-type
ALTER TABLE time_entry
ALTER COLUMN duration_minutes TYPE BIGINT;

--changeset tatyana-zagaynova:003-remove-duration-not-null-constraint
ALTER TABLE time_entry
ALTER COLUMN duration_minutes DROP NOT NULL;