CREATE TABLE delivery_methods (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    cost DECIMAL(10, 2) NOT NULL,
    estimated_delivery_time VARCHAR(50) NOT NULL,
    is_active BOOLEAN,
    delivery_provider VARCHAR(50) NOT NULL,
    seller_id BIGINT REFERENCES sellers(id) ON DELETE CASCADE,
    creation_date TIMESTAMP NOT NULL DEFAULT now()
);