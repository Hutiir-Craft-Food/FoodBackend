drop view if exists v_categories;

create recursive view v_categories (
    id,
    parent_id,
    name,
    path,
    path_names,
    path_ids,
    icon_url,
    keywords
) as
  -- root categories:
  select
    c.id,
    c.parent_id,
    c.name,
    concat('/', c.name) as path,
    cast(c.name as text) as path_names,
    cast(c.id as text) as path_ids,
    c.icon_url,
    coalesce(c.keywords, clean(c.name)) as keywords
  from categories c
  where parent_id is null

  union all

  -- sub categories:
  select
    c.id,
    c.parent_id,
    c.name,
    concat(v.path, '/', c.name) as path,
    cast(v.path_names || ',' || c.name as text) AS path_names,
    cast(v.path_ids || ',' || c.id::text as text) AS path_ids,
    c.icon_url,
    concat_ws(',', v.keywords, coalesce(c.keywords, clean(c.name))) as keywords
  from v_categories v
  inner join categories c
    on c.parent_id = v.id;