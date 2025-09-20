create table if not exists product_units
(
    id         bigint generated always as identity primary key,
    name       varchar(10) not null unique,
    created_at timestamptz not null default now(),
    updated_at timestamptz
);

create unique index if not exists product_units_name_index on product_units (name);