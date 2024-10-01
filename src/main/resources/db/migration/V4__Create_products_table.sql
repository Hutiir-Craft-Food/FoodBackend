CREATE TABLE IF NOT EXISTS products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    thumbnail_image VARCHAR(255),
    image VARCHAR(255),
    available BOOLEAN DEFAULT FALSE,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP(6),
    category_id BIGINT,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    seller_id BIGINT,
    FOREIGN KEY (seller_id) REFERENCES sellers(id)
);