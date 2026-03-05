CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO users (email, password, role, enabled, confirmed)
VALUES
    ('admin@example.com', crypt('admin', gen_salt('bf')), 'ADMIN', true, true),
    ('seller@example.com', crypt('!sellerTop1', gen_salt('bf')), 'SELLER', true, true),
    ('buyer@example.com', crypt('!buyerTop1', gen_salt('bf')), 'BUYER', true, true)
ON CONFLICT (email) DO NOTHING;

INSERT INTO sellers (seller_name, user_id)
SELECT 'Seller' as seller_name, id as user_id
FROM users
WHERE email = 'seller@example.com'
AND NOT EXISTS (
    SELECT 1
    FROM sellers
    WHERE user_id = users.id
);
-- the below cant be used.
-- ON CONFLICT (user_id) DO NOTHING;
-- coz it requires user_id to be unique
-- but we might want to allow multiple Seller profiles
-- under the same user accounts