create table if not exists cart_items
(
    id              bigint generated always as identity primary key,
    user_id         bigint      not null references users (id) on delete cascade,
    product_price_id bigint      not null references product_prices (id),
    quantity        int         not null check (quantity between 1 and 100000),
    created_at      timestamptz not null default now(),
    updated_at      timestamptz,
    constraint uq_cart_items_user_product_price unique (user_id, product_price_id)
);
