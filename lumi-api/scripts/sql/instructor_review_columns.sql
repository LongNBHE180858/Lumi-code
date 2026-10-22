-- Run against the target database after Liquibase has created user_profile.
-- Re-running this script is safe when the columns already exist.
ALTER TABLE public.user_profile
    ADD COLUMN IF NOT EXISTS instructor_rejection_reason varchar(2000),
    ADD COLUMN IF NOT EXISTS instructor_reviewed_at timestamp,
    ADD COLUMN IF NOT EXISTS instructor_reviewed_by varchar(50);
