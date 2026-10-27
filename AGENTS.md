# NnmBot 开发约定

开发阶段直接更新 API、状态模型和全部调用方，不为历史版本保留旧构造器、接口别名、迁移、双写或回退分支。外部协议适配按实际协议需求保留。

## 项目边界

- 后端仓库：`/Users/ren/RcGit/NnmBot`
- 前端仓库：`/Users/ren/RcGit/nnm-admin-web`
- 两个仓库独立开发、独立版本管理，通过 `/api/admin/v1` 协作。
- 当前为本地持续开发阶段，服务地址统一使用 `localhost` 或 `127.0.0.1`。
- 未经明确要求，不执行生产打包、不提交 Git、不引入部署环境地址。
- 前端原有页面、路由、组件和功能板块源码必须完整保留，作为后续功能开发与视觉风格的实现范例；开发阶段继续展示这些页面，不得提前删除、覆盖或隐藏，项目收尾时再统一评估哪些入口需要隐藏。
- 对登录、RBAC 等原页面进行业务适配时，应尽量保留原有结构和实现风格；如需大幅重写，先保留可追溯的原始实现或与 Art Design Pro 上游源码核对，不能只保留同名菜单。

## 后端风格

- Java 注解与被注解的字段、方法、参数或类型声明必须分行书写；例如 `@PathVariable`
  单独占一行，下一行再写参数类型和名称。
- 无特殊命名冲突时，使用 `import` 导入类型，不在业务代码中直接书写类的全限定名。
  只有同名类型无法通过 import 清晰区分时才保留全限定名，并保持范围最小。
- Java 代码保持精简，参考 `charge-operator` 的分层方式。
- Controller 只负责协议转换、参数接收和权限声明，业务逻辑放入 Service。
- Warframe 主体包按 `runtime` 与 `persistent/{official,curated}` 组织：动态数据及运行时计算归入 `runtime`，官方 JSON 持久化归入 `persistent/official`，本项目维护的数据归入 `persistent/curated`。`plugin`、`controller`、`schedule`、`mq`、`storage` 保持 Warframe 根包，并在内部按来源和业务分包。
- Warframe 动态上游协议模型放入 `runtime/rawdata/<业务>`；官方静态 JSON 输入 DTO 与解析器放入 `persistent/official/pojo/dto`、`persistent/official/parser`；人工导入模型与解析器放入 `persistent/curated`。不保留根目录 `rawdata`、`parser`、`service`、`repository`、`pojo` 或 `shared`。
- Service 接口与实现分离；实体保留原名，不加 DO/Entity 后缀；Repository 非受管投影与服务间传输统一以 `DTO` 结尾；前端响应与命令/页面展示对象统一以 `VO` 结尾；查询参数统一以 `Query` 结尾；新增和修改参数使用或复用 `DTO`，不另设 Request/Response/Item/Display 后缀体系。嵌套传输/展示类型遵循相同后缀。
- `pojo/dto`、`pojo/vo` 只按来源及业务域分包，不再额外设置 `input/output`；同一类型同时承担内部传输和前端展示时，保留 DTO 并在展示边界转换为 VO，不把 DTO 直接作为业务 API 响应。
- Admin 与 Warframe 数据对象统一采用 `pojo/entity`、`pojo/dto`、`pojo/vo`、`pojo/query` 类型层次；Admin 类型在模块根 `pojo` 下，Warframe 类型放在所属生命周期和来源目录下，再按实际业务分包。内部运行状态和外部协议模型不强制归入这些类型。
- Service 接口与实现分离；Admin 服务位于 `service/<业务>`，Warframe 服务位于 `runtime/service/<业务>` 或 `persistent/{official,curated}/service/<业务>`，实现放入对应 `impl`；公开传输对象不定义在 Service 内，下载、文件存储、缓存、本地事件和纯展示映射分别归入实际职责包。
- 官方 JSON 先绑定 `persistent/official/pojo/dto/<业务>` 的逐文件类型化输入；人工维护数据导入绑定 `persistent/curated/pojo/dto/<业务>` 的白名单业务对象。不得把外部 JSON 直接绑定 JPA Entity；原始 JSON 独立保留用于差异审核与精度/缺失值审计。
- 查询入参统一放入所属模块的 `pojo/query/<来源或业务>`；Warframe 查询类型随 `runtime` 或 `persistent` 来源归属，不因经由 HTTP 接收而混入普通 DTO；超过两个查询参数时使用明确 Query 类型，不使用裸 Map。
- 生产枚举统一放在所属模块的 `constant` 包；Warframe 来源专用枚举放在 `runtime/constant` 或 `persistent/{official,curated}/constant`，真正跨来源的模块常量放在根 `constant`。同一业务职责且有共同调用方的相关枚举合并到常量容器的内部枚举，禁止在 DTO、Entity、Service 或内部状态类中再嵌套定义枚举。
- 官方静态数据的 71 个 JSON 各有独立主表和具体 Repository；禁止跨数据集总父表和统一根 Repository。仅本 JSON 内必要子表由主表统一维护。
- MQ 在所属 Maven 模块的 `mq` 下固定分为 `consumer`、`message`、`messagedescriptor`、`consumerdescriptor` 四个目录，目录内再按业务域细分；消息载荷、发布描述符、消费策略和消费者独立成类，不再混入 `messaging` 或匿名策略。
- 所有 `@Scheduled`、`@RdtScheduled` 入口放在所属模块的 `schedule` 包；分布式统一认领任务保留 RDT，各实例内存与本地资源刷新保留本地调度，不因归包改变任务作用域。
- Warframe Plugin 只做命令参数转换、调用 Service 和组装 VO/CommandResult；业务 Service 不依赖 Plugin 或渲染包，也不组装 CommandResult。内部状态、权限上下文和上游协议模型放在真实职责包，不保留混装 DTO/VO 的通用 `model` 包。
- Spring 管理的 Service、Repository、Provider 等协作依赖优先使用 `@Resource`；
  不可变值对象、命令元数据和需要在构造阶段完成校验的非 Spring 对象继续使用构造器传参。
- 跨模块分页响应统一使用 `nnm-common` 的 `PageResult`，不要在业务模块重复定义同构分页 DTO。
- 业务前置条件优先使用 `BusinessAssert` 并配合稳定 `ErrorCode`，不要散落重复的异常构造逻辑。
- 查询方法暂不统一添加事务注解；写操作按实际原子性边界直接使用 Spring
  `@Transactional`，不要在 `nnm-common` 定义事务组合注解。
- 认证鉴权统一使用 Sa-Token，不引入 Spring Security 认证链或并行权限框架。
- Spring Security 仅可通过 `spring-security-crypto` 提供 BCrypt 密码哈希实现。
- 后端是权限安全边界，管理接口使用 `@SaCheckPermission` 校验稳定权限码。
- MapStruct 仅用于字段较多或存在复用价值的对象映射，简单映射直接构造。
- 字符串、集合和对象的通用防御性判断可以使用 Hutool `StrUtil`、`CollUtil`、
  `ObjUtil`；仅在能提升可读性时使用，文件、协议和业务状态校验仍保留显式判断。
- Warframe 模块手写声明的方法使用中文 Javadoc 说明用途、各入参和返回值；对外传播的受检异常说明抛出条件，生成式方法的注释同步维护在生成器中。
- 在连接生命周期、并发状态、协议边界和非直观业务约束等关键位置添加精简行内注释，说明设计意图或
  风险；自解释代码和普通 CRUD 不添加重复代码含义的行内注释。
- 不为普通业务编排重复捕获异常；需要协议错误转换、有效快照恢复、逐通知隔离或资源清理时才在明确边界捕获，其余失败直接传播。
- 优先沿用现有模块边界，不为单一场景提前增加抽象。

## Common 与 Core 约定

- `nnm-common` 只放与具体业务域无关的稳定能力，例如错误模型、响应模型和 HTTP
  基础设施；不得反向依赖 `admin`、`warframe` 等业务模块。
- 静态工具必须无状态、线程安全、异常语义明确并可独立测试；不新增聚合无关职责的“万能
  Utils”，没有实际调用点的工具不提前迁入 `common` 或 `core`。
- 不使用静态 Spring Bean 持有器、静态线程池或带硬编码身份的 `ThreadLocal` 请求上下文。
  调用链上下文应显式传递；确需跨线程传播时，先定义清晰的上下文模型和生命周期。
- 外部 API 地址、超时、重试、缓存容量和生命周期统一使用 `@ConfigurationProperties`
  管理并在启动时校验，不在业务类中散落 `@Value` 或可配置常量。
- 文件持久化必须传播明确异常，并使用同目录临时文件、刷新落盘和原子替换；不得复制吞异常、
  直接覆盖目标文件的旧式文件工具。
- 不新增无界线程池。通用异步任务和调度任务统一使用 `ExecutorConfig`，容量通过
  `nnm.executor.*` 配置；渲染、数据刷新等高成本任务需要独立限流时再建立专用执行器。
- 线程池拒绝策略默认保留背压语义，调整线程数和队列容量时必须结合数据库连接池、外部 API
  限流和 4GB 部署预算一起评估。
- `BotPlugin.command()` 必须非空且在忽略大小写、首尾空格后全局唯一；插件顺序由 `order`
  和规范化命令稳定确定。
- 插件的 `match()` 异常不得阻断其他插件匹配；已匹配插件的 `handle()` 异常不得继续交给
  其他插件重复处理同一消息。
- RDT 的使用方式按职责选择：定时任务、消息消费者和方法级互斥优先使用声明式注解；
  分布式 ID 获取、消息投递和跨连接生命周期的 owner lease 优先使用编程式 API。
- 当前 RDT `@RdtLock` 是方法级锁，不等同于事务级锁；不要假设它会自动延长到外层事务
  最终提交。事务级锁累积管理器暂未实现，新增需求必须先明确事务传播和释放边界。
- 不使用 `ThreadLocal` 跨 WebSocket、线程池、CompletableFuture 或调度回调传播会话、事务、
  锁或业务身份。当前 WebSocket 处理链存在 `applicationTaskExecutor`、
  `shiroTaskExecutor` 和异步完成回调；跨线程上下文必须显式传递，并在同步短调用链结束后
  清理任何临时 ThreadLocal。
- 全局 `RestClient` 默认校验 TLS 证书。`nnm.http.trust-all-certs=true` 只能用于临时排查
  本地代理或证书链问题，不得成为开发共享配置或生产默认值。

## 数据访问与运行约定

- ORM 统一使用 Spring Data JPA + Hibernate，不并行引入 MyBatis-Plus。
- 常规 CRUD 和实体关联通过 Repository 完成，动态条件优先使用 JPA Specification。
- 只有出现多表动态筛选、复杂统计等明确需求时才引入 QueryDSL，不为简单查询提前增加依赖。
- 业务代码不得拼接原生 SQL；确需报表、批处理或数据库特有能力时，放入独立查询实现并补充集成测试。
- 当前开发阶段不使用 Flyway。数据库初始化 SQL 统一聚合在
  `docs/sql/init.sql`，已有数据库修复脚本放在 `docs/sql/` 根目录，由开发者手工执行；
  应用不会自动建表或自动修改数据库结构。
- 所有建表和改表 SQL 都必须写清楚注释：表使用 `COMMENT ON TABLE`，字段使用
  `COMMENT ON COLUMN`；新增约束、索引或枚举值时，在 SQL 上方补充其业务目的，避免只看字段名
  才能理解结构。
- 测试可使用 SQL 验证数据库结构，但测试夹具应放在 `src/test/resources`，不得混入生产业务逻辑。
- 当前开发阶段可以直接修改或重建 `docs/sql/init.sql`；合并基线后的新增脚本按
  `docs/sql/01_*.sql`、`docs/sql/02_*.sql` 顺序维护，不增加自动迁移版本成本。
- Hibernate 使用 `ddl-auto: validate`，不得依赖 `update` 修改数据库。
- H2 仅作为测试依赖，用于自动化测试；开发和生产数据库统一优先 PostgreSQL。
- 当前手工脚本以 PostgreSQL 为目标；若正式选择 MySQL 8，使用独立的 MySQL 脚本目录，不编写
  伪兼容 SQL。
- PostgreSQL 使用 `database=nnmbot`、`schema=nnmbot`；业务表放在 `nnmbot` schema，`public`
  仅作为数据库默认 schema，不承载本项目业务表。
- SQL 必须显式使用 `nnmbot.<table>`，不要依赖连接的 `search_path` 隐式决定业务对象位置。
- 数据库连接池默认上限为 5。新增线程池、缓存或渲染并发时，必须评估 4GB 内存机器的资源预算。
- 当前开发阶段不要求保留测试数据库数据；结构调整直接修改对应手工 SQL 文件。不要修改无关的
  本地日志文件。
- Warframe 外部数据源、代理和缓存演进遵循
  `docs/Warframe数据与命令实现.md`；目标 JVM 业务缓存为 cache2k，
  cache2k 已作为生效的 `CacheManager`。Caffeine 依赖和 YAML 配置仅为评估回退暂留，
  不得在两个框架上并行新增业务缓存。

## 前后端权限约定

- 角色编码使用 `R_` 前缀，例如 `R_SUPER`、`R_ADMIN`、`R_USER`。
- 权限码使用 `业务域:资源:动作`，例如 `system:user:update`。
- 菜单决定导航可见性，权限码决定按钮和 API 操作，两者不要混为一体。
- 前端 `v-auth`、`v-roles` 只改善交互体验，不能代替后端鉴权。
- 前端按钮权限以 `/api/admin/v1/me` 返回的 `buttons` 为准。
- 机器人能力角色使用 `bot_robot_role`、`bot_command_robot_role`、
  `bot_robot_role_instance`，只表达“命令由哪些机器人实例处理”，不得复用
  `sys_role` 或把机器人实例绑定到后台用户角色。
- 群组发现只通过管理端按钮主动查询；发现结果不自动写入 `bot_scope`，只有用户点击添加
  后才创建作用域。OneBot 可同步平台返回的群成员角色，未知平台用户不得自动创建系统用户。

## 验证

- 后端开发启动：`./scripts/run-dev.sh`
- 后端常规验证：`sh mvnw -pl nnm-app -am test`
- 前端类型检查：`npm exec vue-tsc -- --noEmit`
- 前端定向检查：`npm run lint -- <changed-files>`
- 开发运行使用后端 `8080`、前端 `3006`，不执行 `mvn package` 或 `npm run build`。
