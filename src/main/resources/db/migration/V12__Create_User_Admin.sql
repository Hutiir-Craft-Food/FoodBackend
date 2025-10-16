CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Пользователи
INSERT INTO users (email, password, role, enabled, confirmed)
VALUES
    ('${email}', crypt('${password}', gen_salt('bf')), '${role}', ${enabled}, ${confirmed}),
    ('seller@gmail.com', crypt('!sellerTop1', gen_salt('bf')), 'SELLER', true, true),
    ('buyer@gmail.com', crypt('!buyerTop1', gen_salt('bf')), 'BUYER', true, true);

INSERT INTO sellers (seller_name, user_id)
VALUES ('seller', (SELECT id from users where email = 'seller@gmail.com'));

-- Родительская категория
INSERT INTO categories (name, description, parent_id)
VALUES ('Алкоголь', 'Все для вас - Алкаши)', null);

-- Дочерние категории (используем последний полученный ID)
INSERT INTO categories (name, description, parent_id)
VALUES
    ('Пиво', 'Пивка для рывка', currval(pg_get_serial_sequence('categories', 'id'))),
    ('Виски', 'Для людей с утонченным вкусом', currval(pg_get_serial_sequence('categories', 'id')));

-- Продукты
INSERT INTO products (name, available, description, seller_id, category_id)
VALUES
    ('Рыгань', true, 'Почти за дарма. На каждое утро',
     (SELECT id FROM sellers where seller_name = 'seller'),
     (SELECT id FROM categories WHERE name = 'Пиво')),
    ('Оболонь', true, 'Пиво светлое, как вода. Попробуй найти хоть какой-то оттенок желтого',
     (SELECT id FROM sellers where seller_name = 'seller'),
     (SELECT id FROM categories WHERE name = 'Пиво')),
    ('Шотландский Виски', true, 'Для команды разработчиков ХутирКрафт',
     (SELECT id FROM sellers where seller_name = 'seller'),
     (SELECT id FROM categories WHERE name = 'Виски'));