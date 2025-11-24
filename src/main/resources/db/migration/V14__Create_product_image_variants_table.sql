CREATE TABLE IF NOT EXISTS product_image_variants (
    id BIGINT PRIMARY KEY,
    image_id BIGINT NOT NULL,
    link TEXT NOT NULL,
    ts_size VARCHAR NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ
    );

CREATE SEQUENCE product_image_variants_seq START WITH 1 INCREMENT BY 20;

CREATE INDEX idx_product_image_variants_image_id ON product_image_variants (image_id);
CREATE INDEX idx_product_image_variants_size ON product_image_variants (ts_size);

ALTER TABLE product_image_variants
    ADD CONSTRAINT fk_product_image_variants_image
        FOREIGN KEY (image_id) REFERENCES product_imagess (id)
            ON DELETE CASCADE;