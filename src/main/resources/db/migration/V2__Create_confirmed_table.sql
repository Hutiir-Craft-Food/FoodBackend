CREATE TABLE confirm(
    id SERIAL PRIMARY KEY,
    confirmation_token VARCHAR(10),
        created_at TIMESTAMP NOT NULL DEFAULT now(),
        expires_at TIMESTAMP
);