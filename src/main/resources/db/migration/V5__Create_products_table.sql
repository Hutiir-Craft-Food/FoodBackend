CREATE TABLE IF NOT EXISTS products (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    thumbnail_image VARCHAR(255),
    image VARCHAR(255),
    available BOOLEAN DEFAULT FALSE,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP(6),
    category_id BIGINT REFERENCES categories(id) ON DELETE SET NULL,
    seller_id BIGINT REFERENCES sellers(id) ON DELETE CASCADE
);

CREATE INDEX idx_product_name_tsvector ON products
    USING GIN (to_tsvector('simple', name));

CREATE INDEX idx_products_category_id ON products (category_id);
