CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO users (email, password, role, enabled)
VALUES ('${email}', crypt('${password}', gen_salt('bf')), '${role}', ${enabled});