# OpenCQRS Bank Account Demo

This project showcases an event-sourced bank account domain implemented with Kotlin, Spring Boot 3, the new
[OpenCQRS](https://github.com/thenativeweb/opencqrs) framework from Digital Frontiers, and the
[EventSourcingDB](https://www.eventsourcingdb.io) event store by The Native Web. It exposes a REST API, projects data
into
Postgres, and documents event flows with PlantUML.

## Features

- OpenCQRS command handling with EventSourcingDB-backed streams covering the full account lifecycle (open, deposit,
  transfer, failure compensation, close).
- Event handlers organised in dedicated processing groups with JDBC-based progress tracking to keep projections
  idempotent across restarts.
- Postgres JSONB read model (`bank_account_projection`) served via REST endpoints.
- Developer assets: HTTP request collection under `rest/` and PlantUML diagrams in `uml/`.

## Project Layout

- `src/main/kotlin/de/babsek/demo/axontesting` – Spring Boot application, OpenCQRS command handlers, event handlers,
  projections, and REST layer.
- `src/main/resources` – `application.yml` (datasource, EventSourcingDB, and OpenCQRS tuning) plus `schema.sql` for
  bootstrapping projection and progress tables.
- `src/test/kotlin` – JUnit 5 specs using `CommandHandlingTestFixture` from `com.opencqrs:framework-test`.
- `rest/*.http` – IntelliJ HTTP client files that exercise the API.
- `uml/*.puml` – PlantUML sequence diagrams capturing event ordering scenarios.

## Prerequisites

- JDK 21 (Gradle toolchain enforces it).
- Docker (required) to run EventSourcingDB and Postgres alongside the application.
- `./gradlew` wrapper handles all build tasks; no local Gradle installation needed.

## Quick Start

1. Build and start the full stack:
   ```bash
   docker compose up --build
   ```
   This launches EventSourcingDB on `http://localhost:3000`, Postgres on `localhost:5432`, and the Spring Boot service
   on
   `http://localhost:8080`.
2. Exercise the API using the provided HTTP scripts (e.g. run `rest/open bank account.http` in IntelliJ) or any REST
   client.
3. Shut down everything with:
   ```bash
   docker compose down
   ```

## REST API

- `GET /bankaccounts` – List all accounts with current balances and transaction history.
- `GET /bankaccounts/{bankAccountId}` – Retrieve a single account projection.
- `POST /bankaccounts` – Create an account (`bankAccountId`, `ownerName`).
- `DELETE /bankaccounts/{bankAccountId}` – Close an account (requires zero balance).
- `POST /bankaccounts/{bankAccountId}/payments` – Deposit funds (`amount`).
- `POST /transfers` – Initiate a transfer between accounts (`originBankAccountId`, `destinationBankAccountId`, `amount`,
  `reason`).

## Testing & Tooling

- Run the full test suite with `./gradlew test`; the OpenCQRS fixtures run fully in-memory.
- Optional: visualise the event flow diagrams using any PlantUML plugin against `uml/*.puml`.
- Logging is provided via `kotlin-logging`; adjust levels in `application.yml` as needed.

## Contributing

See `AGENTS.md` for detailed contributor guidelines, coding conventions, and workflow expectations.
