create or replace function clean(str text) returns text as
$$
declare cleaned_str text;
begin
    -- trim and sanitize:
    cleaned_str := regexp_replace(trim(str), '[^[:alnum:]\s_\-]+', '', 'g');

    -- replace all sorts of apostrophes https://regex101.com/r/hLTYs6/3
    cleaned_str := regexp_replace(cleaned_str, '[\u0027\u02B9\u02BB\u02BC\u02BE\u02C8\u02EE\u0301\u0313\u0315\u055A\u05F3\u07F4\u07F5\u1FBF\u2018\u2019\u2032\uA78C\uFF07]', '', 'g');

    -- replace long whitespaces with single spaces:
    cleaned_str := regexp_replace(cleaned_str, '\s{2,}', ' ', 'g');

return cleaned_str;
end
$$ language plpgsql;