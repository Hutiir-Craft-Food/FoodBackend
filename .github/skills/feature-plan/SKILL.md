---
name: feature-plan
description: Plan a FoodBackend feature by first studying the existing implementation patterns, architecture, JPA/MapStruct conventions, API contracts, and tests. Produces an implementation-ready plan without modifying code.
---

# FoodBackend Feature Plan

## Purpose

Create an implementation-ready plan for a requested FoodBackend feature.

The plan must fit the existing FoodBackend architecture rather than introducing a generic or theoretically ideal architecture.

The repository is the primary source of truth for implementation patterns.

Do not modify source code, tests, migrations, configuration, or documentation while performing this planning workflow.

## Core principle

Before deciding how to implement the feature, determine how FoodBackend already solves similar problems.

Prefer:

> existing project pattern + smallest necessary change

over:

> new abstraction + theoretical generality

Do not introduce architectural patterns merely because they are considered best practice in isolation.

## Phase 1 — Understand the request

Read:

1. the user's task;
2. `.github/copilot-instructions.md`;
3. relevant existing documentation;
4. existing API contracts if available.

Extract:

- requested behavior;
- affected domain;
- API changes;
- persistence changes;
- validation requirements;
- error behavior;
- security/ownership requirements;
- compatibility constraints;
- explicit non-goals.

Do not invent unspecified product behavior.

If an important behavior is ambiguous, record it under `Open questions` instead of silently choosing an implementation.

## Phase 2 — Repository reconnaissance

Search the repository before proposing implementation.

Identify 2-3 analogous implementations where possible.

For product-related work, inspect relevant parts of:

- `product`;
- `category`;
- `cart`;
- seller-related code;
- product images and image variants when relevant.

For cart-related work, inspect relevant:

- `CartItemController`;
- `CartItemService`;
- `CartItemRepository`;
- `CartItemEntity`;
- `CartItemMapper`;
- request/response DTOs;
- exceptions;
- tests.

Also inspect analogous controller/service/repository/entity/mapper/test combinations outside the immediate domain when they reveal a stronger established pattern.

The goal is to identify what the project actually does, not what a generic Spring Boot application would normally do.

## Phase 3 — Determine existing conventions

For the affected area, determine:

### Controller

- endpoint naming;
- `@GetMapping`, `@PostMapping`, etc.;
- path variables vs query parameters;
- request/response records;
- validation;
- authentication context;
- HTTP response handling.

### Service

- business-rule ownership;
- transaction annotations;
- exception handling;
- repository orchestration;
- mapping responsibilities.

### Repository

- derived queries;
- JPQL;
- native SQL;
- `EntityGraph`;
- fetch joins;
- projections;
- pagination.

### Persistence

Inspect relevant:

- entity relationships;
- `FetchType`;
- cascade behavior;
- orphan removal;
- indexes;
- Flyway migrations.

### Mapping

Determine:

- MapStruct configuration;
- mapper method style;
- nested mappings;
- manual mapping used alongside MapStruct;
- where calculated/loaded data is assembled.

### Tests

Determine:

- unit vs integration/controller test conventions;
- naming;
- fixture setup;
- mocking approach;
- assertions;
- repository test patterns.

## Phase 4 — Architectural fit

Explicitly answer:

### Existing pattern

What existing FoodBackend implementation is closest to this feature?

### Why it fits

Why can that pattern be reused?

### Required deviation

Is anything different from the existing pattern actually required?

If yes, explain the concrete reason.

### New abstraction

Is a new abstraction required?

Default answer should be `No`.

Only introduce a new abstraction if there is a concrete problem such as:

- multiple genuinely different behaviors already required;
- duplicated business knowledge that needs one owner;
- an existing responsibility becoming unreasonably complex;
- a stable boundary required by the feature;
- an explicit project requirement.

Do not introduce Strategy, Factory, Builder, Facade, Adapter, Manager, or similar patterns merely for hypothetical future requirements.

### What should not change

List relevant existing components that should remain untouched.

This is important to prevent scope creep.

## Phase 5 — Impact analysis

Check the feature against:

### API

- endpoint;
- path/query/body parameters;
- request DTO;
- response DTO;
- validation;
- HTTP status codes;
- backward compatibility.

### Business logic

- service methods;
- business rules;
- ownership;
- availability/publication rules.

### Persistence

- entities;
- relationships;
- repository queries;
- EntityGraph/fetch joins;
- indexes;
- migrations.

### Transactions

Determine whether the operation:

- reads lazy relationships;
- writes multiple entities;
- requires a transaction;
- requires `readOnly = true`;
- could produce lazy-loading or consistency problems.

### Mapping

Determine:

- existing mapper reuse;
- new mapping methods;
- data that must be assembled outside MapStruct.

### Security

Check:

- authentication;
- authorization;
- resource ownership;
- exposure of private data.

### Tests

Identify tests that need to be added or changed.

### Performance

Check only concrete risks:

- N+1;
- unnecessary queries;
- unbounded result sets;
- excessive entity loading;
- large response payloads.

Do not add speculative optimizations.

## Phase 6 — File-level implementation plan

Produce a concrete list grouped into:

### Create

Files that need to be created.

### Modify

Existing files that need changes.

### Do not modify

Relevant files that might otherwise be tempting to change but should remain untouched.

For every file, explain its responsibility and the expected change.

Use actual repository paths and class names whenever they can be established.

Do not write vague entries such as:

- "update service layer";
- "modify repository as needed";
- "add appropriate tests".

Instead specify the class and the intended change.

## Phase 7 — Test plan

Define focused tests for the actual behavior.

At minimum consider:

- happy path;
- empty input;
- invalid input;
- missing records;
- unavailable/unpublished data;
- ownership/access;
- response shape;
- relevant repository behavior.

Do not prescribe tests for behavior that the feature does not expose.

Follow existing FoodBackend test conventions rather than introducing a new testing framework or style.

## Phase 8 — Plan output

Return the following structure:

# Feature plan

## Goal

One concise description of the requested behavior.

## Existing implementation patterns

List the relevant existing classes/files and what pattern each demonstrates.

## Requirements and constraints

Separate explicit requirements from assumptions.

## Architectural decision

### Existing pattern

...

### Why it fits

...

### Alternatives considered

Only include realistic alternatives that were actually relevant.

### Why alternatives are rejected

...

### New abstraction required

`Yes` or `No`.

### Reason

...

## Implementation

### Create

- `path/to/File.java` — purpose and change.

### Modify

- `path/to/File.java` — exact responsibility/change.

### Do not modify

- `path/to/File.java` — why it should remain unchanged.

## Persistence

Describe:

- repository changes;
- query strategy;
- relationship loading;
- migration requirements;
- indexes if actually required.

## API

Describe:

- endpoint;
- request;
- response;
- validation;
- errors;
- compatibility.

## Transactions

Describe required transaction boundaries and why.

## Tests

List concrete test classes/methods or test scenarios following existing conventions.

## Risks

Only concrete risks supported by repository evidence or the requirements.

## Open questions

Only unresolved decisions that genuinely require clarification.

## Implementation order

Give a short, dependency-aware sequence of changes.

## Final planning rules

Before finalizing the plan verify:

1. Existing analogous code was inspected.
2. The plan follows the established FoodBackend pattern unless a concrete reason requires deviation.
3. No unnecessary abstraction was introduced.
4. No unrelated refactoring is included.
5. API and error behavior are not invented.
6. JPA relationship loading and transaction boundaries were considered.
7. Tests cover the requested behavior.
8. Every proposed file change has a concrete reason.
9. The plan contains enough detail for another developer to implement it without rediscovering the architecture.
10. No code was modified during planning.