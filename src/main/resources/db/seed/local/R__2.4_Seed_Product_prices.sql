insert into product_prices (price, qty, product_id, unit_id)
select
  cast(RANDOM() * 150 + 100 as NUMERIC(10,2)) as price,
  1 as qty,
  p.id as product_id,
  pu.id as unit_id
from products p
inner join categories c
  on c.id = p.category_id
cross join product_units pu
where c."name" like 'Курка'
and pu."name" = 'кг.'