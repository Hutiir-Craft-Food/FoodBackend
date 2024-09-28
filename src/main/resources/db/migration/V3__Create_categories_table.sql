CREATE TABLE IF NOT EXISTS categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    icon_url VARCHAR(255),
    parent_id BIGINT,
    CONSTRAINT fk_parent_category
    FOREIGN KEY (parent_id) REFERENCES categories(id)
);