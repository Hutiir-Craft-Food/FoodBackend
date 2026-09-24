---
name: architecture-review
description: Perform a read-only architectural and implementation review of a FoodBackend feature against the existing codebase patterns, JPA/MapStruct conventions, API contracts, tests, maintainability, and concrete performance risks.
---

# FoodBackend Architecture Review

## Purpose

Review an implemented FoodBackend feature for correctness, architectural consistency, maintainability, persistence behavior, API integrity, security, tests, and concrete performance risks.

The review is:

- read-only by default;
- evidence-based;
- focused on material problems;
- compared against the existing FoodBackend codebase.

Do not modify code during the review.

Do not automatically refactor code.

Do not turn style preferences into defects.

## Review philosophy

The existing FoodBackend architecture is the baseline.

A generic Spring Boot best practice is not automatically a reason to report an issue.

Report a finding when there is a concrete problem such as:

- incorrect behavior;
- violated requirement;
- broken API contract;
- incorrect business rule;
- inappropriate responsibility;
- excessive coupling;
- duplicated business knowledge;
- unnecessary abstraction;
- transaction/lazy-loading defect;
- N+1 query;
- inefficient or unsafe persistence access;
- security/authorization problem;
- inadequate test coverage of important behavior;
- significant maintainability problem.

If the concern is subjective or uncertain, report it as a question or observation rather than a defect.

## Severity

Use only these severity levels:

### BLOCKER

The implementation is incorrect or unsafe enough that it should not be merged.

Examples:

- data corruption;
- security vulnerability;
- broken core feature behavior;
- clear violation of an explicit requirement;
- production-breaking persistence behavior.

### HIGH

A significant correctness, architecture, persistence, or maintainability problem that should be fixed before merge.

### MEDIUM

A real issue that should be addressed but does not normally invalidate the whole implementation.

### LOW

A minor but concrete improvement with limited impact.

### QUESTION

The code may be correct, but an important behavior or design decision cannot be established from the repository.

Do not classify subjective style preferences as LOW findings.

## Phase 1 — Read project rules

Read:

1. `.github/copilot-instructions.md`;
2. relevant project instructions;
3. the feature/task description;
4. relevant documentation.

Treat explicit project/domain rules as review criteria.

In particular, check the existing rules for:

- controller → service → repository flow;
- DTO/MapStruct API boundaries;
- transaction behavior;
- product/cart conventions;
- availability behavior;
- exception and HTTP conventions;
- tests.

## Phase 2 — Understand the feature

Identify:

- what behavior was requested;
- what files/classes were changed;
- what API behavior changed;
- what persistence behavior changed;
- what tests were added/changed.

Do not assume the changed files are the complete scope.

Search for callers and consumers.

Inspect the relevant existing implementations before judging whether the new code is inconsistent.

## Phase 3 — Compare with existing patterns

Find analogous implementations in the repository.

For each relevant concern compare the feature against existing FoodBackend patterns:

- controller;
- service;
- repository;
- entity;
- mapper;
- DTO;
- exception;
- tests.

Ask:

1. Is the new implementation consistent with the existing pattern?
2. If not, is there a concrete reason?
3. Does the deviation solve an actual problem?
4. Did the change introduce a new abstraction without need?
5. Did the change unnecessarily alter an existing architectural boundary?

Do not report a deviation simply because another architecture could also be used.

## Phase 4 — Correctness and requirements

Verify the actual behavior against the requested requirements.

Check:

- normal flow;
- empty input;
- invalid input;
- missing records;
- unavailable/unpublished products;
- ownership/access;
- edge cases relevant to the feature;
- response shape;
- HTTP status;
- error behavior.

Do not infer unspecified product decisions.

If a requirement is ambiguous and implementation chose one behavior, report it as `QUESTION` unless repository evidence establishes the intended convention.

## Phase 5 — Controller review

Check:

- endpoint mapping;
- path variables vs query parameters;
- request/response DTO usage;
- validation;
- authentication context;
- HTTP concerns remaining in the controller;
- business logic accidentally placed in the controller;
- consistency with existing controller conventions.

Do not report short controller methods or simple delegation as an issue merely because another architecture could abstract them.

## Phase 6 — Service review

Check:

- business rules belong in the service;
- service responsibilities are coherent;
- transaction boundaries are appropriate;
- repository orchestration is correct;
- exceptions follow project conventions;
- mapping/business logic is not unnecessarily mixed;
- no unnecessary service abstraction was introduced.

Pay attention to whether a service now owns several unrelated responsibilities because of the feature.

Do not require additional layers merely to satisfy theoretical SOLID principles.

## Phase 7 — Repository and JPA review

Inspect actual queries and entity relationships.

Check:

### Query correctness

- correct predicates;
- correct joins;
- correct handling of missing records;
- correct handling of unavailable products;
- duplicate rows;
- ordering;
- pagination where required.

### Relationship loading

- lazy-loading failures;
- N+1 queries;
- unnecessary eager fetching;
- excessive entity graph size;
- fetching collections that are not required.

### Query strategy

Prefer the project's established approach:

- derived queries;
- JPQL;
- EntityGraph;
- fetch joins;
- projections.

Do not report native SQL merely because it is native.

Report it only if it creates a concrete issue or unnecessarily violates an established project convention.

### Data volume

Check for:

- unbounded collection queries;
- loading large object graphs;
- unnecessary entity hydration;
- repeated database access.

Performance findings require a concrete mechanism, not speculation.

## Phase 8 — MapStruct and API mapping

Check:

- entities are not exposed directly from new public API responses;
- MapStruct is used consistently where appropriate;
- mapper responsibilities remain mapping-oriented;
- business rules are not hidden inside mappers;
- nested response data is populated correctly;
- null/optional behavior is correct;
- response fields match the API contract.

If manual mapping is used, determine whether it has a concrete justification before reporting it.

## Phase 9 — Product and cart rules

When the feature touches cart/product hydration, explicitly verify:

- `ProductPriceEntity.id` is treated as `productPriceId`;
- missing product prices are omitted where required;
- an existing price belonging to an unavailable/unpublished product is still returned;
- `product.available = false` is preserved for unavailable/unpublished products;
- hydration queries are not incorrectly filtered by product availability;
- DTO nesting and field names remain compatible.

For HCMP-314, verify:

`GET /v1/products/variants?priceIds=1,2,3`

and its documented response behavior.

Do not invent general 4xx semantics if the product decision is still unspecified.

## Phase 10 — Security review

Check:

- authentication;
- authorization;
- ownership;
- exposure of private data;
- user-to-resource access;
- seller/customer boundaries;
- accidental exposure through DTOs or repository queries;
- secrets/tokens/credentials.

Do not claim a security issue without identifying the actual data or access path.

## Phase 11 — Test review

Check whether tests verify behavior rather than implementation details.

Look for coverage of relevant:

- happy path;
- invalid input;
- missing records;
- unavailable data;
- authorization/ownership;
- empty collections;
- response structure;
- repository behavior where query correctness matters.

Check whether existing tests were weakened, removed, or changed merely to accommodate the implementation.

Do not demand tests for irrelevant edge cases.

Do not treat raw code coverage percentage as proof of quality.

## Phase 12 — Maintainability and design

Evaluate:

- responsibility boundaries;
- coupling;
- cohesion;
- naming;
- complexity;
- duplicated business knowledge;
- unnecessary abstractions;
- difficult-to-test code;
- hidden side effects.

### SOLID

Do not report "SOLID violation" without a concrete consequence.

For example, report:

> `CartItemService` now owns unrelated product-search behavior, making changes to either use case coupled and requiring unrelated dependencies.

Do not report:

> This class violates SRP because it has two methods.

### DRY

Report duplication when the same business rule or knowledge is implemented independently.

Do not report simple syntactic similarity as a defect.

### Abstraction

Question new abstractions when:

- they have only one meaningful implementation;
- they hide trivial logic;
- they add indirection without solving a current problem;
- they exist only for hypothetical future requirements.

Do not demand abstraction merely because code could theoretically be reused later.

## Phase 13 — Scope review

Check whether the implementation changed unrelated:

- classes;
- packages;
- APIs;
- migrations;
- formatting;
- architecture.

Unrelated refactoring should be reported when it increases review risk or changes behavior.

Do not report harmless formatting changes as architectural defects unless they materially obstruct review.

## Phase 14 — Findings format

Return findings first.

For each finding use:

### [SEVERITY] Short title

**Location:** `path/to/File.java:line`

**Problem:**
Describe the concrete issue.

**Evidence:**
Explain the relevant code/repository pattern.

**Impact:**
Explain why it matters.

**Suggested direction:**
Give the smallest reasonable direction for fixing it. Do not rewrite the feature unless explicitly asked.

If no material issue exists, state:

> No material architectural or implementation issues found.

Then provide a concise review summary.

## Important evidence rules

- Inspect the relevant function/method body, not only its signature.
- Inspect callers when behavior depends on how a method is used.
- Search before claiming that an implementation/pattern does not exist.
- Do not claim N+1 without identifying the relationship/query path that causes it.
- Do not claim a performance problem without identifying the concrete query/data-flow mechanism.
- Do not claim an API incompatibility without identifying the changed contract.
- Do not claim a SOLID/DRY problem without identifying the concrete consequence.
- If unsure whether something is a bug, use `QUESTION`.
- Distinguish:
    - bug/correctness issue;
    - architectural concern;
    - maintainability concern;
    - style preference.

Only the first three normally warrant actionable findings.

## Final review checklist

Before finalizing:

1. Project instructions were read.
2. Relevant analogous implementations were inspected.
3. The feature was reviewed against actual FoodBackend conventions.
4. Requirements were checked independently of the implementation.
5. API behavior was checked.
6. Controller/service/repository boundaries were checked.
7. JPA relationships and query behavior were checked.
8. Transaction and lazy-loading behavior were checked.
9. MapStruct/API boundaries were checked.
10. Product/cart-specific rules were checked when relevant.
11. Security/ownership was checked when relevant.
12. Tests were reviewed for meaningful behavior.
13. Performance findings are evidence-based.
14. No theoretical SOLID/DRY/style criticism was presented as a defect.
15. No unrelated refactoring was requested.
16. Findings include concrete file/line evidence where available.
17. Review did not modify the repository.

The default outcome of this skill is a review report, not code changes.