CREATE SEQUENCE product_image_variants_seq START WITH 1 INCREMENT BY 4;

CREATE TABLE IF NOT EXISTS product_image_variants (
    id BIGINT PRIMARY KEY
        DEFAULT nextval('product_image_variants_seq'),
    image_id BIGINT NOT NULL,
    link TEXT NOT NULL,
    ts_size VARCHAR NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ
    );

CREATE INDEX idx_product_image_variants_image_id ON product_image_variants (image_id);
CREATE INDEX idx_product_image_variants_size ON product_image_variants (ts_size);

ALTER TABLE product_image_variants
    ADD CONSTRAINT fk_product_image_variants_image
        FOREIGN KEY (image_id) REFERENCES product_images (id)
            ON DELETE CASCADE;