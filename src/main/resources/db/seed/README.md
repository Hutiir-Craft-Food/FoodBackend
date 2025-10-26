# Environment-Based Seeds

This directory contains seed files that are specific to different environments. 

### Directory structure:

```
db/migration/
db/seed/dev/
db/seed/prod/
```

### Flyway configuration in application.yml:

```yaml
spring:
    flyway:
      locations: classpath:db/migration,classpath:db/seed,classpath:db/seed/${spring.profiles.active}
```
data seeding scripts will be executed after the migration scripts, 
allowing you to populate your database with environment-specific data.
SQL scripts must be idempotent to avoid issues when running multiple times.

If you need to run data seeding script(s) multiple times, during each flyway migration 
add comment `-- ${flyway:timestamp}` to the script, 
as suggested in [Repeatable migrations](https://documentation.red-gate.com/fd/repeatable-migrations-273973335.html).

## References:
- 
- [Repeatable migrations](https://documentation.red-gate.com/fd/repeatable-migrations-273973335.html)
- [Tutorial - Use different data migration locations for your different targets](https://documentation.red-gate.com/fd/tutorial-use-different-data-migration-locations-for-your-different-targets-279085161.html)
- [Tutorial - Use repeatable migrations to manage data](https://documentation.red-gate.com/fd/tutorial-use-repeatable-migrations-to-manage-data-190578952.html)
- [Best practice for re-loading Seed Data](https://productsupport.red-gate.com/hc/en-us/community/posts/24969051128093-Best-practice-for-re-loading-Seed-Data)