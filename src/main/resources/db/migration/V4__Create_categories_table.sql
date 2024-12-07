CREATE TABLE IF NOT EXISTS categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    icon_url VARCHAR(255),
    parent_id BIGINT REFERENCES categories(id),
    create_date TIMESTAMP NOT NULL DEFAULT now(),
    update_date TIMESTAMP
);