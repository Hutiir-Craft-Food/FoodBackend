CREATE TABLE IF NOT EXISTS products (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255),
    thumbnail_image VARCHAR(255),
    image VARCHAR(255),
    available BOOLEAN DEFAULT FALSE,
    description VARCHAR(255),
    create_date TIMESTAMP NOT NULL DEFAULT now(),
    update_date TIMESTAMP,

    category_id BIGINT REFERENCES categories(id),
    seller_id BIGINT REFERENCES sellers(id)
);