CREATE TABLE sellers (
    id SERIAL PRIMARY KEY,
    company_name VARCHAR(100),
    seller_name VARCHAR(100),
    phone_number VARCHAR(13) NOT NULL UNIQUE,
    customer_phone_number VARCHAR(13) NOT NULL UNIQUE,
    logo VARCHAR,
    description VARCHAR,
    create_date TIMESTAMP NOT NULL DEFAULT now(),
    update_date TIMESTAMP,

    user_id BIGINT REFERENCES users(id),
    address_id BIGINT REFERENCES addresses(id) ON DELETE SET NULL
    );