CREATE TABLE IF NOT EXISTS sellers (
                         id SERIAL PRIMARY KEY,
                         company VARCHAR(255),
                         tax_code VARCHAR(255),
                         phone_number VARCHAR(255) NOT NULL UNIQUE,
                         user_id BIGINT,
                         FOREIGN KEY (user_id) REFERENCES users(id)
);