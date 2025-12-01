insert into product_prices (price, qty, product_id, unit_id)
select
  cast(RANDOM() * 150 + 100 as NUMERIC(10,2)) as price,
  1 as qty,
  p.id as product_id,
  pu.id as unit_id
from products p
inner join categories c
  on c.id = p.category_id
join product_units pu on pu."name" = 'кг.'
where c."name" = 'Курятина'
  and not exists (
        select 1 from product_prices pp
        where pp.product_id = p.id
          and pp.unit_id = pu.id
    );

insert into product_prices (price, qty, product_id, unit_id)
select
    cast(RANDOM() * 300 + 200 as NUMERIC(10,2)) as price,
    1 as qty,
    p.id as product_id,
    pu.id as unit_id
from products p
         inner join categories c
                    on c.id = p.category_id
         join product_units pu on pu."name" = 'кг.'
where c."name" = 'Свинина'
  and not exists (
        select 1 from product_prices pp
        where pp.product_id = p.id
          and pp.unit_id = pu.id
    );

insert into product_prices (price, qty, product_id, unit_id)
select
    cast(RANDOM() * 500 + 300 as NUMERIC(10,2)) as price,
    1 as qty,
    p.id as product_id,
    pu.id as unit_id
from products p
         inner join categories c
                    on c.id = p.category_id
         join product_units pu on pu."name" = 'кг.'
where c."name" = 'Яловичина'and not exists (
        select 1 from product_prices pp
        where pp.product_id = p.id
          and pp.unit_id = pu.id
    );