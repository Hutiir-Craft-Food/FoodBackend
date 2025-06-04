CREATE TABLE IF NOT EXISTS categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    icon_url VARCHAR(255),
    keywords TEXT,
    parent_id BIGINT REFERENCES categories(id)
        ON DELETE RESTRICT
);

CREATE INDEX idx_categories_keywords_tsvector
    ON categories
        USING gin(to_tsvector('simple', coalesce(keywords, '')));

CREATE INDEX idx_categories_name_tsvector ON categories
  USING gin(to_tsvector('simple', name));