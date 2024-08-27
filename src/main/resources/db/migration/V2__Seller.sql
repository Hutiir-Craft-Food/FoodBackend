CREATE TABLE seller (
    id SERIAL PRIMARY KEY,
    company_name VARCHAR(100),
    seller_name VARCHAR(100),
    phone_number VARCHAR(13),
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    creation_date TIMESTAMP NOT NULL DEFAULT now()
    );