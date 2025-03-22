# Підтримка **повнотекстового пошуку та векторного пошуку** у PostgreSQL.

---

## Вмикаємо, виконанням скриптів, розширення для триграми

```CREATE EXTENSION IF NOT EXISTS pg_trgm;```

## Після активації розширень можна створити GIN-індекси для повнотекстового пошуку та триграмний індекс для подібного пошуку.

### Індекс для триграмного пошуку (pg_trgm)

```
CREATE INDEX products_name_trgm_idx ON products USING GIN (name gin_trgm_ops);
```
✔ Цей індекс використовується для пошуку слів за частковим збігом

✔ Використовується для нечіткого пошуку (similarity())


### Індекс для повнотекстового пошуку (tsvector)

#### Для таблиці продуктів

```
CREATE INDEX idx_product_name_tsvector ON products
    USING GIN (to_tsvector('simple', name));
```

```
CREATE INDEX idx_products_category_id ON products (category_id);
```

#### Для таблиці категорій

```
CREATE INDEX idx_categories_keywords_tsvector
    ON categories USING GIN (to_tsvector('simple', keywords));
```

```
CREATE INDEX idx_categories_parent_id
    ON categories (parent_id);
```
✔ Ці індекси покращують пошук через to_tsquery()

✔ Оптимізують роботу пошукових запитів у PostgreSQL

## Keywords

### Cтворення адміністратором слів поліпшення охвату пошуку 

```
/v1/categories/keywords
```

___



## Виконання скриптів у DBeaver

1. Запустіть DBeaver та підключіться до бази даних PostgreSQL
2. Створіть новий SQL-скрипт: File → New → SQL Script
3. Скопіюйте та вставте SQL-код у редактор
4. Виконуємо скрипт, натиснувши Run у верхньому меню
5. Переконуємося, що скрипт виконався без помилок у вкладці “Query Manager”

## Перевірка індексів
```
SELECT indexname, indexdef FROM pg_indexes 
WHERE tablename = 'products';
```
✔ Якщо все пройшло успішно,  побачимо індекси у виводі.

## Перевірка роботи триграмного пошуку
```
SELECT name, similarity(name, 'запит') AS sim
FROM products
WHERE similarity(name, 'запит') > 0.3
ORDER BY sim DESC;
```
✔ Якщо результат є – триграмний індекс працює

## Перевірка роботи повнотекстового пошуку
```
SELECT name, ts_rank_cd(to_tsvector('simple', name), to_tsquery('запит:*')) AS rank
FROM products
WHERE to_tsvector('simple', name) @@ to_tsquery('запит:*')
ORDER BY rank DESC;
```
✔ Якщо є збіги – повнотекстовий пошук працює
