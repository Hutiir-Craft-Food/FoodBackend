CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(40) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    enabled BOOLEAN DEFAULT FALSE,
    confirmed BOOLEAN,
    create_date TIMESTAMP NOT NULL DEFAULT now(),
    update_date TIMESTAMP
                   );

CREATE INDEX idx_email ON users (email);