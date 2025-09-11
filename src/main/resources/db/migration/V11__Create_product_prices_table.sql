create table if not exists product_prices
(
    id         bigint generated always as identity primary key,
    price      numeric(10, 2) not null check ( price >= 0 ),
    qty        int                     default 0 not null check ( qty >= 0),
    created_at timestamptz    not null default now(),
    updated_at timestamptz    not null default now(),
    product_id bigint         not null references products (id) on delete cascade,
    unit_id    bigint         not null references product_units (id),
    unique (product_id, unit_id, qty)
);

create index idx_product_prices_product_id on product_prices (product_id);
create unique index uq_product_prices_unique on product_prices (product_id, unit_id, qty);
create index idx_product_prices_unit_id on product_prices (unit_id);