# NnmBot

Warframe QQ Bot - Modular Architecture

## 开发入口

新开发会话先阅读：

1. [`AGENTS.md`](AGENTS.md)：仓库级开发约定和运行边界。
2. [`docs/项目上下文与续作指南.md`](docs/项目上下文与续作指南.md)：项目目标、前后端仓库、
   Warframe 参考项目、数据表与 JSON 来源、当前实现和验证边界。
3. [`docs/README.md`](docs/README.md)：现行文档索引和按任务选择的阅读顺序。

Warframe 社区数据的配置入口已经统一为
`nnm.warframe.community-data.datasets`。同一数据集下可以登记一个或多个来源；
`url`/`urls` 只表示一个来源的地址或故障转移地址，不再使用独立的
`dataset-urls`、`dataset-manifests` 两块配置。

## Modules

| Module | Description |
|--------|-------------|
| nnm-common | Common utilities, exceptions, HTTP client, API response |
| nnm-core | Bot core: plugin SPI, message dispatch, events, executor config |
| nnm-render | Image rendering engine (SPI pluggable: openhtmltopdf / Playwright) |
| nnm-warframe | Warframe domain: world state, market, change detection |
| nnm-admin | Admin REST API: authentication, bot management |
| nnm-app | Application entry point, assembles all modules |

## Requirements

- Java 21+
- Maven 3.9+
- PostgreSQL (development and production)
- Redis (shared sessions, RDT queues, schedules and connection ownership)
- H2 (automated tests only)

## Quick Start

```bash
# Configure PostgreSQL and Redis, and install the local RDT dependency first.
# See docs/RDT接入与多实例运行.md.
./scripts/run-dev.sh
```

Robot instances and protocol connection settings are created in the admin UI and stored in
PostgreSQL. YAML files are not a robot-instance configuration source.

Distributed mode is enabled in `application-distributed.yml`. Each concurrent application
node needs a unique, stable `INSTANCE_ID`; the local default is `local-1`.
See [RDT接入与多实例运行.md](docs/RDT接入与多实例运行.md) for module boundaries,
queue semantics, shared storage requirements and two-node verification.

Database schemas are managed manually during the current development phase. Execute
`docs/sql/init.sql` against PostgreSQL when creating or rebuilding a local database.
Hibernate validates entity mappings at startup and does not create or update tables
automatically. Application tables use the `nnmbot` schema.

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

The `FOR ROLE` value must be the role that will execute the SQL scripts; omit it only when
running the command as that same role.

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
`docs/项目架构与开发约定.md` for the plugin contract and security boundary.

For the current backend module and responsibility map, see
`docs/项目架构与开发约定.md`.

## Architecture

```
Spring Boot 4.0 + Spring Modulith
├── Event-driven inter-module communication
├── Plugin SPI for bot commands
├── Strategy pattern for change detection
├── Virtual threads (Java 21) for async processing
└── Resilience4j for external API fault tolerance
```
