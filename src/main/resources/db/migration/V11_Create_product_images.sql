CREATE TABLE product_images (
    id BIGINT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    uid VARCHAR(255) NOT NULL,
    link TEXT NOT NULL,
    ts_size VARCHAR(30),
    position INTEGER NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE
);

CREATE SEQUENCE product_images_seq START WITH 1 INCREMENT BY 20;

CREATE INDEX idx_product_images_product_id ON product_images (product_id);
CREATE INDEX idx_product_images_uid ON product_images (product_id, uid);
CREATE INDEX idx_product_images_position ON product_images (product_id, position);

ALTER TABLE product_images
    ADD CONSTRAINT fk_product_images_product
        FOREIGN KEY (product_id) REFERENCES product (id);