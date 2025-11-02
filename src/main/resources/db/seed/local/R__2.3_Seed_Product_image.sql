INSERT INTO product_images (id, product_id, uid, link, ts_size, position)
SELECT
    nextval('product_images_seq'),
    p.id,
    CONCAT ('photo-', TO_CHAR(NOW(), 'YYmmDD-'), 'image-', p.id) as uid,
    CASE
        -- Курка и продукты из курицы
        WHEN p.name IN ('Курка', 'Куряче філе', 'Фарш курячий', 'Копчена курка', 'Курка гриль')
            THEN 'https://images.unsplash.com/photo-1587336768918-ca17c127c2e5?w=500&auto=format&fit=crop&q=60'
        WHEN p.name IN ('Куряче серце', 'Куряча печінка')
            THEN 'https://images.unsplash.com/photo-1587593810167-9d91a1ac6f1c?w=500&auto=format&fit=crop&q=60'
        WHEN p.name IN ('Курячі стегна')
            THEN 'https://images.unsplash.com/photo-1604503468506-a8da13d82791?w=500&auto=format&fit=crop&q=60'
        WHEN p.name IN ('Курячі крильця')
            THEN 'https://images.unsplash.com/photo-1599483340001-96f5d07b5c5c?w=500&auto=format&fit=crop&q=60'
        -- Свинина
        WHEN p.name IN ('Свинина', 'Свиняча вирізка', 'Свиняча грудинка')
            THEN 'https://images.unsplash.com/photo-1592924357228-91a4daadcfea?w=500&auto=format&fit=crop&q=60'
        WHEN p.name IN ('Свиняча печінка')
            THEN 'https://images.unsplash.com/photo-1587593810167-9d91a1ac6f1c?w=500&auto=format&fit=crop&q=60'
        WHEN p.name IN ('Свинячі ребра')
            THEN 'https://images.unsplash.com/photo-1558036117-15e82a2c9a9a?w=500&auto=format&fit=crop&q=60'
        WHEN p.name = 'Бекон'
            THEN 'https://images.unsplash.com/photo-1574781338179-7d1b4e4adb2c?w=500&auto=format&fit=crop&q=60'
        WHEN p.name = 'Ковбаса'
            THEN 'https://images.unsplash.com/photo-1596818538476-1a856a8c8245?w=500&auto=format&fit=crop&q=60'
        WHEN p.name = 'Сало'
            THEN 'https://images.unsplash.com/photo-1587336768918-ca17c127c2e5?w=500&auto=format&fit=crop&q=60'
        -- Яловичина
        WHEN p.name IN ('Яловичина', 'Яловича вирізка', 'Яловичі ребра', 'Яловичий фарш', 'Стейк яловичий')
            THEN 'https://images.unsplash.com/photo-1558036117-15e82a2c9a9a?w=500&auto=format&fit=crop&q=60'
        WHEN p.name IN ('Яловича печінка', 'Яловичий язик')
            THEN 'https://images.unsplash.com/photo-1587593810167-9d91a1ac6f1c?w=500&auto=format&fit=crop&q=60'
        ELSE 'https://images.unsplash.com/photo-1587336768918-ca17c127c2e5?w=500&auto=format&fit=crop&q=60'
        END,
    sz.size,
    1
FROM products p
CROSS JOIN (VALUES
    ('THUMBNAIL'),
    ('SMALL'),
    ('MEDIUM'),
    ('LARGE')
) as sz (size)
LEFT JOIN sellers s on s.seller_name = 'Seller';
