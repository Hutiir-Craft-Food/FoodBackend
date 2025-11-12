truncate table categories cascade;

-- Top category
INSERT INTO categories (name, description, parent_id)
VALUES
      ('Алкоголь', 'Горілка, вино, пиво', null),
      ('М''ясо', 'Свіже м''ясо та м''ясні вироби', null),
      ('Молочні продукти', 'Молоко, сири, йогурти та інше', null);

  -- Subcategories under 'Алкоголь':
INSERT INTO categories (name, description, parent_id)
SELECT
    sub.name,
    sub.description,
    c.id
FROM (
         VALUES
             ('Пиво', 'Світле, темне,нефільтроване'),
             ('Віскі', 'Для людей з витонченим смаком'),
             ('Вино', 'Червоне, біле, рожеве вино')
     ) AS sub(name, description)
    JOIN  categories c ON c.name = 'Алкоголь' AND c.parent_id IS NULL;

    -- Subcategories under 'М'ясо':
INSERT INTO categories (name, description, parent_id)
SELECT
    sub.name,
    sub.description,
    c.id
FROM (
         VALUES
             ('Свинина', 'Свіжа свинина'),
             ('Яловичина', 'Висока якість яловичини'),
             ('Курятина', 'Куряче філе, стегна, крильця')
     ) AS sub(name, description)
        JOIN  categories c ON c.name = 'М''ясо' AND c.parent_id IS NULL;

    -- Subcategories under 'Молочні продукти':
INSERT INTO categories (name, description, parent_id)
SELECT
    sub.name,
    sub.description,
    c.id
FROM (
         VALUES
             ('Сири', 'Різні види сирів'),
             ('Молоко', 'Пастеризоване молоко'),
             ('Йогурти', 'Натуральні та фруктові йогурти')
     ) AS sub(name, description)
        JOIN  categories c ON c.name = 'Молочні продукти' AND c.parent_id IS NULL;