# RDT 接入与多实例运行

本文记录 NnmBot 当前代码的实现，原始方案在相邻仓库
`redis-distributed-toolkit/docs/NnmBot接入方案.md`。本次未改前端、未新增数据库表，
仍使用 JPA、手工 SQL、Sa-Token 与 cache2k。

## 1. 模块与目录

```text
nnm-common/common/task             任务接口、进度协议、响应 DTO
nnm-core/core
  config                          NnmBot 分布式业务参数和启动校验
  task                            后台任务权限/状态/SSE；声明式提交与执行锁
  channel/runtime                 机器人期望状态、账号 owner、状态快照、发现请求路由
  onebot/connection                WebSocket 生命周期与发送前归属检查
  qqofficial                      QQ 网关与 HTTP 发送前归属检查
  plugin/execution                平台消息去重和执行前归属检查
nnm-warframe/warframe/cache        共享 WorldState、刷新 Stream、本地索引失效广播
nnm-warframe/warframe/task         Warframe 业务调度任务，调用本模块 Service
nnm-admin/admin/service           跨节点运行状态查询
nnm-app                           依赖组装、Redis/Sa-Token 配置、完整应用测试
```

调度任务不单独划分 Maven 模块。任务属于哪个业务域，就放在该模块的 `task` 包；
例如 WorldState 轮询与仲裁图片预热属于 `nnm-warframe`。任务类只负责调度入口和任务编排，
业务处理放在所属模块的 Service。

这些路径省略了 `src/main/java/com/nnm/bot/`。`nnm-core` 直接依赖 RDT starter，
已移除独立的 `nnm-infrastructure` 模块。内存任务与 Redis 任务实现归 `core.task`，
接口仍在 `common.task`。其他项目引入 RDT 不需要复制 NnmBot 的接入层。

通用的 `RedisOwnerLeaseService` 已归 RDT core，稳定实例身份保护 `NodeIdentityLease`
和 Redis 客户端配置由 RDT starter 自动装配。后台任务权限、机器人启停期望、
平台消息去重键和 Warframe 数据规则是本项目的业务，不属于通用 starter。

### 声明式调用入口

| 能力 | 当前入口 |
| --- | --- |
| 分布式锁 | `AdminTaskCoordination.reserve/execute`、`SharedWorldStateStore.refresh` 的 `@RdtLock` |
| 分布式 ID | `DistributedForumIds.next` 编程式调用 `DistributedIdGenerator` |
| 共享快照/发现结果写入 | `SharedWorldStateStore.write`、`ScopeDiscoveryExchange.respond` 的编程式缓存写入 |
| 消息发送 | 刷新请求、快照广播、索引失效、发现请求使用 `MessageQueueTemplate` 编程式发送 |
| 消费与调度 | 保留 `@RdtQueueListener`、`@RdtScheduled` |

声明式方法必须经 Spring 代理从另一个 Bean 调用，同类自调用不会触发切面。
后台任务通过独立协调 Bean 划分短暂提交锁与工作线程执行锁，不能把异步提交方法整体加锁
后误认为锁覆盖了后台执行。跨回调的连接租约仍显式 acquire/renew/release。
Redis 状态读取无数据库回源，保留 `CacheService.get`，不伪装成缓存穿透加载器。
RDT 注解时长现在兼容 `200m`、`2s`、`PT200M` 和 `${...}` 配置占位符。

选择原则是：消费者、集群调度和方法边界上的互斥使用声明式注解，使类的基础设施身份
直接可见；分布式 ID 获取、消息投递和跨生命周期租约使用编程式 API，使调用点能够明确
看到资源分配、发送时机和释放责任。`@RdtPublish` 仍可用于稳定的“业务方法成功后发布”
场景，但 NnmBot 当前优先使用编程式发送，避免把消息副作用隐藏在普通业务方法返回值之后。

## 2. 本地依赖与启动

本项目仅使用运行时依赖 `io.github.rdt:rdt-spring-boot-starter:1.0.0-SNAPSHOT`。
RDT 管理端是独立的 `rdt-console` 应用，不会随 NnmBot 启动。版本集中于根 POM 的
`rdt.version`。

首次接入或修改 RDT 后，在 RDT 仓库执行以下命令，将运行时依赖模块写入本机 Maven
仓库。这些命令不构建 NnmBot 的生产包，也不启动 RDT 示例应用：

```sh
cd /Users/ren/RcGit/redis-distributed-toolkit
sh mvnw -N install:install
sh mvnw -pl rdt-core,rdt-spring-boot-starter \
  test jar:jar install:install -DskipTests
```

父模块是 POM，不能对包含父模块的 reactor 执行 `jar:jar install:install`，
否则可能错误地将 JAR 内容安装到父 POM 的位置。

启动前准备本地 PostgreSQL、Redis，按既有约定手工执行 `docs/sql/init.sql`；
已有数据库不需要为本次 RDT 接入新增表。然后：

```sh
cd /Users/ren/RcGit/NnmBot
INSTANCE_ID=local-1 ./scripts/run-dev.sh
```

默认后端为 `localhost:8080`。启动脚本继续使用当前各模块 `target/classes`，
直接加载 core 等现有模块，不再加载 infrastructure，不需要构建 NnmBot 可执行 JAR。

另一个终端启动第二个节点：

```sh
cd /Users/ren/RcGit/NnmBot
INSTANCE_ID=local-2 SERVER_PORT=8081 ./scripts/run-dev.sh
```

两节点必须使用相同代码、数据库、Redis database 和 `rdt.namespace`。
`INSTANCE_ID` 是应用节点身份，不是数据库里的机器人实例编码。
它必须同时满足“并行节点不重复”和“同一节点重启后不变”。
重复身份会在 RDT 消费者启动前被拒绝；异常退出后可能需要等待旧身份租约过期。
滚动替换同一身份时，先停止旧进程，不要同时运行两份。

## 3. 配置

配置集中在 `nnm-app/src/main/resources/application-distributed.yml`：

| 配置/环境变量 | 默认 | 含义 |
| --- | --- | --- |
| `nnm.distributed.enabled` | `true` | 启用业务共享状态、owner 协调与 RDT 调度 |
| `NNM_REDIS_HOST` / `NNM_REDIS_PORT` | `127.0.0.1` / `6379` | 本地 Redis 地址 |
| `NNM_REDIS_DATABASE` | `0` | 所有同集群节点使用相同 database |
| `NNM_REDIS_PASSWORD` | 空 | 不需要认证时不发送空密码 AUTH |
| `rdt.namespace` | `nnmbot:${APP_ENV:dev}` | RDT Key 命名空间 |
| `INSTANCE_ID` | `local-1` | 稳定、唯一应用节点身份 |
| `nnm.distributed.owner-lease` | `30s` | 账号和节点身份租约 |
| `nnm.distributed.reconcile-interval` | `10s` | 本地连接协调和租约续期周期 |
| `nnm.distributed.task-timeout` | `30m` | 后台任务最长观测执行时间 |
| `nnm.distributed.task-retention` | `2h` | 任务状态额外保留时间 |
| `nnm.distributed.task-poll-interval` | `2s` | 跨节点任务 SSE 采样间隔 |
| `nnm.distributed.max-task-subscriptions` | `128` | 每节点后台任务 SSE 订阅上限 |
| `nnm.distributed.message-dedup-retention` | `1d` | 平台消息去重窗口 |
| `nnm.distributed.world-state-retention` | `2d` | 当前世界状态 Redis TTL |
| `nnm.distributed.discovery-timeout` | `60s` | 非 owner 群组发现等待期限 |

租约必须至少为协调间隔的三倍。所有 Duration 参数启动时校验为正数。
Redis 不可用时不会静默退回本地连接或本地锁。

`rdt.client` 默认设置工作线程 2、Netty 线程 2、普通连接上限 12、
订阅连接上限 4、最小空闲各 1。数据库连接池仍为每节点 5。
`rdt.schedule.workers=2`；不启用远程 Java 任务。
这些是每个 JVM 的预算，增加应用节点会成倍增加总连接数、缓存和渲染占用。
两台 JVM 不应各自按“独占 4GB”设置堆内存。

旧 `nnm.redis-client.*` 参数已迁入 `rdt.client.*`。稳定节点保护通过
`rdt.instance.lease` / `rdt.instance.renew-interval` 配置，本项目引用账号租约的
30s/10s 参数。节点身份续租是 RDT 自有的单个周期任务，不需要业务配置调度器。

`nnm.distributed.enabled=false` 只切回单节点业务协调、内存任务和本地轮询；
Sa-Token 的 Redis 会话依赖仍保留，不能将该开关理解为彻底无 Redis 运行。
普通自动化测试通过测试专用 auto-configuration 排除项隔离 Redis。
不要混跑 distributed=true/false 的业务节点。

## 4. 机器人连接归属

管理端启停不再直接操作收到 HTTP 请求的那个进程：

1. 启停请求写入共享的机器人期望状态；在事务中调用时，提交后才发布。
2. 各节点每隔协调周期读取数据库配置和期望状态。
3. 按“平台 + 机器人账号”竞争租约，只有 owner 建立 OneBot/QQ 连接。
4. 租约包含 owner、递增 fencing version 和随机 acquisition token。
5. 续期和释放通过 Redis Lua 比较完整 token，旧 owner 不能续期或删除新租约。
6. 续期失败、配置改变、机器人禁用或删除时停止本地连接并注销旧注册。
7. 每个连接捕获自己的 token。重新获得同一账号不意味着旧 socket 恢复发送资格。
8. 新节点在旧租约释放或过期后接管。一般还需等待一个协调周期及平台重连时间。

Redis/数据库不确定时按 fail-closed 处理。OneBot/QQ 的回调、发送入口以及命令
执行入口检查归属；节点身份续期失败后停止该节点 RDT 注解消费者和调度，需重启恢复。
身份丢失日志必须纳入运行监控。

启停接口表达的是“已提交期望状态”，不是平台握手已完成。
管理端通过共享状态和 SSE 观察最终 CONNECTED/STOPPED 等状态。
状态页面的集群模式不订阅旧节点的本地 registry 事件，避免旧 STOPPED 覆盖新 owner。
账号尚无 owner 时展示 STOPPED/DISABLED，不能把它当作启动完成确认。
没有显式启动过的机器人不会仅因数据库 `enabled=true` 就自动连接。

fencing version 是本应用的连接身份校验，不是 QQ/OneBot 平台认可的 fencing
协议。归属检查与远端网络副作用之间仍存在时间窗口；已发出的请求不能撤销。
不能宣称网络分区、长 GC 停顿下的外部副作用具备严格 exactly-once。

## 5. 定时任务与队列

| 业务 | RDT ID / destination | 模式 / group |
| --- | --- | --- |
| 世界状态轮询 | `warframe-worldstate-poll-v1` | 集群 Cron 调度，负责投递刷新请求 |
| 世界状态刷新 | `worldstate-refresh-v1` | STREAM / `worldstate-workers-v1` |
| 世界状态更新通知 | `warframe-worldstate-updated-v1` | RELIABLE_BROADCAST / `worldstate-local-v1` |
| 本地物品索引失效 | `warframe-item-index-invalidated-v1` | RELIABLE_BROADCAST / `warframe-item-index-v1` |
| OneBot 群组发现 | `onebot-discovery-v1` | RELIABLE_BROADCAST / `onebot-discovery-owners-v1` |

### 5.1 任务调度方式

RDT 的 `@RdtScheduled` 支持 Spring 风格的五字段或六字段 cron，也支持
`fixedRate`、`fixedDelay` 和 `initialDelay`。调度方式按任务边界选择：

- `ClusterBotRuntime.reconcile()` 是当前实例独有资源的协调，不是 RDT 任务，使用 Spring
  `@Scheduled(fixedDelayString = ...)`，保证每个实例都能续租和管理自己持有的 WebSocket。
  Redis owner lease 仍负责同一个机器人连接的集群归属竞争。
- `warframe-worldstate-poll-v1` 是集群共享任务，使用 RDT 调度。
  它负责在集群中选择一个实例触发业务刷新，业务服务内部仍可根据数据源状态
  进行退避和幂等判断。

仲裁排期从管理端统一社区数据集 `arbitration_schedule` 主动刷新，进入 Redis 分片预览后
由管理员采纳；当前代码不再注册旧的 `warframe-arbitration-refresh-v1` 自动写库任务。
Redis 控制台中的旧任务记录不代表当前应用仍在调度，验收时应避免与仍运行的旧版本节点混用。

需要严格按自然时间触发的任务可以使用 cron，例如：

```java
@Component
@RdtScheduled(
        id = "daily-summary-v1",
        displayName = "每日摘要生成",
        cron = "0 0 3 * * *",
        zone = "Asia/Shanghai"
)
public class DailySummaryScheduledTask
        implements RdtScheduledTask {

    @Override
    public void execute() {
        // 任务实现
    }
}
```

cron 与 `fixedRate`、`fixedDelay` 只能选择一种。RDT 负责多实例竞争、触发记录和失败
状态；业务代码仍负责外部数据的幂等、失败退避和结果校验。`@RdtScheduled` 是类级
身份注解，任务类必须同时是 Spring Bean 并实现 `RdtScheduledTask`；一个类只定义一个任务。

### 5.2 集群任务与每节点任务

凡是操作 Redis、数据库或外部接口，并且同一时刻只需要一个实例执行的工作，优先使用
`@RdtScheduled`。凡是操作当前 JVM 独有资源的工作，必须每个实例执行，例如：

- 本地图片或渲染结果预热；
- 当前实例临时目录清理；
- 只存在于当前 JVM 的 cache2k 派生缓存维护。

这些任务继续使用 Spring `@Scheduled`。否则只让一个节点执行会导致其他节点的本地文件
和本地缓存永远不被维护。

STREAM：同组节点竞争消费，业务异常由 RDT 重试，超过 `max-attempts=5` 后进入死信。
可见性超时默认 2 分钟。调度“成功”仅代表投递成功，真正刷新结果需查看队列和业务任务。

RELIABLE_BROADCAST：每个稳定实例有自己的消费进度，重启后可补消费。
广播只携带更新提示或发现请求，不发送机器人凭证、Java 回调或整个历史 WorldState。
世界状态广播消费时重新读取当前快照，避免离线补消费导致旧状态覆盖新状态。

世界状态的自动刷新和管理端刷新共用 watchdog 分布式写锁，
完整快照与原始 JSON 一起替换。上游请求失败保留最后成功快照，但向调用方抛错，
使 Stream 重试而不是错误确认成功。

cache2k 仍是 JVM 业务缓存。索引更新在数据库提交后可靠广播失效；
数据库提交与 Redis 发布并非一个原子事务，崩溃窗口可能漏发。
物品索引现有 TTL（默认 8 小时，可调整 `nnm.warframe.cache.item-resolution-index`）
提供最终刷新界限；严格实时一致需要后续增加事务 outbox，当前未实现。

### 5.3 消费者编写模板

消费者必须是 Spring Bean，在类上声明 `@RdtQueueListener`，并实现
`RdtMessageConsumer<T>`。一个消费者类只负责一个订阅，消息目的地、消费模式和消费组
必须明确：

```java
@Component
@RdtQueueListener(
        value = "example-refresh-v1",
        displayName = "示例数据刷新",
        mode = QueueMode.STREAM,
        group = "example-workers-v1"
)
public class ExampleRefreshMessageConsumer
        implements RdtMessageConsumer<String> {

    @Override
    public void consume(String command) {
        if (!"latest".equals(command)) {
            throw new IllegalArgumentException("Unsupported command: " + command);
        }
        // 成功返回表示 ACK；抛出异常表示需要重试。
    }
}
```

`STREAM` 的同一个 group 由多个实例共同消费，一条消息只由其中一个实例处理。
`RELIABLE_BROADCAST` 则要求每个实例都消费一次，适合刷新本地 cache2k：

```java
@Component
@RdtQueueListener(
        value = "example-cache-invalidated-v1",
        displayName = "示例缓存失效广播",
        mode = QueueMode.RELIABLE_BROADCAST,
        group = "example-cache-v1"
)
public class ExampleCacheInvalidatedMessageConsumer
        implements RdtMessageConsumer<String> {

    @Override
    public void consume(String cacheKey) {
        // 根据 key 删除或重建当前实例的本地缓存。
    }
}
```

消息发送使用编程式 API：

```java
messages.send("example-refresh-v1", "latest", QueueMode.STREAM);
messages.reliableBroadcast("example-cache-invalidated-v1", "warframe-item-index");
```

消费者泛型只接收可序列化的轻量载荷。需要消息 ID 和创建时间时可使用
`RdtMessageConsumer<Message>` 并从信封解析业务对象。不要把大 JSON、图片、凭证或 Java 对象引用直接
放入消息；跨实例共享的大数据应先写入 Redis/数据库，再广播 key、hash 或 version。
destination、group 和任务 ID 都是运行期协议标识，发布后不要随意改名，否则会形成新的
队列或消费进度。

### 展示名与历史记录

任务和消息路由继续使用原有稳定 ID，中文/英文展示名只用于 RDT 控制台：

| 类型 | 稳定 ID | 展示名 |
| --- | --- | --- |
| 定时任务 | `warframe-worldstate-poll-v1` | Warframe世界状态轮询 |
| 可靠广播 | `onebot-discovery-v1` | OneBot群组发现 |
| 可靠广播 | `warframe-worldstate-updated-v1` | Warframe世界状态更新广播 |
| 集群消费 | `worldstate-refresh-v1` | Warframe世界状态刷新 |
| 可靠广播 | `warframe-item-index-invalidated-v1` | Warframe物品索引失效广播 |

名称通过 `@RdtScheduled.displayName`、`@RdtQueueListener.displayName` 声明，
注册时写入 Redis，独立控制台读取。原 ID、消费组、发送代码和调度周期不变，
不会因为改文案而新建消费组、重新消费旧消息或重置任务调度进度。
升级 RDT 本地依赖后重启 NnmBot，并重启使用新版 core 的 Console；
前端开发模式刷新即可。历史数据没有名称时回退到 ID。多实例应使用同一份展示名配置。

`application-distributed.yml` 现在显式配置：

```yaml
rdt:
  schedule:
    result-ttl: ${NNM_RDT_SCHEDULE_RESULT_TTL:7d}
```

此次保留原有 7 天行为，只把继承的默认值明确写出，方便环境变量调整。按每条结果独立计时；
RUNNING 和完成时的写入都会重设该条记录 TTL。修改后需重启业务实例，仅影响之后的写入，
不会让任务定义过期，也不追溯改变已经保存的历史。

| 配置/记录 | 当前含义与保留行为 |
| --- | --- |
| `rdt.schedule.result-ttl` | RDT 定时任务执行结果，7 天；本次新增显式配置 |
| `nnm.distributed.task-retention` | 后台异步业务任务状态，配置 2 小时；`RedisAdminTaskService` 实际每次保存传入 `task-retention + task-timeout`，当前基准 TTL 为 2 小时 30 分钟 |
| `nnm.distributed.message-dedup-retention` | 平台入站消息的防重复处理标记，1 天，不是队列历史 |
| `nnm.distributed.world-state-retention` | 世界状态快照缓存，2 天，不是更新广播消息的 TTL |
| `nnm.distributed.discovery-timeout` | 群组发现的请求截止时间及结果缓存基准 TTL，60 秒；不自动删除对应 Stream 条目 |
| RDT Stream 原始消息 | ACK 不删除消息，目前不自动过期或裁剪 |
| RDT Redis 死信 | 普通 Map 持久化，目前不自动过期，人工确认后删除 |

通过 `CacheService` 写入的业务状态/快照/发现结果还会叠加缓存 TTL 正向抖动
（RDT 默认 `rdt.cache.jitter=0.1`，即最多额外约 10%）；定时执行结果不走这套缓存抖动。
Console“最近消息”不是每次消费执行历史，也没有通用的消息历史保留配置。
不要为了省内存直接让整个 Stream 过期，否则会同时删除未消费消息和消费组状态。
如需自动清理，应先定义离线实例恢复窗口及所有消费组确认水位，再独立实现安全裁剪。

本次核实并测试了上述边界，没有删除业务 Redis 数据、SQL 数据或现有历史记录。

### 为什么部分任务仍然每节点执行

原方案曾建议论坛临时文件清理也改为集群调度，但当前存储默认是本地目录。
只选一个节点清理会遗留其他节点文件；图片预热也只影响当前 JVM。
因此论坛临时文件清理、图片预热继续使用每节点 Spring 调度。
如果改用共享文件系统，需要再评估清理并发和文件保留策略。

## 6. 管理端后台任务

后台任务的 ID、权限、进度、结果、deadline 和序号共享于 Redis。
从节点 A 发起任务后，可在节点 B 查询同一 ID 或订阅 SSE。
同一任务类型通过短时提交锁复用活动任务 ID，执行期间另有 watchdog 锁防止重入。
实际业务仍使用原 `applicationTaskExecutor` 的有界执行器。

不将业务闭包序列化进 Redis。进程退出后不能在另一节点重建该闭包，因此本次实现的是
“跨节点观察 + 同类互斥”，不是所有后台任务都已迁成可恢复队列。
超过 deadline 显示 FAILED/TIMED_OUT；核对实际业务结果后人工重试。
deadline 不会强制撤销已发生的数据库或 HTTP 副作用。

SSE 按快照序号去重；这是最新状态采样，不保证逐条重放所有中间进度。
终态或客户端断开会清理轮询，任务权限不存在时拒绝查询，不降级为无权限检查。
TTL 在每次状态写入后按 `task-timeout + task-retention` 设置，不是长期审计记录。

## 7. 跨节点业务操作

### 群组发现

收到管理请求的节点不是账号 owner 时，通过可靠广播发送带到期时间的只读请求，
owner 使用自己的平台连接查询，将结果写入 Redis，原请求节点有界等待结果。
依然只有管理员主动点击才发现群组，不自动创建作用域或系统用户。
接管窗口中可能超时，此时请在连接恢复后重新查询。

### 平台消息与主动通知

命令执行前按平台、作用域、账号、会话、平台消息 ID 生成去重键。
同一个 ID 在窗口内只接受一次，缺少平台消息 ID 时无法去重。
执行失败后保留去重标记，避免重放已经部分完成的扣费或发送操作。
这偏向避免重复，不保证失败命令自动重试；积分等仍应依靠数据库业务约束。

Warframe 通知规则先检查本节点是否具有可发送的 owner，再竞争原有数据库游标。
没有连接的节点不能提前“消耗”别人的通知。
游标提交与平台发送不是事务，发送失败可能丢通知；所有连接均离线时也不提供历史通知回放。
当前没有实现通知 outbox，不应承诺必达。

### 论坛文章 ID

分布式模式改用 Redis 原子序列，分配前将共享高水位提升到不小于数据库最大 ID。
实现使用 `DistributedIdGenerator.nextAtLeast("warframe-forum-article-v1", minimum)`：
业务先读取数据库最大 ID，再由 RDT 通过整数 CAS 提升共享高水位并返回唯一 ID。
既有 Snowflake ID 不变；不存在回写或重编号。
不可与旧版固定 worker ID 的写入节点混跑。
不要在线清空 Redis 后继续假定所有在途分配都安全，恢复 Redis/数据库需协调停写。

## 8. 认证与 RDT 控制台

业务认证仍是 Sa-Token，新增 `sa-token-redis-template` 共享会话。
所有节点使用一致的 token 配置，登录请求和后续 API 请求不需要固定到同一 JVM。
角色/权限来源、`@SaCheckPermission` 和机器人角色表均未替换。

Sa-Token 的 Key 不由 `rdt.namespace` 管理。不同环境至少使用隔离的 Redis
database/Redis 实例，不能只改 RDT namespace 就认为登录会话也被隔离。

RDT 控制台由独立的 `rdt-console` 应用提供，不属于 NnmBot 的运行时依赖，也不提供
NnmBot 的管理 API。控制台直接连接与业务节点相同的 Redis，通过 `rdt.namespace`
读取任务、队列、消费者和死信等 RDT 数据，因此不需要 NnmBot 暴露管理端口。

启动控制台时，在 RDT 仓库配置 `rdt-console/src/main/resources/application.yml`
中的 Redis 地址、命名空间和固定登录账号；NnmBot 不再配置
`rdt.management.*`，也不需要 `NNM_RDT_MANAGEMENT_*` 环境变量。控制台默认管理 API
前缀为 `/rdt/management`，与 NnmBot 的 `/api/admin/v1` Sa-Token 权限边界完全分离。
控制台展示的是 RDT 的共享可观察数据，不等于 NnmBot 后台任务 SSE 或机器人连接状态
页面。

## 9. 多实例运行的前置条件

引入 starter 不会自动共享本地磁盘或让 Redis 自身高可用：

- 所有节点共享 PostgreSQL，使用同一 `NNM_BOT_CREDENTIAL_KEY` 解密机器人凭证。
- 论坛正式文件和可通过任意节点下载的临时文件必须使用共同可访问的存储。
  `NNM_WARFRAME_FORUM_STORAGE_DIRECTORY`、`NNM_WARFRAME_FORUM_TEMPORARY_DIRECTORY`
  配置相同路径文本，并不代表多台机器的磁盘已共享；需要实际共享挂载或另行实现对象存储。
- OneBot 本地文件模式要求平台进程能访问发送节点的路径；跨主机需共享挂载或改用
  平台支持的文件上传/URL 方式。图片、JM 工作目录可保留节点本地，但不能让其他节点
  承接仅该目录拥有的下载 URL。
- 渲染模板、插件版本、外部 API 配置需要一致。每节点 JVM 缓存/插件仍是本地资源。
- Redis 应采用适当持久化、备份和高可用方案。队列、去重、账号期望状态不应被随意
  eviction；Redis 数据丢失不等于普通缓存失效。
- DNS 多 A 记录不负责应用健康检查和快速摘除故障节点。入口需要具有健康检查的
  反向代理/负载均衡；SSE 需关闭代理缓冲并允许长连接。
- 数据库事务提交与 Redis 期望状态发布存在失败窗口；启停请求报错时应重新查询并重试。
- 未做真实 QQ/OneBot 平台的跨进程断网与故障注入验收；不要仅凭单元测试直接全量切换。

## 10. 验证与故障演练

常规回归不要求本地 Redis：

```sh
sh mvnw -pl nnm-app -am test
```

对专用测试 Redis 执行跨客户端和完整应用测试：

```sh
sh mvnw -pl nnm-app -am test \
  -Drdt.it.redis=redis://127.0.0.1:16391
```

Redis 测试使用随机命名空间，只清理本次测试 Key，不执行 FLUSHDB。
完整应用测试使用 H2 创建/销毁测试结构，不修改开发 PostgreSQL。
本次新增覆盖：

- 两客户端账号租约互斥、过期接管、旧 token 不能续租/释放新 owner。
- 同类任务复用 ID，跨节点读取权限/结果/进度，执行节点消失后的超时状态。
- Stream 竞争消费、可靠广播两节点消费与离线恢复、PEL 接管、失败进入死信。
- 本地 runtime 接管、机器人改名清理、事务提交前不发布期望状态。
- 旧 OneBot session 失去归属后禁止发送；非 owner 不推进通知游标。
- 完整应用 RDT 注解注册、Redis Sa-Token 存储、WorldState JSON/Redis 往返。

切换前还应手动验证：

1. 两个 localhost 节点连接同一 Redis/PostgreSQL，启动同一个机器人，仅有一个平台连接。
2. 从非 owner 节点启停、查看状态、查询群组，确认由 owner 执行。
3. 在 A 登录后携带同一 Token 请求 B；在 A 提交任务后用 B 查询进度。
4. 终止 owner，观察租约过期及另一个节点重新连接；重新投递相同平台消息，检查不重复处理。
5. 暂停节点/断开 Redis，确认旧连接拒绝发送，身份失效节点需重启。
6. 停掉一个广播节点、更新数据、用相同 INSTANCE_ID 重启，确认本地索引和当前快照恢复。
7. 检查跨节点文件下载及重试，不能仅验证无文件的普通 API。
