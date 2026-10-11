# Warframe 数据流向与命令取数说明

本文件描述当前代码链路，不把设计确认当成完成验收。
来源顺序、已知缺口和验收边界见 [数据来源待决策与实现差距](Warframe数据来源待决策与实现差距.md)。

## 1. 四条数据链路

```text
官方动态 WorldState -> 拉取/解析 -> JVM 运行时快照 -> 实时命令
官方 Public Export -> 静态导出解析 -> 独立官方记录/明细表 -> 官方目录和物品引用
社区静态资料 -> 主动刷新/预览/人工采纳 -> 对应社区业务表 -> 业务 Service
仲裁排期 -> 统一社区数据集刷新/Redis 预览/人工采纳 -> wf_arbitration_schedule -> 仲裁 Service
```

- 动态 WorldState 不写入静态社区表。
- WorldState 项目的 `missionTypes.json`、`solNodes.json`、`sortieData.json` 等是静态代码簿，不是动态快照。
- Public Export 和 plus 社区资料分表保存，不能把 plus 当作官方数据写回官方记录。
- 仲裁排期与精华规则是两个业务模型和两张表。
- Warframe Market 报价/拍卖来自外部查询 API，不属于社区静态快照。
- 同功能社区来源共享业务模型，以 `sourceCode` 区分；`recordSource` 只区分 COMMUNITY 与 SELF_MAINTAINED。

## 2. 初始化

空库初始化入口为 `docs/sql/init.sql`，不重放已删除的旧脚本：

```sh
/Library/PostgreSQL/18/bin/psql -h localhost -U <本地用户> -d nnmbot \
  -v ON_ERROR_STOP=1 -f docs/sql/init.sql
```

数据库需事先建立；脚本建立 `nnmbot` schema，不能对已有 schema 当升级脚本重跑。
它包含 psql 专用指令，当前按 PostgreSQL 18 格式维护。
应用仍使用 `ddl-auto: validate`，不会自动建表或迁移。
2026-10-10 已按要求删除两份独立修复 SQL，本轮未修改 `init.sql`。
当前基线的翻译、奖励池字段和废弃社区表仍未完全对齐现有实体，不保证初始化后可直接通过
Hibernate 校验；旧 SQL 验证入口也仍有依赖缺口，详见 `docs/sql/README.md`。

2026-10-01 已通过隔离 PostgreSQL 18 的三套 SQL 校验，未连接或修改应用数据库。
远程社区种子不重新加入；仅保留 19 条本地自维护记录，即 13 条 Market 组件规则和 6 条 Riven 别名/组合规则。
官方导出、仲裁排期和新的赏金定义初始为空。初始化成功不代表所有采纳来源已接通刷新。

## 3. 普通社区刷新

核心代码：

- `service/impl/WarframeCommunityDatasetServiceImpl.java`
- `service/WarframeCommunityDatasetLock.java`
- `service/WarframeCommunitySourceLoader.java`
- `client/WarframeCommunityApiClient.java`
- `cache/WarframeCommunityPreviewStore.java`
- `application/WarframeCommunityDatasetProjectionMapper.java`
- `constant/WarframeCommunityDataset.java`
- `constant/WarframeCommunitySource.java`
- `model/entity/WarframeCommunityMergeReceipt.java`

```text
主动刷新
  -> 立即尝试取得数据集分布式锁
  -> 检查并复用现有有效待确认预览
  -> SourceLoader 并行下载、纯解析登记来源，每个来源的 CDN 按顺序回退
  -> 成功 sourceCode 独立和数据库 COMMUNITY 记录比较，失败来源保留原数据
  -> PreviewStore 保存新增/修改/删除操作、成功来源范围和失败摘要
  -> 释放刷新锁，后台任务/SSE 通知完成
  -> 前端查看、勾选操作
  -> 提交 previewId + selectedOperationIds
  -> 再次取得同一数据集锁，校验预览和数据库基线
  -> 数据库事务执行选中操作并写幂等回执
  -> 提交后失效缓存、清理预览，事务完成后释放锁
```

### 3.1 候选与来源隔离

差异分类仍包含 ADDED、CHANGED、REMOVED、UNCHANGED，但 Redis `records` 只有前三类。
不变只用于统计；SELF_MAINTAINED 不进入 diff。普通刷新不再调用旧人工字段合并引擎。
未选中不写库，既不等于 KEEP 写入，也不等于停用。删除操作是物理删除。

同来源使用完整业务键比较：普通表包含 `recordKey`，必要时包含 locale；突击还包含 recordType。
同表不同来源分别保存，不能先按读取优先级压成一条再 diff。
同一数据集的来源在独立有界执行器中并行下载、解析，每个来源内部按配置顺序尝试 CDN，
不会同时下载多个副本。来源线程不查询数据库；结果按配置顺序汇总，再在协调线程比对。
下载或解析失败只影响该来源，既不修改其原数据，也不生成删除候选；成功来源仍可独立审核。
`sources` 同时返回成功与失败摘要，包含状态、脱敏地址、耗时、记录数、错误和操作定位信息。
全部失败仍生成可查看的无操作预览，但不写待确认索引；合法空快照才可生成该来源删除候选。
非法 JSON、重复对象键、尾随文档、重复业务键会使相应来源失败；
不同上游 schema 的完整性仍需继续逐项加强。

新增操作预分配 RDT 雪花 ID，采纳时沿用；已有操作使用原数据库 ID，前端使用字符串。
`recordKey` 是业务身份，不是操作 ID。请求不携带前端拼装的写库 JSON：

```json
{"previewId":"服务端预览ID","selectedOperationIds":["服务端操作ID"]}
```

预览不可由查看者修改。成功提交关闭整个批次，未选项一起丢弃，但不会永久忽略；
上游下次仍有差异可再次提出。过期只丢弃预览，不修改正式记录。
管理端普通列表/预览分页不会限制命令服务的读取量。
普通预览接口以 `current`、`size` 查询参数分页，每页最多 100 条；
`operations` 使用公共 `PageResult`，摘要仍是整个批次的计数。
上传入口同样只返回第一页，Redis 保留完整操作集合。
页面按来源、再按新增/修改/删除折叠展示；分组返回整个预览的操作 ID，不只返回当前页。
页面按操作 ID 保存跨页选择，默认不勾选，支持来源全选、类型全选、本页全选及完整前后值；
分页不自动采纳未查看的记录，也不再提供人工字段冲突处理选项。

### 3.2 锁、人工操作和失败恢复

- 刷新、合并和人工写入共享数据集锁，立即获取，不自旋等待。
- 待确认批次保护其完整来源范围，包括本批不变记录；管理员等待期间不长期持有锁。
- COMMUNITY 不提供直接人工编辑或删除；上游删除候选只能通过预览采纳后物理删除。
  SELF_MAINTAINED 在允许人工管理的数据集中独立新增、修改和删除。
- 写锁通过事务同步在事务完成后释放，不能仅依赖方法返回。
- 事务回滚不销毁预览，可使用同一批次重试。
- 幂等回执和业务变更同事务落库，Redis 写入/删除全部失败也不能重放。
- 旧批次的重复访问只补偿自身缓存清理，不移除后来生成的新待确认索引。
- 当前固定租约配置尚未有真实 Redis 多实例长事务/失锁验收，不能据单元测试宣称所有竞争边界完成。

来源不唯一的数据集目前拒绝没有明确来源的上传，避免将资料归给错误来源。
管理端上传选择来源协议尚未补齐；主动刷新仍按 `application-bot.yml` 的
`nnm.warframe.community-data.datasets` 来源列表获取所有来源。

### 3.3 Redis 分片与发布

存储协议统一由 `cache/WarframeCommunityPreviewStore` 承担，数据集 Service 只提供完整业务快照、
来源摘要和分组，并控制提交后消费时机。记录和差异使用同一分片算法，不向 common 提前泛化。

记录、字段差异分别分片保存；每片按 RDT 实际 JSON UTF-8 字节数限制到 256 KiB，
包括括号、逗号和字符串转义。单条超限在首次缓存写入前失败，不通过扩大 Netty 线程数
掩盖大值写入。header 只含计数、来源、分片数和状态，不再重复保存完整记录和差异。

记录省略与 upstream 相同的 current 和默认空人工字段，读取时恢复；original 并发校验基线完整保留。
记录、差异与独立分组索引按需使用 `gzip1:` Base64 GZIP，只有实际存储值更小时才压缩；
兼容旧对象分片、未压缩 JSON 字符串和旧 header 内嵌分组。分片限制单次写入大小，压缩和去重
减少总存储占用，两者作用不同。

普通分页只读取当前页相交的记录分片，不为一页数据反复装载整个预览；旧批次或遗物子表缺少
合适索引时回退完整组装。正式合并仍读取全部记录和差异并校验基线。

全部记录、差异分片和分组索引写完才写 header，header 是发布标记，待确认索引最后写。
分页校验被读取的分片，正式合并要求全部声明分片均存在且格式有效。写入异常补偿清理全部
计划分片、分组、header 和待确认索引，包括“值写成功、失效通知失败”的情况。
各键独立尽力清理，单个清理失败不阻断其余步骤；无法清理的孤立键最终按 TTL 过期。

这不是 Redis 多键原子事务。与 PostgreSQL 之间也没有跨系统事务；真正的数据库原子性
由选中变更和幂等回执同事务提交保证，提交后再清理缓存，回滚保留预览。

业务 ObjectMapper 为 Jackson 2，RDT 为 Jackson 3。缓存读取使用 `Object.class`，
已是目标实例则直接使用，Map 按单条转换；不得把 Jackson 2 `JavaType` 传给 RDT。

## 4. 来源、业务表和消费链

实际 URL、镜像和来源列表在 `application-bot.yml`，外部访问沿用配置化客户端。
来源登记不代表已经有命令消费者；参考过一个项目也不代表运行时依赖其本地目录。

### 4.1 KingPrimes/DataSource

| 文件 | 数据集 | 主要用途 |
| --- | --- | --- |
| `alias.json` | alias | 输入别名识别 |
| `nodes.json` | nodes | 节点信息 |
| `reward_pool.json` | reward_pool | 奖励池候选 |
| `state_translation.json` | state_translation | 状态、任务、挑战名称/说明 |
| `market_element.json` | market_element | Market 属性资料 |
| `market_riven_tion.json`、`market_riven_tion_alias.json` | 同名数据集 | Riven 属性/别名 |
| `riven_analyse_trend.json`、`riven_trend.json` | 同名数据集 | Riven 趋势 |
| `term.json` | term | 术语，当前远程刷新仍关闭 |

普通记录分别落入 `wf_community_dataset_*` 等专用表；术语表为 `wf_community_term`。
不能因为术语存在上传入口就认定它已支持完整主动远程刷新。
`translation.json` 的路径末段结构不再作为普通物品翻译来源，历史社区行停用并保留审计。

### 4.2 WorldState 静态代码簿

`missionTypes.json`、`solNodes.json`、`factionsData.json`、`syndicatesData.json`、
`operationTypes.json`、`upgradeTypes.json`、`persistentEnemyData.json`、
`fissureModifiers.json`、`languages.json` 各自进入对应的静态业务模型。

`sortieData.json` 现在统一进入 `wf_community_worldstate_sortie`，以 MODIFIER/BOSS 区分类型；
不再使用两张独立的修正/首领业务表。

动态链路仍为 `WarframeOfficialApiClient -> WorldStateParser -> WarframeWorldStateServiceImpl`。
`WorldStatePollingTask` 更新内存状态，不把动态数据导入代码簿。

### 4.3 节点数据的三张表边界

参考项目 `NyxBot` 的 `NodeService.initData()` 同时初始化官方
`ExportRegions` 和 KingPrimes `nodes.json`。本项目保留这两类数据，但按职责拆分为三张表，
因此管理端社区数据集中的“节点”只对应 KingPrimes 来源，并不是漏展示来源：

| 表 | JSON 来源 | 记录职责 | 是否属于社区“节点”数据集 |
| --- | --- | --- | --- |
| `wf_official_region` | 官方 Public Export `ExportRegions` | 官方节点身份及稳定的星图结构化字段 | 否，随官方导出刷新 |
| `wf_community_dataset_nodes` | `KingPrimes/DataSource/warframe/nodes.json` | 社区节点补充资料，保留 `sourceCode`，参与社区预览/采纳 | 是 |
| `wf_community_worldstate_node` | `warframe-worldstate-data/data/zh/solNodes.json` | WorldState 引用代码簿，解释动态状态中的节点代码、阵营和任务类型 | 否，属于 WorldState 代码簿 |

节点引用解析由 `WarframeReferenceResolverService` 按
`自维护 -> 官方 ExportRegions -> KingPrimes nodes.json -> WorldState solNodes.json`
组合候选；这只是节点引用解析的局部优先级，不是所有社区来源的全局优先级。

### 4.4 plus 赏金与奖励

| 文件 | 当前落点 | 消费状态 |
| --- | --- | --- |
| `ExportBounties.json` | `wf_community_bounty_definition` + `wf_community_bounty_stage` | 定义及有序阶段整体采纳，按动态 jobType 补充名称/说明/阶段 |
| `ExportRewards.json` | `wf_community_dataset_reward_pool` | 完整池键与物品键、阶段、数量、0 到 1 概率、稀有度结构化；原始阶段保留 |
| `supplementals/riven_unrollables.json` | `wf_community_riven_unrollable` | 已登记刷新，尚无明确命令消费 |
| `dict.zh.json`、`dict.en.json` | `wf_community_dataset_translation` | 来源 + 完整键 + locale 的单语言 displayName 投影；中英文同时保留 |

ExportBounties 是静态任务定义，不提供“当前有哪些赏金”。先有动态任务才补充静态定义；
动态实例时间、状态、敌人等级和已有奖励不由静态文件替代。

奖励池另有 KingPrimes `reward_pool.json` 和 info-api `reward_pool_local.json`。
`WarframeCommunityCatalogServiceImpl.rewardPools()` 按已确认顺序选完整有效记录：

```text
SELF_MAINTAINED > plus > KingPrimes > info-api
```

高优先级来源的合法空池也遮蔽低优先级记录，只有缺少整条池记录时才回退；不跨来源拼接奖励数组。
此顺序不推广为翻译、节点和遗物的全局顺序。`probability` 与 `rarity` 分别可空，不从稀有度或
遗物 `chance` 推导奖励池概率；读取时通过完整 `itemKey` 动态解析展示名，未解析保留内部键。

### 4.5 仲裁排期与精华

排期来自配置的 browse.wf `arbys.txt`，登记为 `arbitration_schedule`，
由 `WarframeCommunityDatasetServiceImpl -> ArbitrationScheduleParser ->
WarframeArbitrationStoreImpl` 写入 `wf_arbitration_schedule`。
管理端入口是“Warframe 社区数据 -> 仲裁排期”，不是额外的刷新按钮；
可以查询已采纳排期，刷新后分页查看、按来源/变更类型选择并采纳。

排期元数据为 `refreshable=true`、`manageable=false`、`readOnly=true`。
只读限制的是人工新增、上传、修改和删除，不限制刷新和采纳上游变更；这些限制由后端校验，
前端按 `manageable && !readOnly` 控制人工入口。统一批量刷新会包含已配置的仲裁排期。

排期以 activation 为数据库主键；新增候选使用 RDT 操作 ID，已有候选使用 activation
字符串，前端统一按字符串保存选择。删除候选仅限本次上游覆盖的时间窗口，窗口外历史或
未来记录不视为删除；与窗口重叠的旧任务仍参与比对。
已补数据库基线校验、事务回执、回滚重试和 Redis 清理失败防重放，不定时绕过确认写库。

旧 `WarframeArbitrationAdminServiceImpl` 及 `/warframe/arbitration/*` 保留兼容；
其旧预览协议不代表当前统一社区入口，不能与普通社区预览 ID 混用。

精华来源是 `warframe-info-api:routes/arbitrationEssence.js`：

- 安全提取完整 ESSENCE_TABLE，不执行 JavaScript，也不抓有限天数 `/arb/:days?` 响应反推规则。
- 进入独立 `wf_community_arbitration_essence`，使用普通社区刷新流程。
- 保留完整节点键，按精确字符串匹配；单位为精华/小时，缺失不默认零。
- 不采纳 `data/arbitration_essence.json`，不恢复旧初始化种子。

### 4.6 遗物、地图与其他资料

- `warframe-relic-data/data/Relics.json` 是唯一正式遗物来源，一次解析主表、奖励和掉落三张表，共享预览与采纳事务。
- `WarframeRelicServiceImpl` 已去掉管理端式的 20/50 条截断；遗物主从结构按完整业务对象刷新和读取。
- `Relics.min.json`、warframe-items 的 `Relics.json` 和 plus `ExportRelics.json` 不登记为刷新或回退来源；
  历史社区行停用，自维护父子记录保留。官方 `ExportRelicArcane` 仍作为独立官方数据保存。
- warframe-hub 鱼类和地图 GeoJSON 分别进入 `fish_guide`、`map_feature`，同表多文件按来源隔离，暂无明确命令消费。
- `market_component` 是本地自维护组件后缀映射。
- 社区 `command_alias` 已移除，命令别名只由后台 `bot_command_alias` 和插件命令注册处理；
  `wfExtraTerms.json` 不作为正式社区来源，`alias_local.json` 仍有接入缺口。
- info-api `state_translation_local.json` 当前登记为 `worldstate_rule` 汇总表，专用代码簿消费与它之间尚有映射缺口。

## 5. 官方 Public Export

`WarframeOfficialDataServiceImpl` 读取官方英文/本地化索引，按 `exportType + uniqueName` 保存官方记录，
保留逐条英文及本地化 JSON，同步官方结构化明细；不是字节级保存完整文件。

主要表有 `wf_official_export_record`、`wf_official_weapon`、`wf_official_warframe`、
`wf_official_upgrade`、`wf_official_relic_arcane`、`wf_official_region`、`wf_official_recipe`。
官方导入自身的 enabled 行为与社区物理删除协议不同，不应相互套用。

`ExportSortieRewards` 是官方奖励/相关挑战定义，不是 `sortieData.json` 的首领/修正代码簿。
已登记通用导入不代表所有官方业务字段都已有命令消费者。

## 6. 命令查询与翻译职责

```text
插件/命令 -> 业务 Service / Catalog -> Repository、内存快照或外部 API
```

Repository 只访问数据。目标是各业务 Service 返回干净唯一结果，命令不选择来源；
各业务 Service 负责自己的来源选择；当前顺序是可调整的实现默认值，不建立全局来源优先级。
缺少所有候选允许为空，不强行跨来源拼成来源不明的记录。

| 场景 | 主要链路 |
| --- | --- |
| 警报、裂隙、突击等 | WorldState 内存快照 -> 静态代码簿/引用解析 -> 输出 |
| 赏金 | 动态任务 -> 赏金静态定义 + 奖励池 Service -> 引用翻译 |
| 输入物品名/黑话 | `WarframeItemResolverService` -> 官方身份 + 社区别名/术语/名称 |
| 输出内部路径/代码 | `WarframeReferenceResolverService` -> 正式名称；`WarframeReferenceProjectionEnrichmentService` 补充相关业务资料 |
| Market/Riven | 物品识别和社区属性 -> Market 实时 API |
| 遗物/金银垃圾 | `WarframeRelicService` -> 遗物/奖励表，官方物品目录辅助名称解析 |
| 仲裁 | `ArbitrationCommandHandler` -> `WarframeArbitrationService` -> 排期表 + 精华规则补充 |

输入识别得到稳定业务键，输出翻译据此读取正式名称。这是两个职责，不把玩家黑话当最终展示名称。
参考 Nyx 的键和调用路径，但不复制它混合官方/社区存储的方式。
`WarframeItemNameService` 按完整键选择：自维护 > 可用官方本地化 > plus 中文 > plus 英文。
不能通过路径末段查询，解析不到时保留键。普通物品字典、输入别名、术语和命令别名互不替代。

## 7. SSE、续签与验证

现有前端 `utils/http/sse.ts` 使用统一 API 前缀，401 后自动续签重试；
`api/admin-task.ts` 在断线后查询当前任务状态并重连，通知可恢复到 previewId。
后端续签使用普通 `StpUtil.login()`；refreshToken 仍是旧协议兼容字段，未启用自定义双 Token。
SSE 是通知通道，不是最终写库数据的可信来源。

已补任务级断线恢复测试，尚未做浏览器端 401/断线集成验收。
真实 Redis 多实例、浏览器端 SSE 401/断线恢复和全来源空库刷新到命令的最终验收仍未完成。
运行测试、剩余来源和需用户决定的问题统一记录在 [待决策与实现差距](Warframe数据来源待决策与实现差距.md)。
