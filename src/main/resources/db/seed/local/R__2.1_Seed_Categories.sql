truncate table categories cascade;

-- Top category
INSERT INTO categories (name, slug, description, parent_id)
VALUES
      ('Алкоголь', 'алкоголь', 'Горілка, вино, пиво', null),
      ('М''ясо', 'мясо', 'Свіже м''ясо та м''ясні вироби', null),
      ('Молочні продукти', 'молочні-продукти', 'Молоко, сири, йогурти та інше', null);

  -- Subcategories under 'Алкоголь':
INSERT INTO categories (name, slug, description, parent_id)
SELECT
    sub.name,
    sub.slug,
    sub.description,
    c.id
FROM (
         VALUES
             ('Пиво', 'пиво', 'Світле, темне,нефільтроване'),
             ('Віскі', 'віскі', 'Для людей з витонченим смаком'),
             ('Вино', 'вино', 'Червоне, біле, рожеве вино')
     ) AS sub(name, slug, description)
    JOIN  categories c ON c.name = 'Алкоголь' AND c.parent_id IS NULL;

    -- Subcategories under 'М'ясо':
INSERT INTO categories (name, slug, description, parent_id)
SELECT
    sub.name,
    sub.slug,
    sub.description,
    c.id
FROM (
         VALUES
             ('Свинина', 'свинина', 'Свіжа свинина'),
             ('Яловичина', 'яловичина', 'Висока якість яловичини'),
             ('Курятина', 'курятина', 'Куряче філе, стегна, крильця')
     ) AS sub(name, slug, description)
        JOIN  categories c ON c.name = 'М''ясо' AND c.parent_id IS NULL;

    -- Subcategories under 'Молочні продукти':
INSERT INTO categories (name, slug, description, parent_id)
SELECT
    sub.name,
    sub.slug,
    sub.description,
    c.id
FROM (
         VALUES
             ('Сири', 'сири', 'Різні види сирів'),
             ('Молоко', 'молоко', 'Пастеризоване молоко'),
             ('Йогурти', 'йогурти', 'Натуральні та фруктові йогурти')
     ) AS sub(name, slug, description)
        JOIN  categories c ON c.name = 'Молочні продукти' AND c.parent_id IS NULL;

UPDATE categories
SET keywords = name
WHERE keywords IS NULL OR trim(keywords) = '';