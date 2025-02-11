CREATE TABLE IF NOT EXISTS products (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255),
    thumbnail_image VARCHAR(255),
    image VARCHAR(255),
    available BOOLEAN DEFAULT FALSE,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP(6),
    category_id BIGINT REFERENCES categories(id),
    seller_id BIGINT REFERENCES sellers(id)
);

CREATE INDEX idx_product_name_tsvector ON products USING GIN (to_tsvector('simple', name));
CREATE INDEX idx_product_description_tsvector ON products USING GIN (to_tsvector('simple', description));