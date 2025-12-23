INSERT INTO product_images (product_id, position)
SELECT
    p.id,
    1
FROM products p
LEFT JOIN sellers s on s.seller_name = 'Seller';
