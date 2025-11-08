CREATE TABLE IF NOT EXISTS product_images (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_id BIGINT NOT NULL,
    uid VARCHAR(255) NOT NULL,
    link TEXT NOT NULL,
    ts_size VARCHAR(30),
    position INTEGER NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ
);

CREATE SEQUENCE product_images_seq START WITH 1 INCREMENT BY 20;

CREATE INDEX idx_product_images_product_id ON product_images (product_id);
CREATE INDEX idx_product_images_uid ON product_images (product_id, uid);
CREATE INDEX idx_product_images_position ON product_images (product_id, position);

ALTER TABLE product_images
    ADD CONSTRAINT fk_product_images_product
        FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE product_images
    ADD CONSTRAINT product_images_ts_size_check
        CHECK (
               ((ts_size)::text = ANY (
                 (ARRAY[
                   'THUMBNAIL'::character varying,
                   'SMALL'::character varying,
                   'MEDIUM'::character varying,
                   'LARGE'::character varying
                 ])::text[])
               ) OR (ts_size IS NULL)
        );