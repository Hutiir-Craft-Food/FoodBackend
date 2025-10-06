drop view if exists v_categories;

create recursive view v_categories (
    id,
    parent_id,
    name,
    path,
    icon_url,
    keywords,
    json_tree
) as
  -- root categories:
  select
    c.id,
    c.parent_id,
    c.name,
    concat('/', c.name) as path,
    c.icon_url,
    coalesce(c.keywords, clean(c.name)) as keywords,
    jsonb_build_object(
      'id', c.id,
      'name', c.name,
      'children', json_array()
    ) as json_tree
  from categories c
  where parent_id is null

  union all

  -- sub categories:
  select
    c.id,
    c.parent_id,
    c.name,
    concat(v.path, '/', c.name) as path,
    c.icon_url,
    concat_ws(',', v.keywords, coalesce(c.keywords, clean(c.name))) as keywords,
    jsonb_set(
      v.json_tree,
      '{children}', json_array(to_jsonb(jsonb_build_object(
        'id', c.id,
        'name', c.name,
        'children', json_array()
      )))
    ) as json_tree
  from v_categories v
  inner join categories c
    on c.parent_id = v.id;