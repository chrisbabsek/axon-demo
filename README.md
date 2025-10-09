# Bank Service with OpenCQRS

This project implements an event-sourced bank account domain using [OpenCQRS](https://github.com/open-cqrs/opencqrs) and the [EventSourcingDB](https://www.eventsourcingdb.io) from TheNativeWeb.
The write model runs on top of EventSourcingDB while the read model is materialized in PostgreSQL and exposed through a REST API built with Spring Boot and Kotlin.

## Features

- OpenCQRS-based command model with event sourcing support for opening accounts, deposits, transfers, compensating failures and closures.
- Projection pipeline persisting bank account snapshots in PostgreSQL (JSONB transaction history) and exposing them via REST.
- Automated command-side tests powered by the OpenCQRS testing fixture.
- Production ready Docker setup bundling the Spring Boot application, EventSourcingDB and PostgreSQL.

## Project Layout

- `src/main/kotlin` – Spring Boot application, OpenCQRS command handlers, event handlers, REST controllers and projection code.
- `src/main/resources/application.yml` – Application configuration (EventSourcingDB URI/token, PostgreSQL datasource, JSON settings).
- `src/test/kotlin` – JUnit 5 tests relying on `CommandHandlingTestFixture` from OpenCQRS.
- `rest/*.http` – HTTP requests for IntelliJ IDEA’s HTTP client to exercise the API locally.
- `docker-compose.yml` – Complete runtime environment with EventSourcingDB, PostgreSQL and the application container.

## Prerequisites

- JDK 21 (configured via Gradle toolchain)
  - The repository omits the `gradle-wrapper.jar` binary. The wrapper scripts fetch the configured Gradle
    distribution on first use and extract the wrapper jar automatically (requires `curl`/`wget` and the JDK tools).
- Docker (for running EventSourcingDB and PostgreSQL via `docker-compose`)
- Optional: [EventSourcingDB account](https://www.eventsourcingdb.io) if you want to replace the default demo token

## Running Locally

1. **Start infrastructure**
   ```bash
   docker compose up -d esdb postgres
   ```
2. **Launch the Spring Boot application**
   ```bash
   ./gradlew bootRun
   ```
   By default the app expects EventSourcingDB on `http://localhost:3000` with API token `secret` and PostgreSQL on `localhost:5432/postgres` (user `postgres`, password `secret`). Adjust `application.yml` or environment variables as required.
3. **Exercise the API** – Use the `rest/*.http` files or a REST client:
   ```bash
   curl -X POST http://localhost:8080/bankaccounts \
        -H 'Content-Type: application/json' \
        -d '{"bankAccountId":"001","ownerName":"Ted Tester"}'
   ```
4. **Shut down infrastructure**
   ```bash
   docker compose down
   ```

## Running the Full Docker Stack

To build and run everything, including the Spring Boot service inside a container, execute:

```bash
docker compose up --build
```

The compose file provisions:
- `esdb` – EventSourcingDB (port `3000`, UI enabled)
- `postgres` – PostgreSQL with schema initialization via `schema.sql`
- `app` – Spring Boot application (`http://localhost:8080`)

Configuration can be adjusted via environment variables inside `docker-compose.yml` (e.g. change the EventSourcingDB token or PostgreSQL credentials).

## Testing

Run the command-side specification suite:

```bash
./gradlew test
```

The tests use OpenCQRS’ fixture to replay events and validate command behavior without connecting to an external EventSourcingDB instance.

## Useful Links

- [OpenCQRS GitHub](https://github.com/open-cqrs/opencqrs)
- [EventSourcingDB Documentation](https://docs.eventsourcingdb.io)
- [EventSourcingDB Docker Hub](https://hub.docker.com/r/thenativeweb/eventsourcingdb)

