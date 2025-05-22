create or replace view v_products as
  select
    c.id as category_id,
    c.name as category_name,
    p.id as product_id,
    p.name as product_name,
    p.thumbnail_image,
    p.available,
    c.keywords,
    to_tsvector('simple',
        trim(both ' ' from concat_ws(' ', clean(p.name), clean(c.keywords)))
        ) as "tsvector"
  from products p
  inner join v_categories c
    on c.id = p.category_id;
  -- TODO: join with sellers, and display seller_name
  -- where p.available = true -- TODO: confirm if we really need this filer

drop view if exists v_products;