create or replace function search_suggestions(search_text TEXT)
returns table (
    pid BIGINT,
    pname TEXT,
    thumbnail_image TEXT,
    available BOOLEAN,
    cid BIGINT,
    cname TEXT,
    tsvector TSVECTOR,
    tsquery TSQUERY,
    similarity FLOAT
)
language sql
as
$$
with recursive catalogue as (
    select
        c.id,
        c.parent_id,
        c.name as self_name,
        c.keywords,
        concat(c.name, ',', coalesce(c.keywords, ''))::text as text
    from categories c
    where parent_id is null
    union all
    select
        c.id,
        c.parent_id,
        c.name as self_name,
        c.keywords,
        concat(ctg.text, ',', clean(c.name), ',', coalesce(c.keywords, ''))::text as text
    from catalogue ctg
    inner join categories c on c.parent_id = ctg.id
),
suggestions as (
    select
        p.id as pid,
        p.name as pname,
        p.thumbnail_image,
        p.available,
        c.id as cid,
        c.self_name as cname,
        to_tsvector('simple', concat(clean(p.name), ' ', regexp_replace(clean(p.name), '[ʼ''‘’]', '', 'g'), ' ', coalesce(c.text, ''))) as tsvector,
        case
            when length(search_text) < 3
                then to_tsquery('simple', search_text || ':*')
            else to_tsquery('simple', replace(clean(search_text),' ', '&') || ':*')
        end as tsquery,
        greatest(
            similarity(clean(p.name), clean(search_text)),
            similarity(c.text, clean(search_text))
        ) as similarity
    from products p
    inner join catalogue c on c.id = p.category_id
    where p.available = true
)
select *
from suggestions;
$$;