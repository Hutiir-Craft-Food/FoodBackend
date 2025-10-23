CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO users (email, password, role, enabled, confirmed)
VALUES
    ('${email}', crypt('${password}', gen_salt('bf')), '${role}', ${enabled}, ${confirmed});
    -- TODO: what's gonna happen env variables are not set?
    --  can we check that somehow at migration time?
    --  can we fail the migration if they are not set?