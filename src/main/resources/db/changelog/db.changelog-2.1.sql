--liquibase formatted sql

--changeset tatyana-zagaynova:001-remove-IN-PROGRESS-status
UPDATE task
SET status = 'TODO'
WHERE status = 'IN_PROGRESS';

ALTER TABLE task
DROP CONSTRAINT IF EXISTS task_status_check;

ALTER TABLE task
ADD CONSTRAINT task_status_check
CHECK (status IN ('TODO', 'DONE'));