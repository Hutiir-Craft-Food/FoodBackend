CREATE TABLE IF NOT EXISTS confirms(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    confirmation_token VARCHAR(6),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    expires_at TIMESTAMP NOT NULL,
    user_id BIGINT REFERENCES users(id)
);