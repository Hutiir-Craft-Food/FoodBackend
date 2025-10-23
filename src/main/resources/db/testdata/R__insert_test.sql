-- Родительская категория
INSERT INTO categories (name, description, parent_id)
VALUES ('Алкоголь', 'Все для вас - Алкаши)', null);

-- Дочерние категории (используем последний полученный ID)
INSERT INTO categories (name, description, parent_id)
VALUES
    ('Пиво', 'Пивка для рывка', (select id from categories where name = 'Алкоголь')),
    ('Виски', 'Для людей с утонченным вкусом', (select id from categories where name = 'Алкоголь'));