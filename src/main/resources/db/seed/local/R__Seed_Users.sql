CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Users:
  truncate table users cascade;

  -- admin:
  INSERT INTO users (email, password, role, enabled, confirmed)
  SELECT 'admin@example.com', crypt('admin', gen_salt('bf')), 'ADMIN', true, true
  WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@example.com');

  -- seller & buyer:
  INSERT INTO users (email, password, role, enabled, confirmed)
  VALUES
      ('seller@example.com', crypt('!sellerTop1', gen_salt('bf')), 'SELLER', true, true),
      ('buyer@example.com', crypt('!buyerTop1', gen_salt('bf')), 'BUYER', true, true);

  -- Seller's profile:
  INSERT INTO sellers (seller_name, user_id)
  SELECT 'Seller' as seller_name, id as user_id
  FROM users WHERE email = 'seller@example.com';