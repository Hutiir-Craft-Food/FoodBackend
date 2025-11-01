CREATE EXTENSION IF NOT EXISTS pgcrypto;

DO $$
DECLARE
letters TEXT := 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
    digits TEXT := '0123456789';
    specials TEXT := '!@#$%^&*()_+-=';
    all_chars TEXT := letters || lower(letters) || digits || specials;
    generated_pass TEXT := '';

BEGIN
    -- Добавляем гарантированные типы символов
    generated_pass :=
        substr(letters, floor(random() * length(letters) + 1)::int, 1) ||
        substr(digits, floor(random() * length(digits) + 1)::int, 1) ||
        substr(specials, floor(random() * length(specials) + 1)::int, 1);

    -- Добавляем ещё случайные символы для длины (12–14)
FOR i IN 1..(9 + floor(random() * 3)) LOOP
        generated_pass := generated_pass || substr(all_chars, floor(random() * length(all_chars) + 1)::int, 1);
END LOOP;

    -- Перемешиваем символы
    generated_pass := (
        SELECT string_agg(ch, '')
        FROM unnest(string_to_array(generated_pass, NULL)) ch
        ORDER BY random()
    );

    IF NOT EXISTS (SELECT 1 FROM users WHERE email = '${email}') THEN
        INSERT INTO users (email, password, role, enabled, confirmed)
        VALUES (
            '${email}',
            crypt(generated_pass, gen_salt('bf')),
            '${role}',
            true,
            true
        );

        RAISE NOTICE '==============================================================';
        RAISE NOTICE '✅ Admin account created successfully!';
        RAISE NOTICE '   Email:    %', '${email}';
        RAISE NOTICE '   Password: %', generated_pass;
        RAISE NOTICE '   Role:     %', '${role}';
        RAISE NOTICE '==============================================================';
ELSE
        RAISE NOTICE 'ℹ️ Admin with email % already exists — skipping creation.', '${email}';
END IF;
END $$;