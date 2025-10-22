CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO users (email, password, role, enabled, confirmed)
VALUES
    ('seller@gmail.com', crypt('!sellerTop1', gen_salt('bf')), 'SELLER', true, true),
    ('buyer@gmail.com', crypt('!buyerTop1', gen_salt('bf')), 'BUYER', true, true);

INSERT INTO sellers (seller_name, user_id)
VALUES ('seller', (SELECT id from users where email = 'seller@gmail.com'));