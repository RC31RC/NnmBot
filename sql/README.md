# PostgreSQL 初始化

`scripts/` 是本地代码生成、SQL 校验及离线资料核对工具，不纳入 Git 跟踪，也不是应用运行依赖；新检出仓库可能没有这些本地工具及其报告文件。

`init.sql` 是当前开发基线，业务对象显式位于 `nnmbot` schema。Hibernate 使用 `ddl-auto: validate`；应用不自动建表、改表、运行迁移或删除旧表。

## 开发库

新库运行 `init.sql` 建立结构及系统固定数据；社区资料通常在运行时由管理端录入。`community.sql` 仅用于新库需要一次性导入本轮已核对的参考业务数据时手工执行，不是运行时同步前提。
`init.sql` 包含管理员通知表及权限、官方审核批次的 `PENDING/APPLYING/PARTIAL_FAILED/APPLIED/EXPIRED/ARCHIVED` 状态、命令 HTML 候选模板/单选 `template_id`、紫卡市场模板；关联命令由插件代码声明，不建立关联表。空库由开发者手工执行一次 `init.sql`，之后由 Hibernate `ddl-auto: validate` 校验；应用不会自动建表、改表或执行 SQL。

**已经有数据的开发库不能再次执行 `init.sql`**：初始化脚本的建表和插入会与现有数据冲突。以前执行过 01、02 的旧库如缺少 `bot_command.template_id`，需先备份、确认数据保留方案，再单独人工同步该列和旧模板字段；不要将这份完整初始化脚本当作补丁执行。需要保留已有数据时，先确认保留方案，再手工同步数据库；不要在已有库重放初始化脚本。

较早的 01～04 手工修复已在开发库执行，临时脚本已清除。`01_drop_foreign_key_constraints.sql` 也已在现有开发库执行并删除；其净效果体现在初始化基线中：不再种入已退役的 `conclave` 命令及 `warframe:community:sync` 权限。后续增量脚本从 `01_*.sql` 重新编号；现有库不要重放 `init.sql`。

数据段为普通 INSERT，可使用 psql、DataGrip、DBeaver 或 JDBC 脚本执行器。不要仅选中半条 INSERT。需要转换新的 PostgreSQL dump 时使用 `scripts/database/convert-postgres-copy-init.cjs`；不要用字符串替换处理 COPY 协议。

每张表和每个字段写明 COMMENT，约束/索引上方说明业务目的。官方模型和所有生成式关系表使用 `wf_official_*`，社区人工资料使用 `wf_community_*`。来源类别与维护状态分开，官方自维护记录仍在官方模型表中。

`init.sql` 不声明表级外键；群组关联的级联删除由业务事务负责。官方批次失败重试期限及对象回收标记均已归入 `init.sql`，其原临时脚本不再保留。

## 本轮参考资料初始化

旧的缓存版本升级已合并进 `init.sql`，对应的临时脚本已删除。版本只随应用业务写事务递增，
用于缓存代际及 MQ 通知排序；应用不轮询数据库版本，不主动发现手工 SQL 改动。
MQ 通知失败只记录日志；其他实例等待缓存正常过期、淘汰或重启后再加载。

| 文件 | 职责 |
| --- | --- |
| `init.sql` | 表结构、系统固定数据，以及已执行的 01～04 净效果：17 条完整自维护外观/物品资料（14 皮肤、2 自定义、1 杂项）、20 条深层楼层映射、1 条突击火灾术语 |
| `community.sql` | 可选参考资料：社区别名、裂罅、仲裁和仍缺少的官方模型自维护资料；不重播已经纳入 `init.sql` 的记录 |

空库先执行 **`init.sql`**；需要其余参考资料时，再手工执行 **`community.sql`**。01～04 已在当前开发库执行并删除；已有库不要重跑 `init.sql`、也不要再次执行已删除补丁。旧库的 7 条重复译名和误归属外观已在已执行补丁中清理；空库无需执行对应 DELETE。
后续实际需要的增量脚本从 `sql/01_*.sql` 重新编号，仅在新变更发生时新增。官方资料仍通过审核流程导入；20 条楼层记录只是官方公告的固定楼层→奖池映射，不是本周随机结果，也不复制已采纳的官方掉落池。

需要补充其他参考资料时可执行 `community.sql`：只插入完全缺失的身份，不覆盖人工/官方记录，也不推进缓存版本。手工 SQL 不触发应用缓存刷新，请在应用启动前执行。

`community.sql` 包含 332 组社区物品别名、43 条裂罅别名及组合、18 个节点收益估计、34 条其余自维护官方模型资料（17 条物品本地化、17 条首领代码簿）和 9 条限定用途展示词条。移到 `init.sql` 的 17 条记录及火灾术语不再重复写入 `community.sql`。

归并后的结构化数据及来源校验摘要在 `scripts/database/data/warframe-reference-*.json`，源数据许可证
与种子放在同一目录。先阅读 [参考数据核对说明](../scripts/database/data/参考数据核对说明.md)：其中解释选取的来源与未导入原因；后续逐条技术处置见 [参考资料后续处理](../scripts/database/data/参考资料后续处理.md)。候选仍按候选保留，不冒充已确认的唯一身份，也不代表所有参考功能已移植。“未导入”不等于“无效”。机器 JSON 保留用于来源追溯和自动校验。

`python3 scripts/database/generate-reference-seed.py` 可离线重生成 `community.sql` 中的参考种子和中文核对说明，**只读取 `init.sql` 中的基线身份以免重复，不写入 `init.sql`，不自动生成或覆盖编号脚本**，也不覆盖 community 中独立维护的原有裂罅组合。
生成过程不依赖参考目录、不联网、不连接数据库；重新核对来源时同时传入 `--datasource`、`--reference`、`--official`。
编码无效、身份未确认及语义存在冲突的条目不自动写入业务表。

本轮 71 项用途核对见 [参考词条使用场景核对](../scripts/database/data/参考词条使用场景核对.md)：读取 JS 参考项目与 NyxBot 实际调用链，保留源码路径和摘要；不把英文同名、展示短码、旧半括号格式键当成游戏对象身份。
重新核对用途可运行 `python3 scripts/database/audit-reference-usage.py --datasource <DataSource根目录> --reference <warframe-info-api根目录> --nyx <NyxBot根目录> --official <官方成品data/latest>`；用途报告更新后再重新生成参考种子。

[未匹配译名分组说明](../scripts/database/data/未匹配译名分组说明.md) 进一步复核 4,281 条初次未匹配译名：在完整数据树中，2,219 条找到带身份的精确名称候选、10 条只有文本投影候选、2,052 条没有精确候选。其中 2,052 条又按同中文、数量／括号限定、Archwing 展示标记和模糊名称继续分组；候选不代表运行时已经支持，也不会自动生成自维护记录。逐条源路径、身份线索和参考源码字面量位置保存在 `warframe-unmatched-translations.json`。
可用 `python3 scripts/database/audit-unmatched-translations.py --datasource <DataSource根目录> --official <官方成品data/latest> --reference <warframe-info-api根目录> --nyx <NyxBot根目录>` 重做这一离线分组。

可用 `python3 scripts/database/resolve-reference-audit.py --references <参考项目共同目录> --official <官方成品data/latest>` 复核结构化来源的覆盖及旧译名的逐条处置。此脚本只生成离线报告，不连接数据库，不生成主表或改写编号 SQL。

资料对应的当前命令、确认及保留边界，以及正式 diff 的身份规则见 [资料与命令对应及 diff 规则](../scripts/database/data/资料与命令对应及diff规则.md)。

## 本轮后续核对

已执行的 02 曾纠正旧皮肤表归属、补齐完整业务定义，未改动 OFFICIAL 行。它不再作为新库的补丁，新库由 `init.sql` 直接建立最终正确的业务资料；其他旧库需单独核查数据，不重放本仓库已删除的补丁。

仍未证明业务身份的普通译名和旧敌人资料见 [本轮结果与待裁定事项](../scripts/database/data/参考资料待裁定事项.md)。
不能把“有同名候选”或旧清单为空解释成全量核对已经完成。

可用 `python3 scripts/database/triage-reference-translations.py` 从带来源指纹的审核结果重生成两份 CSV 和摘要。

## 验证

```sh
python3 scripts/database/resolve-reference-audit.test.py
python3 scripts/database/triage-reference-translations.test.py
node scripts/database/verify-reference-customs-sql.cjs
node scripts/database/verify-reference-seed-sql.cjs
node scripts/database/verify-warframe-sql.cjs --hibernate
node scripts/database/verify-dataset-cache-sql.cjs
node scripts/database/verify-reference-seed-sql.cjs
node scripts/database/verify-bounty-sql.cjs
node scripts/database/verify-worldstate-command-sql.cjs
node scripts/database/verify-bot-reply-routing.cjs
node scripts/database/verify-group-cascade.cjs
```

检查器在临时 PostgreSQL 中验证当前初始化基线；只创建临时 cluster/database，结束后清理，不读取应用数据库连接。可用 `PG_BIN` 指定二进制目录。Hibernate 检查只启动映射，不启动应用、Redis 或业务任务。

初始化包含当前命令、菜单、权限及 HTML 模板，但不作为运行时已导入官方数据或实机发送的证明。模板读取 VO 的字段，数据库只保存 HTML 和 URL/占位符，不保存图片二进制。附件由 VaultS3 管理；对象引用和生命周期见对应论坛服务。

SQL 检查夹具放在 `src/test/resources`，不放入业务代码。表数量和测试结果读取当前检查输出，不保存历史验收计数。
