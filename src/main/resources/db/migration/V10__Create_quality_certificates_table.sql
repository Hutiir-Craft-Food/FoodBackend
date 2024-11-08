CREATE TABLE quality_certificates (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    certificate_url VARCHAR(255),
    issue_date TIMESTAMP NOT NULL,
    expiration_date TIMESTAMP NOT NULL,
    creation_date TIMESTAMP NOT NULL DEFAULT now(),

    seller_id BIGINT REFERENCES sellers(id) ON DELETE CASCADE
);