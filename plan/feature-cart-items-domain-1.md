---
goal: "Implement the authenticated Cart Items domain for registered users"
version: "1.0"
date_created: "2026-09-13"
last_updated: "2026-09-13"
owner: "FoodBackend"
status: "In progress"
tags: [feature, backend, cart, spring-boot, jpa, flyway, api]
---

# Introduction

![Status: In progress](https://img.shields.io/badge/status-In%20progress-yellow)

This plan defines only the HCMP-329 backend Cart Items domain for registered users. It covers persistent cart-item storage, authenticated read access, and the cart-item upsert/delete API contract described in the linked Confluence specification. The plan follows the repository's existing Spring Boot controller-service-repository flow, JPA entities, request/response records, validation, exception conventions, Flyway migrations, JWT security, and JUnit 5/Mockito testing style.

## 1. Requirements & Constraints

- **REQ-001**: Provide an authenticated `GET /v1/cart/items` endpoint returning `CartItemDTO[]`.
- **REQ-002**: Provide an authenticated `PUT /v1/cart/items/{productPriceId}` endpoint accepting `{ "quantity": integer }` and returning `CartItemDTO`.
- **REQ-003**: Implement `PUT` as create-or-replace semantics for the current authenticated user and `productPriceId`.
- **REQ-004**: Treat incoming quantity as an absolute value; never add it to an existing quantity.
- **REQ-005**: Return `201 Created` when `PUT` inserts a new row and `200 OK` when it updates an existing row.
- **REQ-006**: Validate quantity inclusively between `1` and `100000`; invalid values return `400 Bad Request` with the Ukrainian user-facing message `Кількість має бути від 1 до 100000`.
- **REQ-007**: Validate that `productPriceId` references an existing `ProductPriceEntity` on the insert branch; an unknown price ID returns `400 Bad Request`.
- **REQ-008**: Provide an authenticated `DELETE /v1/cart/items/{productPriceId}` endpoint returning `204 No Content` after deletion.
- **REQ-009**: Deleting an already absent item returns `404 Not Found`, as specified by the API contract.
- **REQ-010**: Persist items indefinitely until explicit deletion or future checkout behavior removes them; no expiration field or scheduled cleanup is introduced.
- **REQ-011**: `CartItemDTO` contains exactly `productPriceId` and `quantity`; do not expose the internal surrogate `id`, user identity, price, unit, product, seller, currency, or subtotal.
- **REQ-012**: Use the authenticated principal to scope every cart repository operation to one user; never accept `userId` from the request path or body.
- **REQ-013**: All `/v1/cart/items` operations require a valid JWT and must return `401 Unauthorized` when unauthenticated.
- **REQ-014**: Use a unique database constraint on `(user_id, product_price_id)`.
- **REQ-015**: Keep API display hydration outside this domain; clients obtain product display data through `GET /v1/products/variants?priceIds=...`.
- **REQ-016**: Keep all user-facing API error messages in Ukrainian and all new internal log messages in English.
- **PERF-001**: Make the cart read path one bounded database query for the current user, map only `productPriceId` and `quantity`, and never load product, seller, unit, or image relationships.
- **PERF-002**: Make PUT and DELETE use the composite `(user_id, product_price_id)` index through exact user-scoped predicates; do not scan all cart rows or query by the internal cart-item ID.
- **PERF-003**: Keep transactions short: resolve the authenticated user before entering the cart write operation where possible, perform one lookup plus one insert/update for PUT, and perform one lookup plus one delete for DELETE.
- **PERF-004**: Design the repository methods to avoid N+1 queries, unnecessary entity graphs, repeated user lookups inside item loops, and unbounded in-memory transformations.
- **PERF-005**: Validate production-oriented behavior with query-count and concurrent-operation tests available from existing project dependencies; do not add a new load-testing framework as part of HCMP-329.
- **PERF-006**: Review the active datasource connection-pool configuration and database indexes before completion; change configuration only when the existing application settings demonstrably cannot support the endpoint's query pattern.
- **SEC-001**: Do not log JWTs, credentials, full request bodies, or sensitive user data.
- **SEC-002**: Enforce authorization at the security configuration/controller boundary and user scoping in the service/repository layer.
- **CON-001**: Preserve packages under `com.khutircraftubackend` and follow the existing domain-directory style: keep the Cart Item entity and its related repository, service, DTO, mapper, controller, exceptions, and tests under the Cart Item domain directory; add subdirectories only where they match existing repository conventions.
- **CON-002**: Use Java 17, Spring Boot 3.3, Spring Data JPA, Bean Validation, Lombok, Flyway, and JUnit 5 already present in the repository.
- **CON-003**: Do not add a dependency or a separate persistence technology for the cart.
- **CON-004**: Do not expose JPA entities directly from the new public API.
- **GUD-001**: Use request/response records where an API boundary record is appropriate, with MapStruct only where it reduces mapping duplication.
- **GUD-002**: Put business rules in the service, HTTP status and request binding in the controller, and persistence access in repositories.
- **GUD-003**: Mark read methods that traverse JPA relations with `@Transactional(readOnly = true)` and write methods with `@Transactional`.
- **GUD-004**: Use targeted repository methods for `(user_id, product_price_id)` lookups rather than `findById`, because the API identifier is not the internal cart-item primary key.
- **PAT-001**: Follow existing exception-message constant classes, typed runtime exceptions, `GlobalErrorResponse`, `@RequiredArgsConstructor`, `@Slf4j`, and nested Arrange/Act/Assert tests.
- **PAT-002**: Follow existing Flyway naming and SQL style, including explicit foreign keys, indexes, and database-level constraints.
- **PAT-003**: Preserve last-write-wins behavior; do not introduce optimistic locking or row-level locking for this scope.
- **PAT-004**: Prefer a DTO projection or a repository query selecting only `productPriceId` and `quantity` for GET, because the public cart contract does not require entity relationships.
- **PAT-005**: Implement HCMP-329 persistence/domain foundations before child endpoint tasks HCMP-330 (PUT) and HCMP-331 (DELETE). The child tasks may proceed in parallel only after the shared schema, entity, relationship, and repository contracts are stable.
- **PAT-006**: Include `created_at` and `updated_at` audit fields because existing entities use audit metadata and the fields support operational statistics without changing the public DTO contract.
- **PAT-007**: Handle a concurrent insert race with a bounded strategy only. Prefer one atomic PostgreSQL upsert when repository conventions and the child PUT contract allow it; otherwise permit at most one retry after a unique-constraint conflict, then propagate the persistence failure. Never retry indefinitely.

## 2. Implementation Steps

### Implementation Phase 1

- **GOAL-001**: Establish the HCMP-329 Cart Items persistence/domain foundation before implementing child endpoint tasks HCMP-330 and HCMP-331.

| Task | Description | Completed | Date |
|------|-------------|-----------|------|
| TASK-001 | Add `src/main/resources/db/migration/V16__Create_cart_items_table.sql`. Create `cart_items` with an identity `bigint` primary key `id`, non-null `user_id` foreign-keyed to `users(id)`, non-null `product_price_id` foreign-keyed to `product_prices(id)`, non-null integer `quantity`, and non-null `created_at`/nullable `updated_at` audit columns consistent with existing migrations. Use the repository's existing FK deletion conventions; if no convention overrides it, cascade user deletion and prevent deletion of a referenced product price. Add one composite unique constraint or unique index on `(user_id, product_price_id)`; use that index for exact item lookup and user-scoped reads, and avoid redundant indexes unless query plans demonstrate a need. Do not add expiry behavior. | ✅ | 2026-09-13 |
| TASK-002 | Add `src/main/java/com/khutircraftubackend/cart/CartItemEntity.java` in the existing Cart Item domain-directory style. Map the table with JPA, Lombok, and the existing audit conventions, including `createdAt` and `updatedAt`. Keep the internal `id` private to persistence and expose getters only to domain code; map `user` as a lazy `@ManyToOne` to `UserEntity` and `productPrice` as a lazy `@ManyToOne` to `ProductPriceEntity`, both non-null. | ✅ | 2026-09-13 |
| TASK-003 | Add `src/main/java/com/khutircraftubackend/cart/CartItemRepository.java`. Extend `JpaRepository<CartItemEntity, Long>`. Add a projection-based user read returning only `productPriceId` and `quantity`, an exact `findByUserIdAndProductPriceId(Long userId, Long productPriceId)` lookup for PUT/DELETE, and a user-scoped delete operation suitable for the delete endpoint. Verify the generated SQL uses the composite index and does not fetch `ProductPriceEntity`, `UserEntity`, or other display relationships for GET. | ✅ | 2026-09-13 |
| TASK-004 | Add `src/main/java/com/khutircraftubackend/cart/exception/CartExceptionMessages.java` with constants for invalid quantity, invalid product price, cart item not found, and any generic cart validation text. Keep messages intended for API consumers in Ukrainian and keep constant names in English. | | |
| TASK-005 | Add typed cart exceptions under `src/main/java/com/khutircraftubackend/cart/exception/` for invalid quantity, invalid product price, and missing cart item. Match existing runtime exception style and status handling; use `400` for invalid input/reference and `404` for deletion of an absent item. | | |

### Implementation Phase 2

- **GOAL-002**: Define API records, map persistence objects safely, and implement authenticated cart business logic.

| Task | Description | Completed | Date |
|------|-------------|-----------|------|
| TASK-006 | Add `src/main/java/com/khutircraftubackend/cart/response/CartItemDTO.java` as a response/request-compatible record with exactly `Long productPriceId` and `int quantity`. Do not add `id`, user, currency, display, or audit fields. Add `src/main/java/com/khutircraftubackend/cart/request/CartItemRequest.java` with `@Min(1)` and `@Max(100000)` on `int quantity`, or use the repository's established validation style if primitive validation semantics require a wrapper type. | | |
| TASK-007 | Add `src/main/java/com/khutircraftubackend/cart/mapper/CartItemMapper.java` as a Spring MapStruct mapper, or a narrowly scoped explicit mapper if MapStruct cannot map the nested `productPrice.id` cleanly without exposing unrelated fields. Map `CartItemEntity.productPrice.id` to `CartItemDTO.productPriceId` and `quantity` directly. | | |
| TASK-008 | Add `src/main/java/com/khutircraftubackend/cart/CartService.java`. Implement `getItems(Principal)` as a read-only transaction using one user resolution and one projection query. Implement `upsertItem(Principal, Long, CartItemRequest)` as a short transaction: find the existing item by current user and price ID, update quantity when present, otherwise resolve the price ID and insert one row. Never increment an existing quantity. Implement `deleteItem(Principal, Long)` as a short transaction scoped by current user and price ID, throwing the cart-not-found exception when absent. Do not iterate over entities or issue per-item queries. Log only technical failures and keep logs in English. | | |
| TASK-009 | Add `src/main/java/com/khutircraftubackend/cart/CartController.java` under `/v1/cart/items`. Implement `GET`, `PUT /{productPriceId}`, and `DELETE /{productPriceId}`. Bind `@Valid CartItemRequest`, return the mapped DTO, set `201` for insert and `200` for update, and set `204` for successful deletion. Obtain the authenticated user through `Principal` or the repository's established security pattern; do not accept a user ID from the client. | | |
| TASK-010 | Introduce a small service result/status mechanism for the `PUT` operation only if required to distinguish insert from update without leaking persistence details into the controller. The preferred shape is a service result containing the DTO and a creation flag, or a domain result record; do not return `ResponseEntity` from the service. Ensure the result is produced without an additional read-after-write query when the saved entity already contains the required fields. | | |
| TASK-011 | Verify `src/main/java/com/khutircraftubackend/config/SecurityConfig.java` leaves `/v1/cart/items/**` protected by `.anyRequest().authenticated()` and does not add a broad `permitAll` matcher. If controller method security is used for consistency, add only an explicit authenticated guard and avoid role restrictions because the requirement applies to registered buyers generally. | | |
| TASK-012 | Extend `GlobalExceptionHandler` only where existing `@ResponseStatus` handling cannot produce the required `GlobalErrorResponse`. Preserve the repository's response shape, use Ukrainian messages for business errors, use Spring's `401` behavior for missing/invalid JWTs, and do not add a broad catch or silent fallback. | | |

### Implementation Phase 3

- **GOAL-003**: Prove the API contract, security boundary, transaction behavior, and persistence constraints with focused tests.

| Task | Description | Completed | Date |
|------|-------------|-----------|------|
| TASK-013 | Add `src/test/java/com/khutircraftubackend/cart/CartServiceTest.java` using `@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks`, nested test classes, `@DisplayName`, and Arrange/Act/Assert sections. Cover: empty authenticated cart, multiple items mapped to only `productPriceId` and quantity, insert of a valid new price, update of an existing item by absolute quantity, unknown price ID on insert, quantity below `1`, quantity above `100000`, delete of an existing item, and `404` exception behavior for an absent item. Verify user-scoped repository calls, no per-item repository loop, and that an update does not resolve or insert a new product price unnecessarily. | | |
| TASK-014 | Add `src/test/java/com/khutircraftubackend/cart/CartControllerTest.java` or extend the repository's established Spring MVC test approach. Verify `GET` response shape, `PUT` `201` for insert, `PUT` `200` for update, `DELETE` `204`, validation response for invalid quantity, and that the DTO contains no internal ID or display fields. | | |
| TASK-015 | Add an authenticated/unauthenticated controller integration test, following `AuthenticationControllerImplTest` conventions and available JWT test configuration. Verify requests without a valid token to `GET`, `PUT`, and `DELETE` receive `401 Unauthorized`, while a valid authenticated user can access only their own cart items. Do not print tokens in test output. | | |
| TASK-016 | Add repository or `@DataJpaTest` coverage for the `(user_id, product_price_id)` uniqueness constraint and user isolation. Verify two different users can hold the same `productPriceId`, while one user cannot create duplicate rows for that price. Include a read-path assertion that one GET operation loads all fields through one repository query and does not trigger relationship queries. | | |
| TASK-017 | Add migration verification to the existing test/build workflow. Confirm Flyway applies the new migration after `V15`, foreign keys point to `users` and `product_prices`, quantity is non-null, and the composite index supports the exact user/price predicates. | | |
| TASK-018 | Add production-oriented integration coverage using only existing test dependencies: execute repeated user-scoped GET/PUT/DELETE operations with representative cart data, assert bounded repository interaction counts, verify no N+1 behavior, and exercise concurrent PUT attempts for the same user and price. The test must verify that the database uniqueness constraint remains authoritative, the implementation does not retry indefinitely, and no request returns a success-shaped response after the bounded retry strategy is exhausted. | | |
| TASK-019 | Review `application*.yml` datasource and Hibernate settings and the database query plan for the cart repository methods. Document the observed query shape and confirm that the endpoint does not require a new pool or framework dependency. If an existing pool setting is clearly undersized or a missing index is proven by the query plan, update only the directly related configuration or migration and add a focused regression check. | | |
| TASK-020 | Run focused validation in this order: `./mvnw -Dtest=CartServiceTest,CartControllerTest,CartRepositoryTest test`, then the relevant full test package if present, then `./mvnw test`. If a test selector or class name differs from the created tests, use the exact repository test names without adding new tooling. | | |

### Implementation Phase 4

- **GOAL-004**: Complete contract review and repository-aligned documentation before handing the implementation to the next branch.

| Task | Description | Completed | Date |
|------|-------------|-----------|------|
| TASK-021 | Review all changed files against HCMP-329 and the Confluence page. Confirm the four backend acceptance criteria and the complete cart CRUD contract are covered, and `GET /v1/products/variants?priceIds=...` remains the exclusive display-data hydration path. | | |
| TASK-022 | Add or update OpenAPI annotations only if the repository's cart controllers use them. Document endpoint paths, status codes, request/response schemas, authentication requirement, quantity range, and the absence of a `POST /v1/cart/items` endpoint. Keep documentation language consistent with existing API annotations. | | |
| TASK-023 | Inspect the final diff for unrelated changes, secret exposure, entity leakage, N+1 queries, full-table scans, missing composite indexes, user-scope bypasses, inconsistent status codes, and non-English user-facing messages. Confirm the worktree contains only intended HCMP-329 implementation and test files plus the migration. | | |

## 3. Alternatives

- **ALT-001**: Add a separate `POST /v1/cart/items` endpoint for creation. Not chosen because the specification explicitly requires one `PUT` create-or-replace endpoint and the client already knows `productPriceId`.
- **ALT-002**: Use `CartItemEntity.id` as the API path identifier. Not chosen because the contract addresses items by the user-scoped `productPriceId`, and exposing the surrogate key violates the DTO/data-layer requirements.
- **ALT-003**: Store a serialized cart JSON document per user. Not chosen because the contract requires a normalized `cart_items` table, a per-user/per-price uniqueness constraint, and independent PUT/DELETE operations.
- **ALT-004**: Add optimistic locking or row-level locking. Not chosen because the specification explicitly defines last-write-wins semantics and excludes lock-conflict behavior from this scope.
- **ALT-005**: Return hydrated product, seller, unit, or subtotal data from cart endpoints. Not chosen because hydration is explicitly delegated to the product variants endpoint and cart DTOs are bookkeeping records only.

## 4. Dependencies

- **DEP-001**: Existing `UserEntity`, `UserRepository`, and `UserService.findByPrincipal(Principal)` for authenticated user resolution.
- **DEP-002**: Existing `ProductPriceEntity` and `ProductPriceRepository` for validating and associating `productPriceId` on insert.
- **DEP-003**: Existing JWT filter and `SecurityConfig` for authentication and `401 Unauthorized` handling.
- **DEP-004**: Existing `GlobalErrorResponse`, `GlobalExceptionHandler`, and exception status conventions for structured API errors.
- **DEP-005**: PostgreSQL schema and Flyway migration ordering after `V15__Add_product_article_column.sql`.
- **DEP-006**: Existing Maven/JUnit 5/Mockito/Spring Security Test dependencies; no dependency additions are expected.

## 5. Files

- **FILE-001**: `src/main/resources/db/migration/V16__Create_cart_items_table.sql` — cart table, foreign keys, indexes, and `(user_id, product_price_id)` uniqueness.
- **FILE-002**: `src/main/java/com/khutircraftubackend/cart/CartItemEntity.java` — JPA persistence entity.
- **FILE-003**: `src/main/java/com/khutircraftubackend/cart/CartItemRepository.java` — user-scoped persistence operations.
- **FILE-004**: `src/main/java/com/khutircraftubackend/cart/CartService.java` — authenticated cart business logic and transaction boundaries.
- **FILE-005**: `src/main/java/com/khutircraftubackend/cart/CartController.java` — `/v1/cart/items` HTTP endpoints and status codes.
- **FILE-006**: `src/main/java/com/khutircraftubackend/cart/request/CartItemRequest.java` — validated quantity request record.
- **FILE-007**: `src/main/java/com/khutircraftubackend/cart/response/CartItemDTO.java` — minimal public cart-item DTO.
- **FILE-008**: `src/main/java/com/khutircraftubackend/cart/mapper/CartItemMapper.java` — entity-to-DTO mapping.
- **FILE-009**: `src/main/java/com/khutircraftubackend/cart/exception/CartExceptionMessages.java` — Ukrainian user-facing cart messages.
- **FILE-010**: `src/main/java/com/khutircraftubackend/cart/exception/` — typed cart exceptions for invalid input, invalid price reference, and missing item.
- **FILE-011**: `src/main/java/com/khutircraftubackend/exception/GlobalExceptionHandler.java` — only if structured handling is required after testing existing status behavior.
- **FILE-012**: `src/main/java/com/khutircraftubackend/config/SecurityConfig.java` — verification-only or narrowly scoped authenticated matcher adjustment.
- **FILE-013**: `src/test/java/com/khutircraftubackend/cart/CartServiceTest.java` — service/business rule tests.
- **FILE-014**: `src/test/java/com/khutircraftubackend/cart/CartControllerTest.java` — MVC/API contract tests.
- **FILE-015**: `src/test/java/com/khutircraftubackend/cart/CartRepositoryTest.java` — persistence isolation, uniqueness, query-count, and concurrent-operation tests, if repository test infrastructure supports it.
- **FILE-016**: `src/main/resources/application*.yml` — verification-only datasource and Hibernate settings; update only if a measured cart query/load issue requires it.

## 6. Testing

- **TEST-001**: `GET /v1/cart/items` returns an empty JSON array for an authenticated user with no rows.
- **TEST-002**: `GET /v1/cart/items` returns only `productPriceId` and absolute quantity for the current user.
- **TEST-003**: `PUT /v1/cart/items/{productPriceId}` inserts a valid new item and returns `201 Created`.
- **TEST-004**: Repeating `PUT` for the same user and price updates the existing row and returns `200 OK`.
- **TEST-005**: Updating an existing row sets quantity exactly to the request value; it does not add to the previous quantity.
- **TEST-006**: Inserting an unknown `productPriceId` returns `400 Bad Request`.
- **TEST-007**: Quantity `0`, negative quantity, and quantity `100001` are rejected with `400 Bad Request`.
- **TEST-008**: Quantities `1` and `100000` are accepted.
- **TEST-009**: `DELETE /v1/cart/items/{productPriceId}` removes the current user's item and returns `204 No Content`.
- **TEST-010**: Deleting an item absent for the current user returns `404 Not Found`; an item belonging to another user is treated as absent.
- **TEST-011**: Unauthenticated GET, PUT, and DELETE requests return `401 Unauthorized`.
- **TEST-012**: The same product price can be stored for different users, but duplicate rows for one user are prevented by the database constraint.
- **TEST-013**: Flyway migration creates the expected table, foreign keys, indexes, and uniqueness constraint.
- **TEST-014**: Cart endpoints do not return product, seller, unit, currency, subtotal, or internal cart-item ID fields.
- **TEST-015**: No cart endpoint performs a per-item product hydration query; product display hydration remains delegated to the variants endpoint.
- **TEST-016**: A cart GET maps representative user data with one repository query and no relationship/N+1 queries.
- **TEST-017**: PUT and DELETE use exact user/price predicates supported by the composite index and do not perform full-table scans.
- **TEST-018**: Concurrent writes for one user and price cannot create duplicate rows; persistence failures are surfaced as errors rather than converted to successful responses.
- **TEST-019**: Repeated representative cart operations complete without unbounded memory growth or per-item database calls.
- **TEST-020**: The focused Maven test command and the full Maven test command pass without weakening existing tests.

## 7. Risks & Assumptions

- **RISK-001**: The existing exception handling mixes `@ResponseStatus`, domain handlers, and `GlobalExceptionHandler`; endpoint tests must verify the actual serialized `GlobalErrorResponse` rather than assuming annotation behavior.
- **RISK-002**: `UserEntity` and `ProductPriceEntity` relations may be lazy in a way that triggers detached-entity access during mapping; the minimal DTO design should avoid traversing those relations after the service transaction.
- **RISK-003**: Existing migrations and test profiles may use H2-specific behavior; SQL must remain compatible with PostgreSQL and the repository's test setup, and migration validation must run in the supported profile.
- **RISK-004**: A concurrent insert can race before the unique constraint is evaluated; the database constraint is authoritative. The child PUT implementation must use one atomic upsert where compatible with the repository conventions, or at most one explicit retry after a unique-constraint conflict, and must not silently convert an exhausted or unrelated integrity error into a successful response.
- **RISK-005**: The requirement text states both idempotent deletion and `404` for an already absent item; this plan follows the explicit API line requiring `404`, and tests must lock that behavior before implementation is finalized.
- **RISK-006**: The GET contract returns all current cart items without pagination. The implementation must avoid extra relationship loading and preserve the existing API shape; any future pagination change requires a separate product decision.
- **RISK-007**: Database connection-pool capacity is deployment-specific; this plan verifies existing settings and query efficiency but does not guess production pool values without measured workload data.
- **ASSUMPTION-001**: The current authenticated principal name resolves to the user's email, consistent with `UserService.findByPrincipal` and the JWT user-details implementation.
- **ASSUMPTION-002**: `productPriceId` is the `ProductPriceEntity.id` already used by the product variants hydration flow.
- **ASSUMPTION-003**: Checkout does not need to remove cart rows in HCMP-329; persistence continues until explicit DELETE or a future checkout feature.
- **ASSUMPTION-004**: No cart-specific role restriction is required beyond authentication; ordinary registered buyers with valid JWTs may use the endpoints.
- **ASSUMPTION-005**: HCMP-329 establishes the shared Cart Items domain/persistence foundation. PUT and DELETE entry points are implemented in the dependent child tasks HCMP-330 and HCMP-331, respectively; they must consume the contracts created by HCMP-329 and must not duplicate the schema or entity model.
- **ASSUMPTION-006**: The implementation is limited to the backend acceptance criteria and CRUD contract defined by HCMP-329 and its explicitly dependent child tasks; unrelated frontend, checkout, merge, cap, hydration, and broader cart features are not implemented.

## 8. Related Specifications / Further Reading

- [Jira HCMP-329 — Cart Items domain in backend](https://hutircraftfood.atlassian.net/browse/HCMP-329)
- [Confluence — Remote cart for registered user](https://hutircraftfood.atlassian.net/wiki/spaces/HCFW/pages/208797697/Remote+cart+for+registered+user)
- [Repository conventions](../.github/copilot-instructions.md)
