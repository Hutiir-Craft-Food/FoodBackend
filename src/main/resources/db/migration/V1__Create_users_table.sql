CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(40) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    enabled BOOLEAN DEFAULT FALSE,
    confirmation_token VARCHAR(255),
    creation_date TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_email ON users (email);