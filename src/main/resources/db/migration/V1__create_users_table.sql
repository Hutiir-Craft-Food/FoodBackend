CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(30) UNIQUE NOT NULL,
    password VARCHAR(40) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    enabled BOOLEAN DEFAULT TRUE,
    confirmationToken VARCHAR(100),
    creation_date TIMESTAMP NOT NULL
);

CREATE INDEX idx_email ON users (email);

ALTER TABLE users ALTER COLUMN creation_date SET DEFAULT now();
