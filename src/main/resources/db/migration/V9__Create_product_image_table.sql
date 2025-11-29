CREATE TABLE IF NOT EXISTS product_images (
    id BIGINT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    position INTEGER NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ
);

CREATE SEQUENCE product_images_seq START WITH 1 INCREMENT BY 5;

CREATE INDEX idx_product_images_product_id ON product_images (product_id);
CREATE INDEX idx_product_images_position ON product_images (product_id, position);

ALTER TABLE product_images
    ADD CONSTRAINT fk_product_images_product
        FOREIGN KEY (product_id) REFERENCES products (id);