CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO users (email, password, role, enabled, confirmed)
VALUES ('${email}', crypt('${password}', gen_salt('bf')), '${role}', ${enabled}, ${confirmed}),
       ('seller@gmail.com', crypt('!sellerTop1', gen_salt('bf')), 'SELLER', true, true),
       ('buyer@gmail.com', crypt('!buyerTop1', gen_salt('bf')), 'BUYER', true, true);

INSERT INTO categories (name, description, parent_id)
VALUES ('Алкоголь', 'Все для вас - Алкаши)', null),
       ('Пиво', 'Пивка для рывка', 1),
       ('Виски', 'Для людей с утонченным вкусом', 1);

INSERT INTO products (name, available, description, seller_id, category_id)
VALUES  ('Рыгань', true, 'Почти за дарма. На каждое утро', 2, 2 ),
        ('Оболонь', true, 'Пиво светлое, как вода. Попробуй найти хоть какойто оттенок жолтого', 2, 2 ),
        ('Шотландский Виски', true, 'Для команды разработчиков ХутирКрафт', 2, 3);