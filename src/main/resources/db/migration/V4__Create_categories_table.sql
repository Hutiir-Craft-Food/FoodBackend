CREATE TABLE IF NOT EXISTS categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    icon_url VARCHAR(255),
    parent_id BIGINT REFERENCES categories(id)
);

CREATE INDEX idx_category_name_tsvector ON categories USING GIN (to_tsvector('simple', name));