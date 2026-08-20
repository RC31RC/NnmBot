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
# Configure PostgreSQL in nnm-app/src/main/resources/application.yml
# Configure robot channels in nnm-app/src/main/resources/application-bot.yml
./scripts/run-dev.sh
```

Database schema changes are managed by Flyway. Hibernate validates entity mappings
at startup and does not update production or development schemas automatically.
Application tables use the `nnmbot` schema; Flyway keeps its history table in `public`.

### PostgreSQL permissions

The role in `spring.datasource.username` must be able to use the application schema and modify
the RBAC data written during startup. If `nnmbot` was created by a different
database role, run the following once as the schema owner or a PostgreSQL
administrator, replacing `nnmbot` with the actual application role when needed:

```sql
GRANT CONNECT ON DATABASE nnmbot TO nnmbot;
GRANT USAGE, CREATE ON SCHEMA nnmbot TO nnmbot;
GRANT SELECT, INSERT, UPDATE, DELETE, REFERENCES, TRIGGER
    ON ALL TABLES IN SCHEMA nnmbot TO nnmbot;
GRANT USAGE, SELECT, UPDATE
    ON ALL SEQUENCES IN SCHEMA nnmbot TO nnmbot;
ALTER DEFAULT PRIVILEGES FOR ROLE nnmbot IN SCHEMA nnmbot
    GRANT SELECT, INSERT, UPDATE, DELETE, REFERENCES, TRIGGER
    ON TABLES TO nnmbot;
ALTER DEFAULT PRIVILEGES FOR ROLE nnmbot IN SCHEMA nnmbot
    GRANT USAGE, SELECT, UPDATE ON SEQUENCES TO nnmbot;
```

The `FOR ROLE` value must be the role that will execute future Flyway
migrations; omit it only when running the command as that same role.

`permission denied for schema nnmbot` means the connection reached PostgreSQL
successfully but the configured role is missing at least the schema `USAGE`
privilege. Restarting the application does not change PostgreSQL grants.

The development script compiles all reactor modules first, then starts `nnm-app`
with each module's current `target/classes`. This avoids loading stale internal
module JARs from the local Maven repository and does not create a production package.

## Render Engine

The render engine is auto-selected based on environment:
- **openhtmltopdf** (default): Lightweight, CSS3 support, zero external dependencies
- **Playwright**: Full Chrome rendering, set `render.engine=playwright` in config

Override via `application-bot.yml`:
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
