# PostgreSQL 初始化

`init.sql` 是当前开发基线，业务对象显式位于 `nnmbot` schema。Hibernate 使用 `ddl-auto: validate`；应用不自动建表、改表、运行迁移或删除旧表。

## 开发库

新库只需运行 `init.sql`：已包含管理员通知表及权限、官方审核批次的 `PENDING/APPLYING/PARTIAL_FAILED/APPLIED/EXPIRED/ARCHIVED` 状态、命令 HTML 候选模板/单选 `template_id`、紫卡市场模板；关联命令由插件代码声明，不建立关联表。空库由开发者手工执行一次 `init.sql`，之后由 Hibernate `ddl-auto: validate` 校验；应用不会自动建表、改表或执行 SQL。

**已经有数据的开发库不能再次执行 `init.sql`**：初始化脚本的建表和插入会与现有数据冲突。以前执行过 01、02 的旧库如缺少 `bot_command.template_id`，需先备份、确认数据保留方案，再单独人工同步该列和旧模板字段；不要将这份完整初始化脚本当作补丁执行。需要保留已有数据时，先确认保留方案，再手工同步数据库；不要在已有库重放初始化脚本。

旧库的 01～04 手工修复已执行；这些临时脚本已清除。今后新库仅执行包含现行结构的 `init.sql`，现有库不要重放初始化脚本。

数据段为普通 INSERT，可使用 psql、DataGrip、DBeaver 或 JDBC 脚本执行器。不要仅选中半条 INSERT。需要转换新的 PostgreSQL dump 时使用 `scripts/database/convert-postgres-copy-init.cjs`；不要用字符串替换处理 COPY 协议。

每张表和每个字段写明 COMMENT，约束/索引上方说明业务目的。官方模型和所有生成式关系表使用 `wf_official_*`，社区人工资料使用 `wf_community_*`。来源类别与维护状态分开，官方自维护记录仍在官方模型表中。

群组级联约束、官方批次失败重试期限及对象回收标记均已归入 `init.sql`，不再保留本轮 01、02 临时脚本。

## 本轮缓存版本升级

已有开发库先执行一次 `01_dataset_cache_revision.sql`，再启动本轮代码。脚本在现有
`wf_official_dataset_file` 和 `wf_community_dataset_lock` 中增加 `data_revision`，并补齐
71 个官方 JSON 的文件锁行；保留已采纳 SHA、来源及业务内容，不拉取或重导入数据。
新数据库仍只执行 `init.sql`，不重复执行 01。文件锁的初始 `checked_at=epoch` 不代表已成功拉取。

版本与每个写事务原子递增，缓存通知只在提交后广播；失败时以各实例定时复核版本补偿。
本轮没有 Outbox/Inbox 表、自动迁移器或第二份统一业务主表。

## 验证

```sh
node scripts/database/verify-warframe-sql.cjs --hibernate
node scripts/database/verify-dataset-cache-sql.cjs
node scripts/database/verify-bounty-sql.cjs
node scripts/database/verify-worldstate-command-sql.cjs
node scripts/database/verify-bot-reply-routing.cjs
node scripts/database/verify-group-cascade.cjs
```

检查器在临时 PostgreSQL 中验证当前初始化基线；只创建临时 cluster/database，结束后清理，不读取应用数据库连接。可用 `PG_BIN` 指定二进制目录。Hibernate 检查只启动映射，不启动应用、Redis 或业务任务。

初始化包含当前命令、菜单、权限及 HTML 模板，但不作为运行时已导入官方数据或实机发送的证明。模板读取 VO 的字段，数据库只保存 HTML 和 URL/占位符，不保存图片二进制。附件由 VaultS3 管理；对象引用和生命周期见对应论坛服务。

SQL 检查夹具放在 `src/test/resources`，不放入业务代码。表数量和测试结果读取当前检查输出，不保存历史验收计数。
