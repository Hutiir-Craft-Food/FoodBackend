CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO users (email, password, role, enabled, confirmed)
VALUES ('${flyway:email}', crypt('${flyway:password}', gen_salt('bf')), 'ADMIN', ${flyway:enabled}, ${flyway:confirmed});

-- values for email, password, enabled, and confirmed should be provided as Flyway placeholders during migration
-- https://documentation.red-gate.com/fd/migration-placeholders-275218550.html
-- spring-boot picks up these placeholders from application.properties or environment variables
