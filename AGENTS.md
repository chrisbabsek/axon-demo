# Repository Guidelines

## Project Structure & Module Organization

- `src/main/kotlin/de/...` hosts Axon aggregates, command handlers, and Spring Boot configuration.
- `src/main/resources/application.yml` stores Spring profiles; `schema.sql` seeds H2/Postgres.
- `src/test/kotlin/...` mirrors the main package for JUnit 5 specs; keep fixtures beside the code they cover.
- `rest/*.http` offers HTTP samples for aggregate commands; update when endpoints change.
- `uml/*.puml` captures event-flow diagrams; edit with PlantUML and commit sources only.
- `build/` is Gradle output; do not check it in.

## Build, Test, and Development Commands

- `./gradlew bootRun` starts the Spring Boot sample app with the default in-memory H2 configuration.
- `./gradlew test` runs the full JUnit suite (Axon aggregate fixture tests plus Spring slices).
- `./gradlew clean build` produces a verified application jar.
- `docker-compose up -d postgres` launches the Postgres service used by the production profile; run
  `docker-compose down` when finished.

## Coding Style & Naming Conventions

- Use four-space indentation, JetBrains formatter defaults, and idiomatic null-safety (`val` over `var`).
- Use `PascalCase` for classes and `camelCase` for functions/properties; backing fields for Axon identifiers end with
  `Id`.
- YAML keys stay kebab-case; continue using kotlin-logging `logger { }`.
- Keep packages cohesive (`domain`, `commands`, `events`, `query`); avoid cyclic Spring component scanning.

## Testing Guidelines

- Prefer JUnit 5 with `aggregateTestFixture` for aggregates and SpringMockK/Testcontainers for integrations.
- Name files with the `*Test.kt` suffix and describe behavior in backticked test names.
- Expand fixtures with `given/whenever/expect` scenarios covering event-sourcing edge cases before adding commands.
- Run `./gradlew test` before every push; if Postgres is required, ensure the compose service is up.

## Commit & Pull Request Guidelines

- Follow Conventional Commits (`feat:`, `fix:`, `chore:`) as evident in `git log`.
- Scope each commit to a single concern; include migrations, diagrams, and HTTP examples when they change.
- Pull requests outline user impact, testing, and any operational follow-up (schema or docker steps).
- Link tickets/issues in the description and attach screenshots or HTTP transcripts when API behavior changes.

## Environment & Tooling Tips

- Use Java 21 (Gradle toolchain is locked) and Kotlin 2.2.x; let Gradle manage dependencies via the Axon BOM.
- IntelliJ HTTP Client opens `rest/*.http`; PlantUML support renders the sequence diagrams in `uml/`.
- Default `application.yml` points to Postgres on `localhost:5432`; override `SPRING_DATASOURCE_*` variables if you run
  a different instance.
