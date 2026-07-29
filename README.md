# NnmBot

Warframe QQ Bot - Modular Architecture

## Modules

| Module | Description |
|--------|-------------|
| nnm-common | Common utilities, exceptions, HTTP client, API response |
| nnm-core | Bot core: plugin SPI, message dispatch, events, executor config |
| nnm-render | Image rendering engine (SPI pluggable: openhtmltopdf / Playwright) |
| nnm-warframe | Warframe domain: world state, market, change detection |
| nnm-scheduler | Scheduled tasks: polling, cleanup, notifications |
| nnm-admin | Admin REST API: authentication, bot management |
| nnm-app | Application entry point, assembles all modules |

## Requirements

- Java 21+
- Maven 3.9+
- PostgreSQL (development and production)
- H2 (automated tests only)

## Quick Start

```bash
# Development (localhost PostgreSQL + Flyway)
export DB_HOST=localhost DB_PORT=5432 DB_NAME=nnmbot
export DB_SCHEMA=nnmbot DB_USERNAME=nnmbot DB_PASSWORD=nnmbot
./scripts/run-dev.sh

# Production
export DB_HOST=localhost DB_PORT=5432 DB_NAME=nnmbot
export DB_SCHEMA=nnmbot DB_USERNAME=nnmbot DB_PASSWORD=your-password
java -jar nnm-app/target/NnmBot.jar --spring.profiles.active=prod
```

Database schema changes are managed by Flyway. Hibernate validates entity mappings
at startup and does not update production or development schemas automatically.
Application tables use the `nnmbot` schema; Flyway keeps its history table in `public`.

The development script compiles all reactor modules first, then starts `nnm-app`
with each module's current `target/classes`. This avoids loading stale internal
module JARs from the local Maven repository and does not create a production package.

## Render Engine

The render engine is auto-selected based on environment:
- **openhtmltopdf** (default): Lightweight, CSS3 support, zero external dependencies
- **Playwright**: Full Chrome rendering, set `render.engine=playwright` in config

Override via `application.yaml`:
```yaml
render:
  engine: auto  # auto | openhtmltopdf | playwright
```

Trusted external render engines can implement `RenderEngineProvider` and register through
Java `ServiceLoader`. External JAR discovery is disabled by default; see
`docs/项目模块与目录职责.md` for the plugin contract and security boundary.

For the complete backend directory and file responsibility map, see
`docs/项目模块与目录职责.md`.

## Architecture

```
Spring Boot 4.0 + Spring Modulith
├── Event-driven inter-module communication
├── Plugin SPI for bot commands
├── Strategy pattern for change detection
├── Virtual threads (Java 21) for async processing
└── Resilience4j for external API fault tolerance
```
