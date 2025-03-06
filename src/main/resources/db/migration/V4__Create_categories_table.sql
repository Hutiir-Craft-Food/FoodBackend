CREATE TABLE IF NOT EXISTS categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    icon_url VARCHAR(255),
    keywords TEXT,
    parent_id BIGINT REFERENCES categories(id)
        ON DELETE RESTRICT
);

CREATE INDEX idx_categories_parent_id
    ON categories (parent_id);

CREATE INDEX idx_categories_keywords_tsvector
    ON categories USING GIN (to_tsvector('simple', keywords));