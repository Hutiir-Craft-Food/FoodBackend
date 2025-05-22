create or replace recursive view v_categories (
    parent_id,
    id,
    name,
    description,
    icon_url,
    keywords,
    text
) as
  -- root categories:
  select
    c.parent_id,
    c.id,
    c.name,
    c.description,
    c.icon_url,
    c.keywords::text,
    concat(clean(c.name), ',', coalesce(c.keywords, ''))::text as text
  from categories c
  where parent_id is null

  union all

  -- sub categories:
  select
    c.parent_id,
    c.id,
    c.name,
    c.description,
    c.icon_url,
    c.keywords::text,
    concat(v.text, ',', clean(c.name), ',', coalesce(c.keywords, ''))::text as text
  from v_categories v
  inner join categories c
    on c.parent_id = v.id;

drop view if exists v_categories cascade;
