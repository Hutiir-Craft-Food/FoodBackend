CREATE TABLE IF NOT EXISTS product_images (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_id BIGINT NOT NULL,
    position INTEGER NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ
);

CREATE INDEX idx_product_images_product_id ON product_images (product_id);
CREATE UNIQUE INDEX idx_product_images_position ON product_images (product_id, position);

ALTER TABLE product_images
    ADD CONSTRAINT fk_product_images_product
        FOREIGN KEY (product_id) REFERENCES products (id);