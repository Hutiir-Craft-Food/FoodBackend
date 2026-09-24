# FoodBackend Copilot Instructions

## Project context

- This is a Spring Boot 3.3 backend using Java 17, PostgreSQL, Spring Data JPA, Bean Validation, MapStruct, Lombok, Flyway, and JUnit 5.
- Preserve the existing package structure under `com.khutircraftubackend`.
- Keep changes scoped to the requested feature; do not refactor unrelated code.
- Treat the existing codebase as the primary source of architectural conventions.
- Generic best practices are guidance, not a reason to replace an established project pattern.

## Existing-code-first principle

Before introducing a new implementation pattern, abstraction, utility, or architectural approach:

1. Search the repository for analogous functionality.
2. Inspect at least 2-3 relevant existing implementations when they are available.
3. Identify the established patterns for:
    - controller structure;
    - service responsibilities;
    - repository queries;
    - entities and relationships;
    - request/response DTOs;
    - MapStruct mappings;
    - validation;
    - exceptions and HTTP responses;
    - transaction boundaries;
    - tests.
4. Prefer an existing project pattern when it is suitable for the requested feature.
5. Do not copy an existing implementation blindly if it has a concrete defect or conflicts with explicit requirements.
6. If deviating from an established pattern, explain:
    - what the existing pattern is;
    - why it is insufficient for this case;
    - why the proposed deviation is justified.
7. Do not introduce a new abstraction merely because it is theoretically more extensible, reusable, or "cleaner".

## Architecture and implementation

- Follow the existing controller -> service -> repository flow.
- Keep HTTP concerns in controllers and business rules in services.
- Use request/response records and MapStruct mappers at API boundaries.
- Do not expose JPA entities directly from new public API responses.
- Use `@Transactional(readOnly = true)` for read operations that traverse lazy JPA relationships.
- Prefer repository queries with targeted `EntityGraph` or fetch joins when a response needs related product, seller, unit, image, or image-variant data.
- Preserve existing exception classes, HTTP status conventions, validation style, and response messages.
- Do not introduce a new exception hierarchy when an existing exception is appropriate.
- Do not introduce a new mapper, helper, utility, or base class if an existing project component already has the appropriate responsibility.
- Prefer constructor injection and follow the existing dependency-injection style.
- Keep persistence-specific logic in repositories or persistence-oriented components rather than controllers.
- Keep business decisions in services rather than repositories or controllers.
- Do not make entities responsible for HTTP/API representation.
- Do not add unnecessary layers such as facades, use-case classes, factories, strategies, adapters, or managers unless the existing architecture or a concrete requirement justifies them.

## SOLID and DRY

Apply SOLID and DRY pragmatically.

- A theoretical SOLID violation is not by itself a reason to refactor existing code.
- Consider a design problem significant when it causes a concrete issue such as:
    - unclear responsibility;
    - difficult modification;
    - excessive coupling;
    - unstable dependencies;
    - difficult testing;
    - duplicated business knowledge;
    - inappropriate abstraction.
- DRY applies primarily to duplicated business knowledge, rules, or behavior.
- Do not abstract merely because two pieces of code look syntactically similar.
- Prefer a small amount of straightforward duplication over a premature abstraction whose behavior or ownership is unclear.
- Do not introduce Strategy, Factory, Builder, Template Method, or similar patterns unless there is a concrete design problem that the pattern solves.
- Prefer the simplest design that fits the existing architecture and current requirements.

## API boundaries

- Preserve existing API conventions.
- Use request/response DTOs or records at public API boundaries.
- Do not expose JPA entities directly.
- Keep DTO field names and nesting aligned with existing API contracts and frontend consumers.
- Do not rename existing API fields without an explicit requirement.
- Do not silently change HTTP status codes or response structures.
- Validate incoming request data using the project's existing Bean Validation conventions.
- Preserve existing error response conventions.
- When the required error behavior is not explicitly defined, do not invent a product decision. Identify the ambiguity and ask for confirmation or document the assumption in the plan.

## JPA and persistence

- Follow existing entity relationship mappings unless the feature explicitly requires changing them.
- Be aware of lazy relationships and transaction boundaries.
- Avoid accidental N+1 queries.
- Do not solve every potential N+1 problem by eagerly fetching relationships globally.
- Prefer targeted `EntityGraph`, fetch joins, projections, or dedicated repository queries where appropriate.
- Do not fetch large collections when the response does not need them.
- Avoid changing repository queries without considering their existing callers.
- When changing a query, inspect its callers and existing tests.
- Consider pagination and result size when introducing queries that can return collections.
- Do not introduce database schema changes without considering Flyway migration requirements.
- Do not modify an existing migration that may already have been applied; create a new migration when a schema change is required.
- Preserve existing PostgreSQL conventions and naming.

## Product and cart domain conventions

- `ProductPriceEntity.id` is the `productPriceId` used by the cart.
- A product price that no longer exists is omitted from hydration responses.
- An existing price belonging to an unavailable/unpublished product must still be returned with `product.available = false`.
- Do not filter hydration queries by product availability.
- Keep DTO field names and nesting aligned with the API contract and existing frontend consumers.

## HCMP-314 reference

- The hydration endpoint is:
  `GET /v1/products/variants?priceIds=1,2,3`
- It returns product, price, unit, seller, and thumbnail data for the requested price IDs.
- The handling of general 4xx errors is an explicit product decision and must not be silently invented; document or confirm the chosen behavior before finalizing the endpoint.

## Controllers

- Controllers should handle HTTP concerns:
    - request parameters;
    - path variables;
    - request bodies;
    - validation;
    - authentication/authorization context;
    - HTTP response mapping.
- Do not place business rules in controllers.
- Follow the existing controller naming, endpoint mapping, and response conventions.
- Prefer the simplest controller implementation consistent with existing controllers.

## Services

- Services own business logic and orchestration.
- Keep service methods focused on the requested use case.
- Do not move trivial repository delegation into elaborate abstractions without a reason.
- Preserve existing transaction boundaries unless the feature requires changing them.
- Do not silently change transactional semantics while implementing an unrelated feature.

## Repositories

- Repositories own persistence access.
- Prefer expressive repository methods and targeted queries.
- Before adding a query, search for an existing query that already provides the required data.
- Avoid loading entities and relationships separately when one appropriate query can retrieve the required data safely.
- Avoid unnecessary native SQL when JPQL, derived queries, projections, or EntityGraph can solve the problem consistently with the existing codebase.
- If native SQL is required, keep it scoped and explain why.

## MapStruct and mapping

- Use MapStruct for API/entity mapping where that is the established project pattern.
- Do not replace MapStruct mappings with manual mapping without a concrete reason.
- Keep mapping concerns separate from business logic.
- If additional data must be calculated or loaded to construct a response, keep that responsibility in the service/repository layer rather than hiding business logic inside a mapper.
- Follow existing MapStruct configuration and naming conventions.

## Validation and errors

- Preserve the project's existing validation approach.
- Validate at the appropriate API boundary.
- Do not duplicate validation rules across controller, service, and repository without a reason.
- Preserve existing exception types and global error handling.
- When behavior for invalid, missing, or inconsistent data is unspecified, flag it rather than inventing a new convention.

## Security

- Do not log, hard-code, commit, or expose credentials, tokens, secrets, passwords, or private configuration.
- Follow existing authentication and authorization mechanisms.
- Do not weaken authorization checks to make a feature work.
- Do not expose data merely because it is available through a repository query.
- Consider ownership/access rules when modifying user-specific resources such as cart items, favorites, orders, or seller data.

## Testing and validation

- Add or update focused unit and controller tests for every behavior change.
- Follow existing test structure and naming conventions.
- Reuse existing test fixtures, factories, builders, and helper methods when appropriate.
- Cover, where applicable:
    - valid input;
    - empty input;
    - invalid input;
    - missing records;
    - unavailable products;
    - ownership/access violations;
    - response shape;
    - relevant persistence behavior.
- Do not weaken or remove existing tests to make a change pass.
- Do not add tests solely to increase coverage metrics if they do not verify meaningful behavior.
- Run the smallest relevant Maven test scope first, then broaden validation if needed.
- If a test exposes a product or design ambiguity, do not change the test merely to accommodate an arbitrary implementation.

## Scope and refactoring

- Implement only what the requested feature requires.
- Do not combine feature work with unrelated cleanup.
- Do not rename unrelated classes, methods, packages, or DTO fields.
- Do not reformat unrelated files.
- Do not "modernize" existing code unless explicitly requested or required by the feature.
- If an existing defect blocks the requested feature, identify it explicitly instead of silently expanding scope.
- Prefer small, reviewable changes.

## Performance and scalability

Consider performance when it is relevant to the requested feature.

Pay particular attention to:

- N+1 queries;
- unbounded collection loading;
- unnecessary entity hydration;
- repeated database queries;
- missing pagination;
- expensive filtering/sorting;
- large response payloads;
- unnecessary object transformations.

Do not optimize speculatively.

A performance concern should be reported when there is a concrete reason based on the query, data flow, response size, or execution pattern—not merely because an optimization might theoretically be possible.

## Documentation and comments

- Prefer clear code over comments explaining obvious behavior.
- Add comments when they explain a non-obvious business rule, technical constraint, or intentional deviation from the existing pattern.
- Do not add comments that merely restate the code.
- Update relevant project documentation when the feature changes externally visible behavior or an established development procedure.

## Planning and review

When the user asks for a feature plan, use the `feature-plan` skill.

When the user asks for an architectural review or review of an implemented feature, use the `architecture-review` skill.

Plans and reviews must be evidence-based:
- inspect the actual repository;
- reference existing implementations;
- distinguish facts from assumptions;
- do not invent missing requirements;
- identify open questions explicitly.

A review is read-only by default. Do not modify code while performing an architecture review unless explicitly asked to implement the fixes.