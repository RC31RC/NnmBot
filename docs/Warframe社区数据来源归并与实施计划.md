# Warframe 社区数据来源归并与实施计划

> 本文件保留历史实施过程。2026-10-10 的扩展来源、表设计和二分类结论以
> [桌面最终方案](/Users/ren/Desktop/Warframe数据导入最终方案.md)及
> [任务整理](/Users/ren/Desktop/数据导入任务整理.md)为准；
> 下文历史单遗物来源、排除名称词典等取舍不再约束新方案。查询可以由 Service 选择整条候选
> 或按业务规则字段合并，Repo 与数据库保留多个来源。两份独立修复 SQL 已删除，
> 基线及旧验证入口仍需后续对齐，本轮没有修改代码或 `init.sql`。

## 1. 当前业务理解

NnmBot 的官方 Public Export、官方 WorldState 和社区静态数据是三条不同的数据边界：

- 官方数据保存官方结构化数据和官方本地化字段，进入官方数据表；
- WorldState 是运行时动态数据，运行时由 WorldState Provider/Service 读取，不把动态快照当成社区静态数据；
- 社区数据保存来自参考项目、数据采集项目或清洗项目的静态数据，进入社区数据表。

多个上游 JSON 只要业务用途相同，就进入同一张社区业务表。上游来源不是互相独立的业务表，而是同一业务表的候选来源。刷新时保留来源身份，运行时和目录查询时按照来源优先级选择记录：同一业务键有多个来源时使用优先级最高的完整记录；高优先级来源没有整条记录时，才使用低优先级来源的完整记录。不跨来源拼接字段或数组。

社区数据记录有两种维护类型：

- `COMMUNITY`：由登记的上游来源同步；
- `SELF_MAINTAINED`：管理员在管理端维护，不能被上游刷新覆盖、停用或删除。

`SELF_MAINTAINED` 是社区数据表中的本地维护记录，不等同于独立的“补充数据表”。是否属于社区数据，取决于表的业务职责和是否存在上游来源；维护类型只表示记录的维护方式。

后台 `bot_command_alias` 是 NnmBot 命令配置，不属于 Warframe 社区数据，继续保留。

## 1.1 统一来源配置模型

社区数据来源统一配置在 `nnm.warframe.community-data.datasets`：

```yaml
datasets:
  reward_pool:
    - source-code: warframe-public-export-plus:ExportRewards.json
      urls:
        - https://example.test/ExportRewards.json
  map_feature:
    - source-code: warframe-hub:data/json/geo/vallis/labels.json
      url: https://example.test/labels.json
      world: vallis
```

- YAML 映射的键是业务数据集，值始终是来源列表；
- 一个来源列表项代表一个可审计的 `source-code`；
- 同一数据集配置一个列表项表示单来源，配置多个列表项表示多来源；
- `url` 和 `urls` 都属于同一个来源，`urls` 只是该来源的候选/故障转移地址；
- `world` 是 `map_feature` 记录的开放世界分类，不是连接地址或 Redis 配置；
- 旧的 `dataset-urls`、`dataset-manifests` 和 Java 内部 `DatasetFile` 命名不再使用。

代码绑定类为
`nnm-warframe/.../config/properties/WarframeCommunityDataProperties.java`，使用
`getDatasets()` 和 `DatasetSource`。刷新服务按来源列表独立拉取、解析和比对，不能把
一个来源失败当成空数据。

## 2. 不采纳为正式社区数据的来源

以下内容不进入当前项目的正式社区数据数据集：

- `warframe-info-api/utils/dict/wfTypeAlias.json`：NyxBot 的命令/WorldState 类型输入映射，当前 NnmBot 的命令已经通过明确插件命令和命令别名处理，不读取这套映射；
- `warframe-info-api/api/dataSource/factionMission.js`：参考项目内部解析辅助规则，不是当前 NnmBot 必须导入的静态数据。当前 NnmBot 直接读取 WorldState 内存模型，任务类型和阵营使用已登记的 WorldState 数据来源；
- `wfExtraTerms.json`：参考项目自身的补充词条和拆分规则，不作为一份完整、可独立刷新、可审计的正式社区来源；
- `KingPrimes/DataSource/warframe/translation.json`：主要服务 NyxBot 按路径末段截取查询的内部结构，不适合作为当前项目的普通物品翻译来源；
- `Relics.min.json`：与 `Relics.json` 来自同一项目，内容是压缩/精简变体，当前保守方案使用信息更完整的 `Relics.json`；
- `warframe-items/data/json/Relics.json`：与当前保守方案重复，暂不登记为遗物刷新来源；
- `warframe-public-export-plus/ExportRelics.json`：不是官方 Public Export；当前保守方案不使用它，避免同一遗物业务同时引入另一套主来源；
- `wf_community_command_alias`：当前插件命令链不读取，删除社区数据集登记和实体映射，但不删除后台 `bot_command_alias`。

## 3. 翻译、别名和术语边界

### 3.1 普通物品翻译

正式来源：

- `warframe-public-export-plus/dict.zh.json`
- `warframe-public-export-plus/dict.en.json`

两份字典使用相同的完整键集合，但一个记录中文值，一个记录英文值。写入社区翻译表时，每条记录的身份必须包含：

```text
sourceCode + datasetCode + recordKey + locale
```

`recordKey` 保留完整路径，例如：

```text
/Lotus/Language/Missions/MissionName_Ascension
/Lotus/.../TierATableARewards
```

不能只截取最后一段，也不能只按 `recordKey` 去重，否则中文记录和英文记录会互相覆盖。

官方 Public Export 的本地化字段仍属于官方数据。运行时解析时，官方数据优先；社区字典用于补充官方没有直接提供可用中文显示名的记录。

### 3.2 社区别名

普通社区别名表只表示玩家输入名称、黑话到标准物品名称的映射，例如中文简称、市场名称或玩家俗称。它不表示官方路径的翻译。

当前项目的命令别名由后台 `bot_command_alias` 和插件命令注册负责，不把 NyxBot 的 `wfTypeAlias.json` 误放入社区别名表。

### 3.3 社区术语

`term.json` 只用于参考项目整理出的术语、阵营、玩法词汇或解释性词条。它不承担完整路径翻译，也不承担命令输入解析。

## 4. 奖励池来源和统一结构

正式来源优先级：

1. `warframe-public-export-plus/ExportRewards.json`
2. `KingPrimes/DataSource/warframe/reward_pool.json`
3. `warframe-info-api/api/dataSource/reward_pool_local.json`

三者都进入 `wf_community_dataset_reward_pool`。来源优先级按完整奖励池记录选择：

- 同一个完整奖励池键在多个来源存在时，只保留优先级最高来源的完整记录；
- 高优先级来源没有该完整奖励池键时，才使用低优先级来源的完整记录；
- 不跨来源拼接奖励数组、阶段、概率、数量或其他字段；
- 不把数组位置当作跨来源的稳定业务键；
- 完整奖励池路径和完整物品键必须保留。

统一的奖励项字段为：

```text
stage
itemKey
displayName
count
probability
rarity
```

其中：

- `stage` 是奖励阶段，从 1 开始；
- `itemKey` 是内部物品键，例如 `/Lotus/StoreItems/...`；
- `displayName` 是通过完整 `itemKey` 查询官方或社区翻译得到的玩家可读名称；
- `count` 是数量；
- `probability` 使用 0 到 1 的小数保存，例如 `0.2` 表示 20%；
- `rarity` 保存 `COMMON`、`UNCOMMON`、`RARE` 等稀有度；
- `probability` 和 `rarity` 可以分别为空，不能强行从稀有度反推概率。

`ExportRewards.json` 的完整阶段结构会被标准化为奖励项数组，同时保留原始阶段和原始字段，便于审计和后续处理。它的路径不会通过“最后一段”翻译。奖励池的 `probability` 型记录与 `Relics.json` 中只有 `rarity`/`chance` 的遗物奖励明细属于两个不同业务数据集，不互相归并。

合法的空 JSON 代表该来源当前明确没有记录，可以生成删除预览，由管理员决定是否提交。HTTP 失败、非 JSON、HTML、重定向异常、解析失败不能当成空 JSON。

## 5. 翻译来源优先级

普通翻译运行时优先级：

1. `SELF_MAINTAINED`
2. 官方 Public Export 的官方本地化字段
3. `warframe-public-export-plus/dict.zh.json`
4. `warframe-public-export-plus/dict.en.json`

英文记录不能因为中文记录存在而被删除。中文和英文是同一个完整键的不同语言投影，不是同一数据库身份。

奖励项的展示名解析按完整 `itemKey` 查找。解析不到时保留内部键，不使用 NyxBot 的路径末段兜底规则。

## 6. 遗物保守方案

当前阶段只使用：

- `warframe-relic-data/data/Relics.json` 作为遗物唯一正式来源；
- 同一份 JSON 一次解析为遗物主表、遗物奖励明细表、遗物掉落明细表；
- 三张表使用关联键保存父子关系，并共享一次刷新预览和提交事务；
- `Relics.min.json` 不使用；
- `warframe-items/data/json/Relics.json` 暂不作为当前保守方案的刷新来源；
- `warframe-public-export-plus/ExportRelics.json` 暂不使用，也不登记为当前遗物刷新来源。

待系统稳定后，如果实际数据证明其他来源确实需要补充遗物的缺失整条记录，再单独评估来源登记和完整记录优先级；本轮不做遗物多来源归并，也不做字段级拼接。

## 7. 代码修改范围

### 后端

- 删除 `command_alias` 社区数据集、来源登记、实体/仓储/详情映射和目录读取分支；
- 删除 KingPrimes 普通翻译来源及其配置；
- 保留后台命令别名配置；
- 扩展 `ExportRewardsParser`，兼容 probability-only、rarity-only 和完整内部键；
- 奖励池解析后通过完整 `itemKey` 解析 `displayName`；
- 奖励池目录读取改为按完整业务键选择来源优先级最高的整条记录；
- 翻译目录按 `recordKey + locale` 保留中英文记录；
- 保留刷新日志、空来源删除预览和 `COMMUNITY`/`SELF_MAINTAINED` 隔离。

### SQL

历史实施时未修改 `docs/sql/init.sql`，曾新增独立的社区归并修复脚本，用于：

- 删除已废弃的 `wf_community_command_alias` 及其序列、索引；
- 调整奖励池和翻译身份约束、字段注释；
- 保留 `bot_command_alias`；
- 使用显式 `nnmbot.` schema；
- 由开发者手工执行，不引入 Flyway。

2026-10-10 已按要求删除该脚本。当前基线仍保留旧翻译/奖励池字段和废弃社区表，
不能把历史验证通过等同于基线已经完整合并；后续结构对齐应遵循 `docs/sql/README.md`。

### 前端

- 删除命令类型别名数据集；
- 调整数据集名称，使“来源、业务用途、是否可刷新”更明确；
- 奖励池展示完整奖励池键、阶段、物品键、中文名、数量、概率和稀有度；
- 保留社区来源与自维护记录的状态区分。

## 8. 验证计划

后端：

```bash
sh mvnw -pl nnm-app -am test
```

前端：

```bash
cd /Users/ren/RcGit/nnm-admin-web
npm exec vue-tsc -- --noEmit
npm run lint -- src/views/warframe/community-datasets.vue src/types/api/api.d.ts
```

重点验证：

- 同一完整翻译键的中文和英文记录同时存在；
- 只存在 `probability` 或只存在 `rarity` 的奖励项都能导入；
- 完整奖励池路径不会被截断；
- 多来源同一业务键按优先级选择完整记录，缺少整条记录时才由低优先级来源补充；
- 奖励池来源和遗物奖励明细不会被错误合并为同一业务记录；
- 合法空来源产生删除预览；
- 自维护记录不会被上游刷新覆盖或删除；
- 已删除命令类型别名后所有枚举、映射、前端分支和 SQL 仍一致。

官方 Public Export 刷新 500 和真实上游数据联调仍需要运行环境验证，不能仅凭本地编译视为已修复。

## 9. 本轮完成与验证记录

以下为当时的代码、管理端和数据库脚本完成记录，不表示 2026-10-10 扩展方案已完成：

- 后端已移除社区 `command_alias` 数据集和 KingPrimes 普通翻译来源，保留后台
  `bot_command_alias`；
- 翻译已按 `sourceCode + datasetCode + 完整 recordKey + locale` 保存，中英文不会互相覆盖；
- 奖励池已按完整池键选择完整来源记录，统一保存阶段、完整物品键、显示名、数量、概率和稀有度；
- `ExportRewards.json` 的原始 stages 和原始字段保留在快照，非法物品键、数量、阶段和概率不会伪装成空池；
- 遗物刷新来源已收敛为 `warframe-relic-data/data/Relics.json`，主表和两类明细共享刷新边界；
- 运行时名称解析已按自维护、官方可用本地化、plus 中文、plus 英文的顺序处理，无法解析时保留完整键；
- 管理端已展示来源、维护类型、双语记录和奖励明细，奖励明细展示完整键、概率与稀有度；
- 当时新增独立手工归并脚本，未修改 `docs/sql/init.sql`，并补充隔离 PostgreSQL 验证脚本
  和历史数据夹具；该归并脚本已在 2026-10-10 删除，验证入口尚未对齐。

初始实施验证（历史记录）：

- `sh mvnw -pl nnm-app -am test`：BUILD SUCCESS，各模块共统计 616 项测试，0 失败、0 错误，
  10 项环境条件测试跳过；其中 Warframe 模块 411 项全部通过；
- `PG_BIN=/Library/PostgreSQL/18/bin node scripts/database/verify-warframe-sql.cjs`：
  隔离 PostgreSQL 初始化、归并、非法数据整体回滚和 DDL 回滚均通过；
- 前端 `npm exec vue-tsc -- --noEmit`：通过；
- 前端定向 `npm run lint -- src/views/warframe/community-datasets.vue src/types/api/api.d.ts`：通过；
- 使用本地管理端 `http://127.0.0.1:3006/` 配合 mock 管理 API 的浏览器检查：桌面端和移动端均覆盖
  双语记录、自维护隔离、完整奖励键、概率/稀有度和奖励详情布局。

### 9.1 后续修复与统一入口（2026-10-08）

- 来源配置统一为 `nnm.warframe.community-data.datasets`，单来源和多来源使用同一个来源列表模型；
  节点三张表、遗物保守来源和 `map_feature.world` 的职责见
  [项目上下文与续作指南](项目上下文与续作指南.md)。
- 修复 Jackson 2 `JavaType` 传入 Jackson 3 RDT 导致的 `Unrecognized Type`：
  缓存使用 `Object.class` 读取，header、记录和差异再按目标类型转换；已使用实际 RDT `JsonCodec`
  验证往返读取及采纳。
- 预览记录与差异按 RDT 实际 JSON UTF-8 字节数分片，每片最多 256 KiB；全部分片完成后才发布
  轻量 header，再写待确认索引。缺失或损坏分片拒绝查看和采纳，写入失败补偿清理全部计划键。
- 管理端预览按来源、再按新增/修改/删除折叠，支持来源全选、变更类型全选和跨页选择。
- 仲裁排期登记为 `arbitration_schedule`，来源为 `browse.wf:arbys.txt`，落入
  `wf_arbitration_schedule`；统一社区页面支持查询、刷新、分页预览和选中采纳。
  `refreshAll` 会包含已配置的仲裁排期，删除候选仅限上游覆盖窗口。
- 仲裁排期的元数据为 `refreshable=true`、`manageable=false`、`readOnly=true`：
  允许采纳上游新增、修改和删除，不允许人工新增、上传（包括显式指定来源）、修改或删除。
  后端校验所有人工入口，前端按 `manageable && !readOnly` 控制人工操作入口。
- 当前没有独立仲裁定时任务绕过审核写库。旧专用仲裁预览接口仅保留兼容，不能与统一社区预览 ID 混用。

Redis 采用完整后发布、完整性检查和失败补偿，不是多键原子事务，也不是与 PostgreSQL 的跨系统事务。
选中业务变更与幂等回执在 PostgreSQL 同事务提交；回滚保留预览，提交后 Redis 清理失败也不能重放。

### 9.2 最新回归验证（2026-10-08）

- `sh mvnw -pl nnm-app -am test -q`：退出码 0；106 份 Surefire 报告共 641 项测试，
  0 失败、0 错误、10 项跳过，其中 Warframe 模块 431 项全部通过。
- `CommunityRefreshTransactionTest` 12 项通过，覆盖统一仲裁查询和选中采纳、窗口限定删除、
  数据库基线变更拒绝、回滚重试、幂等防重放、Redis 清理失败以及全部人工入口的只读校验。
- `WarframeCommunityDatasetRefreshAllTest` 4 项通过，包含已配置仲裁排期的统一批量刷新。
- 本次文档收尾未启动前端、未连接真实应用数据库执行迁移，也未重复执行上述历史 PostgreSQL、
  浏览器和前端校验。真实环境验收仍按下述边界跟踪。

真实应用数据库迁移没有执行。执行迁移前仍需按 `docs/sql/README.md` 备份数据库、停止旧写入并清理
旧 Redis 预览；执行后重启后端清理缓存。官方 Public Export 刷新 500、真实 Redis 多实例长任务以及
真实上游下载仍需在目标运行环境完成验收，不能用本地 mock 或隔离数据库验证替代。

### 9.3 多来源并行与预览存储收敛（2026-10-09）

- 同一数据集的多个来源由 `WarframeCommunitySourceLoader` 提交到有界的
  `communitySourceExecutor` 并行下载和解析；协调线程统一汇总完成进度，结果最后按配置顺序恢复，
  不会因为并行完成顺序改变来源优先级。
- 每个来源内部由 `WarframeCommunityApiClient` 按配置的 CDN 地址顺序依次尝试。HTTP 失败、空响应、
  HTML 错误页和解析失败都只标记当前来源失败，不影响其他来源继续完成。
- `WarframeCommunityDatasetService` 只将成功来源加入比对和删除范围。失败来源保留数据库现有数据，
  不产生该来源的新增、修改或删除候选；预览仍返回成功来源 diff 和失败来源摘要，便于审核时判断
  本次刷新是否完整。
- 重复的来源加载、状态统计、预览快照存取和 Redis 清理职责已分别收敛到
  `WarframeCommunitySourceLoader`、`WarframeCommunityApiClient`、数据集 Service 和
  `WarframeCommunityPreviewStore`。Service 保留业务优先级、diff 决策、并发基线和事务边界，
  Store 不参与来源请求、业务合并或数据库事务。
- Redis 预览使用轻量 header、记录/差异分片和独立分组索引；分片按实际 RDT JSON UTF-8 字节数控制，
  当前目标上限为 256 KiB。与 upstream 相同的 `current` 和默认空 `manualFields` 不重复保存，
  但 `originalJson` 作为并发校验基线完整保留；读取时兼容旧的未分片和未压缩格式。
- Redis 写入采用“分片和索引完成后发布 header”的顺序，读取分页只访问相交记录分片，正式合并仍校验
  完整快照。压缩、分片或清理的单项失败不会把其他补偿步骤静默跳过；数据库事务提交后才消费预览，
  因此缩减缓存占用不改变审核、回滚和幂等语义。

本轮验证：

- `sh mvnw -pl nnm-app -am test`：本轮生成的 110 份 Surefire 报告共 663 项测试，0 失败、0 错误、
  10 项环境条件测试跳过；其中 Warframe 模块 448 项全部通过。
- 新增 `WarframeCommunityPreviewStoreTest` 6 项通过，覆盖范围读取、字段恢复、压缩基线保留、
  清理失败隔离以及旧预览清理不误删新 pending。
- 上述结果主要验证 mock、单元和集成测试中的并发及 Redis 存储协议；尚未替代真实 CDN、多实例
  Redis、真实上游数据和目标应用数据库的长任务验收，也未执行生产打包或数据库迁移。
