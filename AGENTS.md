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

- Java 代码保持精简，参考 `charge-operator` 的分层方式。
- Controller 只负责协议转换、参数接收和权限声明，业务逻辑放入 Service。
- Service 接口与实现分离；DTO、响应对象和实体职责明确。
- 使用构造注入，避免字段注入。
- 跨模块分页响应统一使用 `nnm-common` 的 `PageResult`，不要在业务模块重复定义同构分页 DTO。
- 业务前置条件优先使用 `BusinessAssert` 并配合稳定 `ErrorCode`，不要散落重复的异常构造逻辑。
- 查询方法暂不统一添加事务注解；写操作按实际原子性边界直接使用 Spring
  `@Transactional`，不要在 `nnm-common` 定义事务组合注解。
- 认证鉴权统一使用 Sa-Token，不引入 Spring Security 认证链或并行权限框架。
- Spring Security 仅可通过 `spring-security-crypto` 提供 BCrypt 密码哈希实现。
- 后端是权限安全边界，管理接口使用 `@SaCheckPermission` 校验稳定权限码。
- MapStruct 仅用于字段较多或存在复用价值的对象映射，简单映射直接构造。
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
- 全局 `RestClient` 默认校验 TLS 证书。`nnm.http.trust-all-certs=true` 只能用于临时排查
  本地代理或证书链问题，不得成为开发共享配置或生产默认值。

## 数据访问与运行约定

- ORM 统一使用 Spring Data JPA + Hibernate，不并行引入 MyBatis-Plus。
- 常规 CRUD 和实体关联通过 Repository 完成，动态条件优先使用 JPA Specification。
- 只有出现多表动态筛选、复杂统计等明确需求时才引入 QueryDSL，不为简单查询提前增加依赖。
- 业务代码不得拼接原生 SQL；确需报表、批处理或数据库特有能力时，放入独立查询实现并补充集成测试。
- Flyway SQL 仅用于表结构和基础数据迁移，统一放在 `nnm-app/src/main/resources/db/migration`。
- 测试可使用 SQL 验证迁移结果，但测试夹具应放在 `src/test/resources`，不得混入生产业务逻辑。
- 表结构由 Flyway 管理；当前开发阶段使用
  `nnm-app/src/main/resources/db/migration/V1__init_schema.sql` 作为唯一可编辑基线，
  直接在该文件中维护结构，不新增版本号。正式发布前再将基线拆分为不可变的版本迁移。
- Hibernate 使用 `ddl-auto: validate`，不得依赖 `update` 修改数据库。
- H2 仅作为测试依赖，用于自动化测试；开发和生产数据库统一优先 PostgreSQL。
- 当前迁移脚本以 PostgreSQL 为目标；若正式选择 MySQL 8，使用独立的数据库迁移目录，不编写伪兼容 SQL。
- PostgreSQL 使用 `database=nnmbot`、`schema=nnmbot`；Flyway 历史表固定保留在 `public`。
- 新增迁移必须显式使用 `nnmbot.<table>`，不要依赖连接的 `search_path` 隐式决定业务对象位置。
- 数据库连接池默认上限为 5。新增线程池、缓存或渲染并发时，必须评估 4GB 内存机器的资源预算。
- 当前开发阶段不要求保留测试数据库数据；结构调整直接修改唯一基线 SQL 文件。不要修改无关的
  本地日志文件。正式发布前再恢复“新增版本脚本、不修改历史迁移”的生产迁移约定。
- Warframe 外部数据源、代理和缓存演进遵循
  `docs/Warframe外部数据源、代理与缓存演进任务.md`；目标 JVM 业务缓存为 cache2k，
  cache2k 已作为生效的 `CacheManager`。Caffeine 依赖和 YAML 配置仅为评估回退暂留，
  不得在两个框架上并行新增业务缓存。

## 前后端权限约定

- 角色编码使用 `R_` 前缀，例如 `R_SUPER`、`R_ADMIN`、`R_USER`。
- 权限码使用 `业务域:资源:动作`，例如 `system:user:update`。
- 菜单决定导航可见性，权限码决定按钮和 API 操作，两者不要混为一体。
- 前端 `v-auth`、`v-roles` 只改善交互体验，不能代替后端鉴权。
- 前端按钮权限以 `/api/admin/v1/me` 返回的 `buttons` 为准。

## 验证

- 后端开发启动：`./scripts/run-dev.sh`
- 后端常规验证：`sh mvnw -pl nnm-app -am test`
- 前端类型检查：`npm exec vue-tsc -- --noEmit`
- 前端定向检查：`npm run lint -- <changed-files>`
- 开发运行使用后端 `8080`、前端 `3006`，不执行 `mvn package` 或 `npm run build`。
