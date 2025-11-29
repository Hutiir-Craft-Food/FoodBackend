INSERT INTO product_images (id, product_id, position)
SELECT
    nextval('product_images_seq'),
    p.id,
    1
FROM products p
LEFT JOIN sellers s on s.seller_name = 'Seller';
