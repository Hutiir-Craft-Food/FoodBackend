ALTER TABLE products
    ADD column article VARCHAR(32);

CREATE UNIQUE INDEX uq_products_article
    ON products (article);

UPDATE products
SET article =
    lpad(seller_id::text, 4, '0') ||
    lpad(id::text, 6, '0')
WHERE article IS NULL
  AND seller_id IS NOT NULL;