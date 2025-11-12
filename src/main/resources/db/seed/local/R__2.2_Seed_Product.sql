-- Курка
INSERT INTO products (name, available, description, category_id, seller_id)
SELECT
    prod.name,
    prod.available,
    prod.description,
    c.id,
    s.id
FROM (
     VALUES
         ('Куряча тушка', true, 'Свіжа курка, фермерська'),
         ('Куряче серце', true, 'Курячі серця свіжі'),
         ('Куряча печінка', true, 'Куряча печінка свіжа'),
         ('Курячі стегна', true, 'Стегна курячі свіжі'),
         ('Курячі крильця', true, 'Крильця курячі свіжі'),
         ('Куряче філе', true, 'Філе куряче без шкіри'),
         ('Фарш курячий', true, 'Фарш з курячого м''яса'),
         ('Копчена курка', true, 'Курка копчена'),
         ('Курка гриль', true, 'Курка-гриль готова')
     ) AS prod (name, available, description)
        JOIN categories c ON c.name = 'Курятина' AND c.parent_id IS NOT NULL
        JOIN sellers s ON s.seller_name = 'Seller';

-- Свинина
INSERT INTO products (name, available, description, category_id, seller_id)
SELECT
    prod.name,
    prod.available,
    prod.description,
    c.id,
    s.id
FROM (
         VALUES
             ('Свинина', true, 'Свіжа свинина'),
             ('Свиняча вирізка', true, 'Свиняча вирізка свіжа'),
             ('Свинячі ребра', true, 'Свинячі ребра для грилю'),
             ('Свиняча грудинка', true, 'Свиняча грудинка свіжа'),
             ('Свиняча печінка', true, 'Свиняча печінка свіжа'),
             ('Бекон', true, 'Бекон якісний'),
             ('Ковбаса', true, 'Різні види ковбас'),
             ('Сало', true, 'Сало солоне')
     ) AS prod (name, available, description)
         JOIN  categories c ON c.name = 'Свинина' AND c.parent_id IS NOT NULL
         JOIN sellers s ON s.seller_name = 'Seller' ;

-- Яловичина
INSERT INTO products (name, available, description, category_id, seller_id)
SELECT
    prod.name,
    prod.available,
    prod.description,
    c.id,
    s.id
FROM (
         VALUES
             ('Яловичина', true, 'Яловичина свіжа'),
             ('Яловича вирізка', true, 'Яловича вирізка преміум'),
             ('Яловичі ребра', true, 'Яловичі ребра для тушкування'),
             ('Яловичий фарш', true, 'Фарш яловичий свіжий'),
             ('Яловича печінка', true, 'Яловича печінка свіжа'),
             ('Яловичий язик', true, 'Яловичий язик для варіння'),
             ('Стейк яловичий', true, 'Стейки з яловичини')
     ) AS prod (name, available, description)
         JOIN  categories c ON c.name = 'Яловичина' AND c.parent_id IS NOT NULL
         JOIN sellers s ON s.seller_name = 'Seller' ;
