# PostgreSQL 手工 SQL

## 当前入口

`init.sql` 是新建或明确重建空库的初始化基线，不是已有数据库的升级脚本。
2026-10-10 已按要求删除两份独立修复 SQL，不保留备份副本，不再执行旧步骤。
本轮只清理文件和说明，没有执行 SQL，也没有修改 `init.sql`。

**当前基线未完全对齐现有实体。** 本地核对发现：

- 翻译表仍是 `chinese_name/english_name`，实体使用 `locale/display_name`。
- 奖励池表仍是 `unique_name/reward_count/rewards_json`，实体使用 `pool_key/reward_items_json`。
- 基线仍创建已从当前社区模型移除的 `wf_community_command_alias`。

因此，不能把删除旧脚本理解为其内容已全部合并，也不保证仅执行当前基线即可通过 Hibernate
结构校验。后续结构变更应直接对齐 `init.sql`；若还需要已有库修复，再新增有明确适用基线的
`01_*.sql`，后续依次编号。本轮桌面数据方案中的六张新增社区表也尚未建表。

## 维护约定

- 数据库为 `nnmbot`，业务对象显式使用 `nnmbot.<table>`，不依赖 `search_path`。
- 不使用 Flyway；应用不自动执行 SQL，Hibernate 使用 `ddl-auto: validate`。
- 建表和改表提供 `COMMENT ON TABLE`、`COMMENT ON COLUMN`；约束、索引和枚举变更说明目的。
- `init.sql` 含 PostgreSQL 18 的 psql 专用指令，应使用匹配客户端；不是任意 GUI 通用脚本。
- 已有数据库不得直接重放基线；执行结构变更前先核对结构、备份和适用范围。
- 社区业务数据通过登记的在线来源拉取、预览和采纳，不增加远程社区初始化种子。
- 完全无外部来源的数据集才属于补充数据；社区 `SELF_MAINTAINED` 兜底不参与来源同步。

## 验证边界

`scripts/database/verify-warframe-sql.cjs` 仍引用本轮删除的归并脚本，当前不能按旧入口直接运行。
本轮按“不修改代码”要求保留验证脚本未动，后续需一起对齐基线、夹具和验证入口。
其他 SQL 验证脚本的适用性本轮未重新验收；历史通过记录不代表此次文件清理后仍能通过。
所有数据库验证只应使用隔离临时 PostgreSQL，不连接应用数据库。

表和数据来源的扩展方案见[桌面最终方案](/Users/ren/Desktop/Warframe数据导入最终方案.md)；
结构和格式明细见[任务整理](/Users/ren/Desktop/数据导入任务整理.md)。
