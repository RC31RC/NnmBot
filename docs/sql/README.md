# PostgreSQL 初始化与手工修复

`init.sql` 是当前开发基线，业务对象显式位于 `nnmbot` schema。Hibernate 使用 `ddl-auto: validate`；应用不自动建表、改表、运行迁移或删除旧表。

## 开发库

数据库名和 schema 均为 `nnmbot`。新建空库时由开发者手工执行 `init.sql`；已有数据库不为更新模板或单个表而重放整份初始化脚本。开发阶段可直接调整基线；额外手工修复按 `01_*.sql`、`02_*.sql` 维护，不引入 Flyway 或新旧双写。

`01_remove_legacy_community_and_arbitration_runtime_tables.sql` 只处理其声明的退役表，执行前阅读脚本内条件和事务保护。它不由应用启动调用。

数据段为普通 INSERT，可使用 psql、DataGrip、DBeaver 或 JDBC 脚本执行器。不要仅选中半条 INSERT。需要转换新的 PostgreSQL dump 时使用 `scripts/database/convert-postgres-copy-init.cjs`；不要用字符串替换处理 COPY 协议。

每张表和每个字段写明 COMMENT，约束/索引上方说明业务目的。官方模型和所有生成式关系表使用 `wf_official_*`，社区人工资料使用 `wf_community_*`。来源类别与维护状态分开，官方自维护记录仍在官方模型表中。

## 验证

```sh
node scripts/database/verify-warframe-sql.cjs --hibernate
node scripts/database/verify-bounty-sql.cjs
node scripts/database/verify-worldstate-command-sql.cjs
node scripts/database/verify-bot-reply-routing.cjs
```

检查器只创建临时 PostgreSQL cluster/database，结束后清理，不读取应用数据库连接。可用 `PG_BIN` 指定二进制目录。Hibernate 检查只启动映射，不启动应用、Redis 或业务任务。

初始化包含当前命令、菜单、权限及 HTML 模板，但不作为运行时已导入官方数据或实机发送的证明。模板读取 VO 的字段，数据库只保存 HTML 和 URL/占位符，不保存图片二进制。附件由 VaultS3 管理；对象引用和生命周期见对应论坛服务。

SQL 检查夹具放在 `src/test/resources`，不放入业务代码。表数量和测试结果读取当前检查输出，不保存历史验收计数。
