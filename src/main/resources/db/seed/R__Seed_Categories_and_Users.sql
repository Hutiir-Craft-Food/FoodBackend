truncate table products cascade;
truncate table categories cascade;

-- Top category
INSERT INTO categories (name, description, parent_id)
VALUES ('Алкоголь', 'Усе для вас — любителі випивки', null);

  -- Subcategories under 'Алкоголь':
INSERT INTO categories (name, description, parent_id)
  SELECT subcategories.name, subcategories.description, categories.id
  FROM (
VALUES
      ('Пиво', 'Пивко для ривка'),
      ('Віскі', 'Для людей з витонченим смаком')
  ) subcategories(name, description)
  FULL OUTER JOIN categories ON 1=1
  WHERE categories.name = 'Алкоголь';

  -- Products from Seller:
INSERT INTO products (name, available, description, seller_id, category_id)
VALUES
    ('Ригань', true, 'Майже даром. На кожен ранок',
     (SELECT id FROM sellers where seller_name = 'seller'),
      (SELECT id FROM categories WHERE name = 'Пиво')
    ),

    ('Оболонь', true, 'Пиво світле, як вода. Спробуй знайти хоча б якийсь відтінок жовтого',
      (SELECT id FROM sellers where seller_name = 'Seller'),
      (SELECT id FROM categories WHERE name = 'Пиво')
    ),

    ('Шотландський віскі Jura', true, 'Для команди розробників ХутірКрафт',
      (SELECT id FROM sellers where seller_name = 'Seller'),
      (SELECT id FROM categories WHERE name = 'Віскі')
    );