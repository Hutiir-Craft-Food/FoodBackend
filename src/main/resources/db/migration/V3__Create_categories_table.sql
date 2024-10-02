CREATE TABLE IF NOT EXISTS categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    icon_url VARCHAR(255),
    CONSTRAINT fk_parent_category,
    parent_id BIGINT REFERENCES categories(id)
);