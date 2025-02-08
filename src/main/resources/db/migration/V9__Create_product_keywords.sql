CREATE TABLE IF NOT EXISTS product_keywords (
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    keyword VARCHAR(255) COLLATE "uk_UA.UTF-8" NOT NULL,
    PRIMARY KEY (product_id, keyword)
);