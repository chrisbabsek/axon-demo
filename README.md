# axon-demo

Demo project that showcases an event-sourced bank account domain built with Kotlin, Spring Boot 3, and Axon Framework 4.
It exposes a REST API, maintains a Postgres-backed read model, and documents the event flow with PlantUML.

## Features

- Event-sourced `BankAccountAggregate` covering account lifecycle: open, deposit, transfer, failure compensation, close.
- Command and event handlers wired through Axon processing groups for separation of responsibility.
- Postgres JSONB projection (`bank_account_projection`) served via REST endpoints.
- Developer assets: HTTP request collection under `rest/` and PlantUML diagrams in `uml/`.

## Project Layout

- `src/main/kotlin/de/babsek/demo/axontesting` – Spring Boot application, Axon aggregate, command handlers, projections,
  and REST layer.
- `src/main/resources` – `application.yml` (datasource + Axon config) and `schema.sql` for bootstrapping the demo
  schema.
- `src/test/kotlin` – JUnit 5 specs using Axon aggregate fixtures and Spring test support.
- `rest/*.http` – IntelliJ HTTP client files that exercise the API locally.
- `uml/*.puml` – PlantUML sequence diagrams capturing event ordering scenarios.

## Prerequisites

- JDK 21 (Gradle toolchain enforces it).
- Docker (optional) to run Postgres locally.
- `./gradlew` wrapper handles all build tasks; no manual Gradle install required.

## Quick Start

1. Start the database: `docker-compose up -d postgres` (default credentials match `application.yml`).
2. Launch the app: `./gradlew bootRun` (listens on `http://localhost:8080`).
3. Exercise the API using the provided HTTP scripts or a REST client; for example, run `rest/open bank account.http` in
   IntelliJ to create an account.
4. Stop services with `CTRL+C` and `docker-compose down` when finished.

## REST API

- `GET /bankaccounts` – List all accounts with current balances and transaction history.
- `GET /bankaccounts/{bankAccountId}` – Retrieve a single account projection.
- `POST /bankaccounts` – Create an account (`bankAccountId`, `ownerName`).
- `DELETE /bankaccounts/{bankAccountId}` – Close an account (requires zero balance).
- `POST /bankaccounts/{bankAccountId}/payments` – Deposit funds (`amount`).
- `POST /transfers` – Initiate a transfer between accounts (`originBankAccountId`, `destinationBankAccountId`, `amount`,
  `reason`).

## Testing & Tooling

- Run the full test suite with `./gradlew test`; aggregate fixture tests do not require Postgres.
- Optional: visualize event flow diagrams using any PlantUML plugin pointed at `uml/*.puml`.
- Logging is provided via `kotlin-logging`; adjust levels in `application.yml` as needed.

## Contributing

See `AGENTS.md` for detailed contributor guidelines, coding conventions, and workflow expectations.
