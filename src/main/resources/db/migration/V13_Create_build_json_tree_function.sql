create function build_json_tree (category_id bigint) returns jsonb as
$$
declare result jsonb;
begin
  with recursive tree as (
    select
      c.parent_id,
      c.id,
      c.name,
      jsonb_build_object(
        'id', c.id,
        'name', c.name,
        'children', '[]'::jsonb
      ) as json_tree
    from categories c
    where c.id = category_id

    union all

    -- climb upward, wrapping children into parents
    select
      c.parent_id,
      c.id,
      c.name,
      jsonb_build_object(
        'id', c.id,
        'name', c.name,
        'children', jsonb_build_array(t.json_tree)
      )
    from tree t
    inner join categories c
      on c.id = t.parent_id
  )
  select json_tree
  into result
  from tree
  where parent_id is null ;

  return result;
end
$$ language plpgsql;