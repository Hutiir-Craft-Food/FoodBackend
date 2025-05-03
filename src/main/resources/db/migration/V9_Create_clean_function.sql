drop function if exists clean(varchar);
create or replace function clean(str varchar) returns varchar as
$$
declare cleaned_str varchar;
begin
    -- assign the initial value
    cleaned_str := str;

    -- replace apostrophe with modifier letter apostrophe:
    cleaned_str := replace(cleaned_str, '''', 'ʼ');

    -- trim and sanitize:
    cleaned_str := regexp_replace(trim(cleaned_str), '[^[:alnum:]\sʼ.,_-]+', '', 'g');

    -- replace long whitespaces with single spaces:
    cleaned_str := regexp_replace(cleaned_str, '\s{2,}', ' ', 'g');

    return cleaned_str;
end
$$ language plpgsql;

