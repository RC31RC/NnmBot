# NnmBot 数据库与 4GB 部署约定

## 技术选择

当前数据访问层使用 Spring Data JPA，底层 ORM 为 Hibernate。项目继续保留这套方案，
不切换或并行引入 MyBatis-Plus。

主要原因：

- RBAC 的用户、角色、菜单、权限及关联关系已经按 JPA 实现。
- 当前业务以实体关系和常规管理操作为主，JPA 足以覆盖，切换只会带来重复实现。
- 4GB 内存限制的主要风险来自 JVM 堆、浏览器渲染、线程池、缓存和数据库连接数，
  ORM 框架差异不是当前主要矛盾。
- 复杂统计或批处理后续可使用 JPQL、原生 SQL、JDBC 批处理或专用查询模块解决，
  无需为了少量复杂查询替换整个 ORM。

## 查询实现边界

- 常规 CRUD、分页和实体关联使用 Spring Data Repository。
- 简单动态条件优先使用 JPA Specification。
- 当多表动态筛选、条件组合或类型安全查询明显增多时，再引入 QueryDSL。
- 业务 Service 和 Controller 不得拼接 SQL。
- 必须使用数据库特有 SQL、报表查询或批处理时，应放入独立的数据访问实现并增加集成测试。
- `db/migration` 中的 SQL 是数据库结构迁移，不是业务查询。
- 自动化测试可以直接查询数据库来验证迁移结果；建库夹具放在 `src/test/resources`。

选择 JPA 并不表示 MyBatis-Plus 不适合企业项目。MyBatis-Plus 更适合以表和 SQL 为中心、
复杂联表和批量操作占比较高的系统；当前 RBAC 以用户、角色、菜单、权限的实体关系和常规
管理操作为主，JPA 的关系映射、事务管理和 Repository 模型更贴合现有实现。后续如果业务
形态变化，可以增加独立查询模块，而不是同时维护两套通用 ORM。

## 数据库策略

- 开发和正式环境优先使用受支持版本的 PostgreSQL。
- PostgreSQL 对象层级统一为 `database=nnmbot`、`schema=nnmbot`。
- `public` 仅保留 `flyway_schema_history` 等数据库级迁移元数据，不放业务表。
- H2 仅作为测试依赖，用于自动化测试，不参与正常开发和生产运行。
- 表结构统一由 Flyway 管理。
- Hibernate 在所有环境使用 `ddl-auto: validate`，只校验实体与表结构，不自动改表。
- 当前 `db/migration` 脚本以 PostgreSQL 为目标。
- 如果最终选择 MySQL 8，应建立独立迁移目录并进行完整集成测试，不能假设 PostgreSQL
  的 `BIGSERIAL`、索引和类型定义可以直接复用。

当前项目仍处于开发基线阶段，现有测试数据无需兼容。`db/migration` 暂时只维护
`V1__init_schema.sql`，新增或修改字段直接更新这一个文件，不增加开发期版本号。旧的本地
开发结构发生变化时可以重建，不在 `public` 中保留业务表。

Flyway history schema 固定为 `public`，Hibernate 默认 schema 为 `nnmbot`。数据库客户端
查看对象时，应展开 `nnmbot -> Schemas -> nnmbot -> Tables`。

Flyway 在开发阶段仍保留，用于验证一份基线能否从空库稳定创建完整结构。正式发布前冻结
该基线；从那时起，所有结构变化再使用新的不可变版本脚本，且不得修改已经发布或执行过的
历史迁移，也不得依赖 Hibernate `update`。

## 4GB 机器基线

推荐先采用以下保守参数：

```bash
java -Xms256m -Xmx1536m \
  -XX:MaxMetaspaceSize=256m \
  -XX:+UseG1GC \
  -jar nnm-app/target/NnmBot.jar \
  --spring.profiles.active=prod
```

基础资源约束：

- Hikari 最大连接数保持为 5，确认数据库压力后再调整。
- 通用异步任务默认最多 1 个工作线程，定时任务避免无界并发。
- JVM 业务缓存目标使用 cache2k，每个缓存区域必须设置容量、过期和故障降级策略。
- 生产环境关闭 Hibernate SQL 输出和 DEBUG 日志。
- 默认优先使用 OpenHTMLToPDF；启用 Playwright/Chromium 时必须限制浏览器实例和页面并发。
- 不在 JVM 内长期保存大图片、完整历史响应或无上限会话集合。

当机器人功能、管理端和图片渲染同时运行后，应以实际监控数据调整堆大小。4GB 主机上建议
为操作系统、数据库代理及 Chromium 等进程至少预留 1.5GB，不要把 JVM 最大堆设置到接近
物理内存总量。
