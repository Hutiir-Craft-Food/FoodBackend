create or replace recursive view v_categories (
    parent_id,
    id,
    name,
    path,
    icon_url,
    keywords
) as
  -- root categories:
  select
    c.parent_id,
    c.id,
    c.name,
    concat('/', c.name) as path,
    c.icon_url,
    coalesce(c.keywords, clean(c.name)) as keywords
  from categories c
  where parent_id is null

  union all

  -- sub categories:
  select
    c.parent_id,
    c.id,
    c.name,
    concat(v.path, '/', c.name) as path,
    c.icon_url,
    concat_ws(',', v.keywords, coalesce(c.keywords, clean(c.name))) as keywords
  from v_categories v
  inner join categories c
    on c.parent_id = v.id;
