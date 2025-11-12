CREATE TABLE IF NOT EXISTS categories (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    icon_url VARCHAR(255),
    keywords VARCHAR(255),
    parent_id BIGINT REFERENCES categories(id)
        ON DELETE RESTRICT
);

CREATE UNIQUE INDEX idx_categories_name_normalized
    ON categories (lower(trim(name)));

CREATE INDEX idx_categories_keywords_tsvector ON categories
    USING gin(to_tsvector('simple', coalesce(keywords, '')));

CREATE INDEX idx_categories_name_tsvector ON categories
    USING gin(to_tsvector('simple', name));

