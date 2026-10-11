# NnmBot 开发约定

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
- Service 接口与实现分离；DTO、响应对象和实体职责明确。
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
- 在连接生命周期、并发状态、协议边界和非直观业务约束等关键位置添加精简注释，说明设计意图或
  风险；自解释代码和普通 CRUD 不添加重复代码含义的注释。
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
