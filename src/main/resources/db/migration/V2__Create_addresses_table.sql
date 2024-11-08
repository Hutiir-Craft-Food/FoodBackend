CREATE TABLE addresses (
    id SERIAL PRIMARY KEY,
    country VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    street VARCHAR(100) NOT NULL,
    house_number VARCHAR(100),
    apartment_number VARCHAR(100),
    postal_code VARCHAR(20) NOT NULL,
    creation_date TIMESTAMP NOT NULL DEFAULT now()
);