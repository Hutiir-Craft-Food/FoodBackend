CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO users (email, password, role, enabled, confirmed)
VALUES ('${email}', crypt('${password}', gen_salt('bf')), '${role}', ${enabled}, 'true');