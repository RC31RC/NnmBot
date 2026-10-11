# Warframe 命令数据与参考项目追溯矩阵

说明校正：2026-10-08；逐命令验收记录保留其原验证日期，不代表本轮重新验收。
范围：`nnm-warframe` 的命令插件及待接入能力。代码侧锚点是每个命令注解下方的数据用途注释；WorldState 公共表见 `AbstractWorldStateCommandPlugin`，遗物公共表见 `AbstractRelicPlugin`。

## 使用边界

本矩阵用于追溯实现与历史验证，不表示每条命令都依赖全部参考项目。
来源到表的现行说明见 [数据流向说明](Warframe数据流向与命令取数说明.md)，
未闭环事项见 [实现差距](Warframe数据来源待决策与实现差距.md)。

- **运行时数据源**是当前进程实际读取的官方接口、Warframe Market API 或 PostgreSQL 表。
- **本地参考项目代码**用于分析协议、复核规则和追溯 JSON，不作为线上可执行依赖；
  社区业务资料通过登记的远端来源在线刷新，不能把“参考源码”和“实际远端数据源”混为一谈。
- **官方 WorldState**经 `worldState.php` 拉取后形成运行时快照；本机使用 JVM/cache2k，分布式模式还涉及 Redis 共享。链路为 `WorldStateParser -> WarframeCommandData -> WarframeReferenceProjectionEnricher -> WarframeWorldStateDisplayMapper`。
- **官方 PublicExport**的静态目录已落表；命令侧主要通过 `wf_official_export_record` 解析物品。
- **已验证**只记录已有定向测试、真实快照回归或明确来源阻塞，不把“已有代码”误记为已验证。

仲裁排期的现行刷新入口是社区数据集 `arbitration_schedule`，机器人只查询 PostgreSQL
已采纳记录，不在命令执行时下载 `arbys.txt`。2026-09-29 的仲裁真实联调使用旧专用预览接口；
现行分片协议、统一入口和只读人工校验已有自动化回归，但仍需真实环境验收。

### 缩写

| 缩写 | 含义 |
| --- | --- |
| `WS` | 官方 `worldState.php` 的当前 JVM 内存快照 |
| `WS-CB` | 公共 WorldState 代码簿：`wf_community_worldstate_node`、`wf_community_worldstate_mission_type`、`wf_community_worldstate_faction`、`wf_community_worldstate_operation_type`、`wf_community_worldstate_fissure_modifier`、`wf_community_worldstate_sortie`、`wf_community_worldstate_syndicate`、`wf_community_worldstate_upgrade_type`、`wf_community_worldstate_persistent_enemy`、`wf_community_worldstate_language` |
| `WM API` | 外部 Warframe Market API，订单或拍卖数据经 cache2k 缓存 |
| `官方目录` | 已入库的官方 PublicExport，当前主要为 `wf_official_export_record` |

## 参考项目与在线数据来源

| 参考项目 | 参考用途 / 数据来源 |
| --- | --- |
| `warframe-worldstate-parser`、`warframe-worldstate-data` | WorldState 字段、代码簿和引用解析规则 |
| `warframe-hub`、`warframe-status` | WorldState 展示字段、状态文案和渲染规则 |
| `warframe-public-export-plus` | 现有赏金、奖励池、双语字典及 unrollable 在线来源；新方案扩展业务 Export 与全语言字典，含遗物元数据，仍进入社区表而非官方表 |
| `warframe-relic-data` | 当前遗物来源；本地有效内容被 Items 覆盖，新方案不增加重复同步，不删除现存历史记录 |
| `warframe-nexus-query`、`warframe-info-api` | WM/紫卡协议、属性筛选、翻译和别名规则 |
| `warframe-items` | 新方案接收全部 25 个成品 JSON，进入物品、遗物、节点及翻译社区表；尚未实施 |
| `mod-generator` | 图片/配装渲染参考 |
| `KingPrimes/DataSource` | 九类现有 Warframe 在线来源；新方案补名称翻译，接收本地十个业务 JSON |

除十个 Warframe 参考项目外，还核对 `KingPrimes/DataSource` 和 `NyxBot`。
NyxBot 用于本地命令行为、别名和展示复核，不是独立静态 JSON 来源，不成为运行时依赖。

社区记录通过 `source_code` 和原始/上游 JSON、摘要追溯来源，不另设 `source_project/source_path`
两列。完整表与文件清单见[桌面最终方案](/Users/ren/Desktop/Warframe数据导入最终方案.md)。
当前初始化入口为 `init.sql`，两份独立修复 SQL 已删除；基线和旧验证脚本仍有对齐缺口，
见 `docs/sql/README.md`。下表是现有实现与历史验证，不能把新方案误写为已接通。

## WorldState 命令

| 命令代码（名称） | 运行时数据源 | 表及用途 | 离线参考 | 已验证状态 / 待补项 |
| --- | --- | --- | --- | --- |
| `archimedeas`（科研） | `WS.Conquests` | 无独立业务表；科研原始条件保留在 JVM 快照，WorldState 代码簿/补充数据负责术语解析 | `warframe-worldstate-parser`、`warframe-info-api` | 已接入 `Conquests` 解析、普通命令、管理端分区和蹲任务监听；仅含 `Conquests` 的快照校验已回归 |
| `deep-archimedeas`（深层科研） | `WS.Conquests`，过滤 `CT_LAB`（去空格、忽略大小写） | 同科研 | `warframe-worldstate-parser`、`warframe-info-api` | 已接入普通命令、管理端分区和蹲任务监听 |
| `hex-archimedeas`（时光科研） | `WS.Conquests`，过滤 `CT_HEX`（去空格、忽略大小写） | 同科研 | `warframe-worldstate-parser`、`warframe-info-api` | 已接入普通命令、管理端分区和蹲任务监听 |
| `descents`（深层下降） | 官方 `WS.Descents`，取首条周轮换；每条含 `RandSeed`、21 层 `Challenges` | 无业务表；轮换保存在 JVM 快照，管理端原始快照保留未知键 | `warframe-worldstate-parser` `RawDescent/Descendia`、NyxBot 深层下降测试 | 已接入 parser/model、聊天命令、管理端投影和蹲任务候选；待真实官方快照验证 |
| `weekly-challenges`（未注册命令） | 官方 `WS.WeeklyChallenges` | 无当前业务表；原始挑战保留在 JVM 快照 | `warframe-worldstate-parser` `WeeklyChallenge`、`warframe-info-api` 原始 WorldState 分支 | 已有解析模型，但参考 API 对该字段原样返回，没有专门格式化入口或稳定聊天命令语义，暂不新增命令 |
| `dark-sectors`（未注册命令） | 官方 `WS.BadlandNodes` | 无当前业务表；暗域节点原始状态保留在 JVM 快照 | `warframe-worldstate-parser` `DarkSector`、`warframe-info-api` 原始 WorldState 分支 | 已映射为 `darkSectors` 解析模型，但参考 API 对该字段原样返回，没有明确聊天命令语义，暂不新增命令 |

| 命令代码（名称） | 运行时数据源 | 表及用途 | 离线参考 | 已验证状态 / 待补项 |
| --- | --- | --- | --- | --- |
| `alerts`（警报） | `WS.alerts` | `WS-CB`：节点、任务、阵营、奖励引用 | WorldState 三项目 | 已真实历史快照回归：`countedItems` 数量、奖励展示和内部引用清洗 |
| `arbitration`（仲裁） | PostgreSQL 已采纳排期，上游为 browse.wf `arbys.txt` | `wf_arbitration_schedule`：小时排期存档；`WS-CB`：节点/任务/阵营补全；`wf_community_arbitration_essence`：精华/小时参考 | `warframe-info-api`；NyxBot `ArbitrationCache` / `ArbitrationPlugin` | 严格 `activation <= now < expiry`；文本解析、代码簿补全、查询、事务存档及失败保护回归通过；2026-09-29 旧入口真实验证 44,056 条下载、SSE 完成、权限 403、Redis 预览、仅勾选一条 PostgreSQL 合并；现行入口真实联调及等待预览过期待补 |
| `arbitration-table`（仲裁表） | 同上 | 同上，未来七天范围前十项，含当前；不是精华规则列表 | 同上 | 独立插件，共用仲裁 service/handler，失败展示存档与陈旧提示 |
| `today-arbitration`（今日仲裁） | 同上 | 同上，按默认上海自然日筛选已存档过去/未来任务 | NyxBot 数据链参考，日期视图为 NnmBot 扩展 | 日期边界与缺口回归通过；首次抓取前历史不可补造 |
| `efficient-arbitration`（高效仲裁） | 同上 | 同上，未来任务按任务类型/星球/阵营三组规则过滤；精华表不参与排期生成 | NyxBot `getArbitrationList()`；`draw-image-plugin-core` 的 `Arbitration.isWorth()` | 未来前十项，规则可配置；当前项排除与三组 AND 回归通过 |
| `archon-hunts`（执刑官猎杀） | `WS.archonHunt` | `WS-CB`：首领、任务、词缀、节点 | WorldState 三项目；`warframe-status` | 已真实快照回归：缺失词缀显示“未知条件” |
| `cycles`（平原、地球状态） | 有效上游周期优先；地球/山谷本地时钟；希图斯/魔胎、扎里曼以 `WS.SyndicateMissions` 的希图斯结束时间为锚点 | `WarframeCycleProperties` 管理山谷/扎里曼校准、赏金偏移及锚点有效期；双衍使用固定 52 秒校准 | `warframe-worldstate-parser` 的 Earth/Cetus/Vallis/Cambion/Zariman/DuviriCycle | 六种周期已接入；扎里曼按固定基准、5 秒修正和 2.5 小时阵营阶段计算，双衍按五个两小时阶段计算；边界、过期锚点、上游优先及不可变快照定向测试通过（2026-09-29） |
| `daily-deals`（每日特惠） | `WS.dailyDeals` | `WS-CB`、官方目录：商品内部标识 | WorldState 三项目；`warframe-public-export-plus` | 已有模块回归 |
| `duviri-cycle`（双衍王境） | 当前 `WS` 缺少或周期字段过期时由本地算法补足 | `WarframeCycleProperties.duviriEpoch`；输出仍保留原始快照不变 | `warframe-worldstate-parser/lib/models/DuviriCycle.ts` | 已接入五状态本地计算；对官方快照缺字段不再阻塞，定向边界测试通过；Circuit choices 仍依赖上游字段 |
| `entrati`（英择谛） | `WS.syndicateMissions` | `wf_community_dataset_state_translation`：任务翻译；`wf_community_dataset_reward_pool`：奖励补全；`WS-CB`：引用 | WorldState 三项目；`warframe-public-export-plus` | 已真实快照回归：隔离库赏金回退、奖励池、路径清洗 |
| `fissures`（裂隙） | `WS.activeMissions`，排除钢铁/九重天 | `WS-CB`：节点、任务、纪元、词缀 | WorldState 三项目；`warframe-status` | 已真实 Mongo 样本回归：`Hard`、`VoidT1..VoidT6`、普通/钢铁分流与纪元 |
| `invasions`（入侵） | `WS.invasions` | `WS-CB`：阵营、节点、任务、奖励 | WorldState 三项目；`warframe-hub` | 已真实快照回归：`Count/Goal`、双方奖励数量、路径清洗 |
| `nightwave`（电波） | `WS.nightwave` | `wf_community_dataset_state_translation`：挑战标题/说明；`WS-CB`：其他引用 | WorldState 三项目；`warframe-info-api` | 已回归：已收录挑战无内部路径；未知值占位并进 `unresolvedReferences` |
| `ostrons`（希图斯） | `WS.syndicateMissions` | `wf_community_dataset_state_translation`：任务翻译；`wf_community_dataset_reward_pool`：奖励补全；`WS-CB`：引用 | WorldState 三项目；`warframe-public-export-plus` | 已核验当前 `CetusSyndicate` 7 个 Jobs，选择和展示回归通过 |
| `solaris`（索拉里斯） | `WS.syndicateMissions` | `wf_community_dataset_state_translation`：任务翻译；`wf_community_dataset_reward_pool`：奖励补全；`WS-CB`：引用 | WorldState 三项目；`warframe-public-export-plus` | 已核验当前 `SolarisSyndicate` 7 个 Jobs，路径清洗和奖励池回归通过 |
| `sorties`（突击） | `WS.sorties`，兼容轻量突击 | `WS-CB`：首领、任务、词缀、节点、奖励 | WorldState 三项目；`warframe-status` | 已真实快照回归：`Variants` / `LiteSorties.Missions` 和缺失词缀降级 |
| `steel-fissures`（钢铁裂隙） | `WS.activeMissions`，仅 `Hard=true` | `WS-CB`：节点、任务、纪元、词缀 | WorldState 三项目；`warframe-status` | 已真实 Mongo 样本回归：与普通裂隙精确分流，纪元正常 |
| `steel-path`（钢铁奖励） | `WS.steelPath` | `WS-CB`：奖励/状态引用 | WorldState 三项目 | 已有模块回归 |
| `void-storms`（九重天裂隙） | `WS.activeMissions` 九重天任务；兼容 `WS.voidStorms` | `WS-CB`：节点、任务、纪元 | WorldState 三项目；`warframe-status` | 已真实快照回归：`ActiveMissionTier`，缺失任务类型显示“未知任务” |
| `void-trader`（奸商） | `WS.voidTraders` | `WS-CB`、官方目录：地点、库存引用 | WorldState 三项目；`warframe-public-export-plus` | 已真实快照回归：无 `Manifest` 仍显示地点、状态、到离开时间 |

## 市场、遗物和静态资料命令

| 命令代码（名称） | 运行时数据源 | 表及用途 | 离线参考 | 已验证状态 / 待补项 |
| --- | --- | --- | --- | --- |
| `market`（市场，`wm`） | `WM API` 订单/统计 | `wf_official_export_record`、`wf_community_dataset_translation`、`wf_community_term`、`wf_community_dataset_alias`：中文、别名和 WM slug；`wf_community_dataset_market_component`：中文组件后缀到 WM 英文后缀 | `warframe-nexus-query`、`warframe-info-api`、`warframe-public-export-plus`、`warframe-items`、`NyxBot` | 已回归 `wm 破灭p满级`、`mag`、`摸尸头`、`摸尸机体`；规则分值不是统计置信度，裸 `mag` 按明确 Prime 家族意图优先 `mag_prime_set`；组件映射数据库化，订单数据不入库 |
| `market-riven`（紫卡市场，`zk`） | `WM API` 紫卡拍卖/武器目录；KingPrimes/DataSource 词条 JSON | `wf_community_dataset_market_riven_tion`：属性目录；`_alias`：单属性/逗号编码组合；官方目录、翻译、术语、别名表：武器精确匹配 | 核对过 `warframe-info-api` 首段武器解析的局限和 `NyxBot` 属性/has 协议；不直接复制其解析器 | 历史 34 属性/17 别名夹具、两条例子、Prime、任意负面及数据库组合测试通过；前端结构化编辑已实现。相关结构/数据已纳入 `init.sql`，旧增量不再单独执行；实际数据库/机器人运行验证待补 |
| `relics`（遗物） | PostgreSQL | `wf_community_relic`：名称/入库；`wf_community_relic_reward`：奖励/稀有度；`wf_community_relic_drop`：掉落资料 | 唯一正式上游 `warframe-relic-data/data/Relics.json`；官方 `ExportRelicArcane` 独立保存 | 已有遗物查询回归；自维护优先，父子同批刷新，不跨来源拼接 |
| `gold-trash`（金垃圾） | PostgreSQL | `wf_community_relic_reward`：`Rare` 奖励 | `warframe-relic-data/data/Relics.json` | 已有遗物查询回归 |
| `silver-trash`（银垃圾） | PostgreSQL | `wf_community_relic_reward`：`Uncommon` 奖励 | `warframe-relic-data/data/Relics.json` | 已有遗物查询回归 |
| `riven-disposition`（紫卡倾向变动） | PostgreSQL | `wf_community_dataset_riven_trend`：社区维护的倾向资料 | `warframe-public-export-plus`、`warframe-info-api` | 查询链可用；时效随社区导入 |
| `translation`（翻译） | PostgreSQL | `wf_community_dataset_translation`：完整键/语言/显示名；`wf_official_export_record`：独立保存官方 Public Export | plus `dict.zh.json` / `dict.en.json` | 自维护 > 可用官方本地化 > plus 中文 > plus 英文；完整键不截断，中英文分别保留 |
| `market_component`（WM 组件映射） | PostgreSQL 本地自维护 | `wf_community_dataset_market_component`：`input_alias` 作为中文输入后缀，`market_suffix` 作为 WM 商品英文后缀；不参与社区远程刷新 | `warframe-info-api`、`warframe-nexus-query` 的商品后缀行为作离线参考；映射本身是 NnmBot 自维护规则 | 已完成实体、投影、Catalog、Resolver 最长后缀、管理端 CRUD、SQL 基线/增量和服务级校验；26 项定向测试通过 |
| `calendar-1999`（1999日历） | PostgreSQL | `wf_community_dataset_state_translation`：`calendar1999` 静态任务资料，不含实时排期 | `warframe-worldstate-data`、`warframe-info-api` | 静态查询可用；不是实时官方日历 |
| `forum-loadout`（wf配装） | PostgreSQL | `wf_forum_article`：标题、分类、标签检索文章 URL | `warframe-hub`、`mod-generator` | 定向测试通过；正文不由机器人返回 |
| `wiki`（Wiki） | PostgreSQL 后拼 Wiki 页名，不抓正文 | `wf_official_export_record`、翻译、术语、别名：物品/别名解析 | `warframe-public-export-plus`、`warframe-items`、`warframe-info-api`、`NyxBot` | 当前仅解析页面名，非 Wiki 爬虫 |
| `liches`（赤毒） | PostgreSQL | `wf_community_term`，`term_group=liches`：静态资料 | `warframe-info-api`、`warframe-items`、`NyxBot` | 静态资料，不是实时库存；独立维护待确认 |
| `sisters`（信条） | PostgreSQL | `wf_community_term`，`term_group=sisters`：静态资料 | `warframe-info-api`、`warframe-items`、`NyxBot` | 静态资料，不是实时库存；独立维护待确认 |
| `perrin-sequence`（佩兰数列） | PostgreSQL | `wf_community_term`：佩兰、信条、终幕静态术语 | `warframe-info-api`、`warframe-items`、`NyxBot` | 不代表本轮库存/元素加成；独立维护待确认 |
| `riven-analyse`（紫卡分析） | 消息图片下载后调用现有 `OcrService` | `wf_official_weapon`、Riven 词条/别名、`wf_community_dataset_riven_analyse_trend`；当前返回行级 OCR 证据和结构化 `CommandResult.data` | 原十个项目提供词库/统计/卡面；NyxBot 提供 OCR、区间计算及启发式评价实现 | **部分实现**：已分离卡片等级/武器段位、按行解析词条并保留未确认状态；组合词条和连续中文 OCR 回归仍失败，尚未完成变体确认、区间计算和图片验收 |

## 订阅和刷新命令

| 命令代码（名称） | 运行时数据源 | 表及用途 | 离线参考 | 已验证状态 / 待补项 |
| --- | --- | --- | --- | --- |
| `subscribe`（订阅） | 当前群、机器人、用户上下文 | `bot_group_bot`、`bot_group`：作用域；`bot_group_member`：调用者；`wf_group_watch_rule`：保存规则 | 无直接数据源；规则字段参考 WorldState 项目 | 订阅服务链可用 |
| `unsubscribe`（取消订阅） | 当前群、机器人、用户上下文 | `bot_group_bot`、`bot_group`：作用域；`bot_group_member`：调用者；`wf_group_watch_rule`：匹配并停用 | 同上 | 取消订阅服务链可用 |
| `update-sister`（更新信条） | 管理端上传 `term.json` 后的 Redis 预览和人工合并；机器人命令不请求远端 | `wf_community_term`：共用静态术语，保留稳定来源身份 | `warframe-info-api`、`warframe-items`、`NyxBot` | **部分完成**：查询可用；原远程来源已停用，独立信条维护仍未建模 |
| `update-liches`（更新赤毒） | 管理端上传 `term.json` 后的 Redis 预览和人工合并；机器人命令不请求远端 | `wf_community_term`：共用静态术语，保留稳定来源身份 | `warframe-info-api`、`warframe-items`、`NyxBot` | **部分完成**：查询可用；原远程来源已停用，独立赤毒维护仍未建模 |
| `update-perrin-sequence`（更新终幕） | 管理端上传 `term.json` 后的 Redis 预览和人工合并；机器人命令不请求远端 | `wf_community_term`：佩兰、信条、终幕静态术语，保留稳定来源身份 | `warframe-info-api`、`warframe-items`、`NyxBot` | **部分完成**：查询可用；原远程来源已停用，独立终幕维护仍未建模 |

## 未闭环项与维护规则

| 项目 | 当前结论 | 后续内容 |
| --- | --- | --- |
| 周期剩余范围 | 地球/山谷/希图斯/魔胎/扎里曼/双衍本地计算已接入；有效上游周期优先 | 继续补真实 WorldState 样本和运行端输出验证；不将缺失的 Circuit choices 当作周期算法阻塞 |
| 信条、赤毒、终幕独立数据 | 三个查询共用 `wf_community_term`，只提供静态术语；term 远程来源已停用，更新命令仅提示管理端上传、预览和人工合并 | 确认来源、独立表字段、录入/审核权限、覆盖和缓存策略 |
| 紫卡分析 | 已有 OCR 原文链路与 Nyx 源码规划，无完整业务计算 | 精确变体/等级确认、属性基础值完整性、区间计算、标注截图回归 |

新增或修改 Warframe 命令时，必须同步更新紧邻 `@BotPluginCommand` 的表/外部源用途注释、本矩阵及验证状态。无法解析的 WorldState 值必须保留用户可见占位，并通过 `unresolvedReferences` 留存原始值，不能把内部路径作为正常展示。
