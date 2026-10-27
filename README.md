# NnmBot

Warframe QQ Bot - Modular Architecture

## 开发入口

新开发会话先阅读：

1. [`AGENTS.md`](AGENTS.md)：仓库级开发约定和运行边界。
2. [`docs/项目架构与开发约定.md`](docs/项目架构与开发约定.md)：项目目标、前后端仓库、
   Warframe 参考项目、数据表与 JSON 来源、当前实现和验证边界。
3. [`docs/README.md`](docs/README.md)：现行文档索引和按任务选择的阅读顺序。

当前命令、表及 JSON 来源的完整清单见
[`docs/reference/命令与数据目录.md`](docs/reference/命令与数据目录.md)；
命令用途与返回语义见 [`docs/Warframe数据与命令实现.md`](docs/Warframe数据与命令实现.md)。
QQ Markdown 关联按钮目前仅有
[`设计方案`](docs/命令模板数据约定.md)，尚未修改运行时代码。

动态 WorldState、仲裁排期和周紫卡数据按运行时刷新，在每个 JVM 内存中持有快照；人工白名单只保留物品别名、术语/黑话、仲裁精华和紫卡自定义别名，持久化到独立 `wf_community_*` 表。

## 配置分层

运行配置统一放在 `nnm-app/src/main/resources/`，仅保留三个 YAML 文件：

| 文件 | 职责 |
| --- | --- |
| [`application.yml`](nnm-app/src/main/resources/application.yml) | 基础设施：数据库、Redis、Sa-Token、执行资源、连接凭据及 RDT 等 starter；通常只随机器或环境调整。 |
| [`application-system.yml`](nnm-app/src/main/resources/application-system.yml) | 系统业务差异：机器人渠道、JMComic、开发跨域与管理员初始化；缓存、路由和分布式协调未覆盖时采用属性类默认值。 |
| [`application-warframe.yml`](nnm-app/src/main/resources/application-warframe.yml) | Warframe 业务差异；由各业务配置属性分别绑定论坛、拉取、查询和缓存。 |

`application.yml` 显式导入另两个文件，正常启动不需要额外激活 profile。
Warframe 的官方、社区、Market、仲裁、周交易和图片索引拉取统一位于
`nnm.warframe.pull.*`；论坛、数据库查询和业务缓存不混入这一层。
配置默认值由各 `@ConfigurationProperties` 属性类型维护，YAML 只覆盖差异；RDT 参数不引用业务属性。
NnmBot 始终使用 Redis 与多实例协调，包括仅运行一个实例。租约、超时和保留参数使用 `nnm.cluster.*`，没有本地运行开关。
需要离线测试模式时分别关闭两者；RDT 节点身份租约与机器人 owner lease 也分别管理。

Warframe 属性由 `WarframePropertiesConfiguration` 集中注册，各业务域的属性类分别绑定并校验：

- `nnm.warframe.forum.storage.*`：论坛附件，替代原 `forum-storage.*`。
- `nnm.warframe.pull.*`：官方、社区、Market、仲裁与运行时索引拉取。
- `nnm.warframe.query.cycles.*`、`query.arbitration.*`、`query.world-state.*`：替代原平级查询配置。
- `nnm.warframe.cache.*`：JVM 本地业务缓存；WorldState 与仲裁等动态快照由各 JVM 自行保存在内存。

外部 YAML 或启动参数应同步使用新前缀；既有显式 `NNM_WARFRAME_*` 环境变量继续可用。
外部来源、刷新频率和功能开关保持既有行为；对象存储的开发缺省地址统一为 `http://localhost:9000`。

`nnm.bot.channels.onebot` 保存 OneBot 共用的协议与传输参数。QQ 官方机器人按实例配置，
凭据和网关参数保存在管理端的 `bot_qq_official_config` 表中；YAML 目前没有
`nnm.bot.channels.qq-official` 属性绑定，未实现的配置项不会生效。
图片模板只绑定命令展示 DTO；图标 URL、背景 URL 和文字按真实字段名取值，
不再使用 `render.template-assets` 或渲染器注入的全局图片变量。
字段清单、多样式模板及缺图处理见 [`命令模板数据约定`](docs/命令模板数据约定.md)。
`nnm.executor` 是本机共享线程池、调度器和并发背压预算，属于基础设施配置；
RDT 执行策略和 Warframe 拉取周期等业务策略分别放在对应配置文件。

### 执行资源预算

以下是 4GB 机器、数据库连接池上限 5 的初始配置，不代表容量压测结论。

| 入口 | 当前预算 | 执行方式 |
| --- | --- | --- |
| `nnm.executor.task` | 核心/最大线程 2/2，队列 100 | Spring `ThreadPoolTaskExecutor`，平台线程。 |
| `nnm.executor.application-concurrency` | 64 | `SimpleAsyncTaskExecutor`，按任务创建虚拟线程，提交入口背压。 |
| `nnm.executor.discovery-concurrency` / `connection-concurrency` | 各 4 | 独立虚拟线程执行器，不与业务消息互相占用许可。 |
| `nnm.executor.scheduler` | 2 | Spring 平台线程调度器。 |
| `nnm.jmcomic.download-threads` / `max-concurrent-tasks` | 4 / 2 | 前者是图片下载线程池；后者是完整下载及 PDF 处理流程的信号量。 |
| `shiro.task-pool` | 核心/最大线程 2/4，队列 100 | SDK 自建 Spring 线程池；当前全局虚拟线程开关使它使用虚拟线程工厂。 |

通用异步和定时任务统一使用 `ExecutorConfig` 管理的有界执行资源；Warframe 不再保留
旧社区下载/解析专属执行器，也不向 `nnm.executor` 注册业务专属属性。
`CallerRuns` 在池/队列饱和时让提交者执行任务，因此池线程数不是严格的全局业务并发上限；
需要严格限制某项业务资源时，在完整资源生命周期设置独立许可。
修改容量时还应评估虚拟任务持有的对象、JM 图片解码和 Chromium 内存，而不只计算线程栈。

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

Distributed business mode is enabled in `application-system.yml`; RDT infrastructure is configured independently in `application.yml`. Each concurrent application
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

Override via `application.yml`:
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
