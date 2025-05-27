create or replace recursive view v_categories (
    parent_id,
    id,
    name,
    description,
    icon_url,
    keywords
) as
  -- root categories:
  select
    c.parent_id,
    c.id,
    c.name,
    c.description,
    c.icon_url,
    concat(clean(c.name), ',', coalesce(c.keywords, ''))::text as "keywords"
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
    concat(v.keywords, ',', clean(c.name), ',', coalesce(c.keywords, ''))::text as "keywords"
  from v_categories v
  inner join categories c
    on c.parent_id = v.id;

drop view if exists v_categories cascade;

select * from v_categories;

SELECT
    dependent_ns.nspname AS dependent_schema,
    dependent_view.relname AS dependent_view,
    source_ns.nspname AS source_schema,
    source_table.relname AS source_table
FROM pg_rewrite
         JOIN pg_class AS dependent_view ON pg_rewrite.ev_class = dependent_view.oid
         JOIN pg_depend ON pg_depend.objid = pg_rewrite.oid
         JOIN pg_class AS source_table ON pg_depend.refobjid = source_table.oid
         JOIN pg_namespace source_ns ON source_ns.oid = source_table.relnamespace
         JOIN pg_namespace dependent_ns ON dependent_ns.oid = dependent_view.relnamespace
WHERE source_table.relname IN ('v_categories', 'v_products');

SELECT table_schema, table_name
FROM information_schema.views
WHERE table_name = 'v_categories';

SELECT definition
FROM pg_views
WHERE viewname = 'v_categories';

SELECT relname, relkind
FROM pg_class
WHERE relname = 'v_categories';
--     	'r' — таблиця,
-- 		'v' — звичайна view,
-- 		'm' — materialized view,
-- 		'i' — індекс,
-- 		'S' — sequence,
-- 		'f' — foreign table,
-- 		'c' — composite type.
