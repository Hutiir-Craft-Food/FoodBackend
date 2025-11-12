CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO users (email, password, role, enabled, confirmed)
VALUES ('${email}', crypt('${password}', gen_salt('bf')), 'ADMIN', ${enabled}, ${confirmed});

-- values for email, password, enabled, and confirmed should be provided as Flyway placeholders during migration
-- https://documentation.red-gate.com/fd/migration-placeholders-275218550.html
-- spring-boot picks up these placeholders from application.properties or environment variables
