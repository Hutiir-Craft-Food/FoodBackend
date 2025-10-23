CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- INSERT INTO users (email, password, role, enabled, confirmed)
-- VALUES ('${email}', crypt('${password}', gen_salt('bf')), '${role}', ${enabled}, ${confirmed});

INSERT INTO users (email, password, role, enabled, confirmed)
VALUES ('admin', crypt('admin', gen_salt('bf')), 'ADMIN', true, true);