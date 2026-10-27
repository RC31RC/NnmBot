# Warframe 数据与命令实现

更新时间：2026-10-26。本文维护用途、参数和返回语义；逐插件、表、Repository、Service、
数据集、项目和文件见 [命令与数据目录](reference/命令与数据目录.md)。
导入、审核与真实环境边界见 [数据模型与导入边界](Warframe数据模型与导入.md)。

## 动态、静态与人工数据边界

| 数据 | Java 运行链路 | 保存位置 |
| --- | --- | --- |
| WorldState | `WarframeWorldStateServiceImpl` 拉取、解析并原子替换，失败保留最近成功快照 | 每 JVM 内存，不使用数据库或 Redis 快照 |
| 仲裁排期 | `WarframeArbitrationRefreshServiceImpl` 拉取、解析，`WarframeArbitrationSnapshotStore` 管理有界历史 | 每 JVM 内存；重启重新拉取，不建排期表 |
| 周紫卡价格 | `WarframeRivenWeeklyServiceImpl` 按平台拉取、解析并保留最近成功结果 | JVM cache2k，不持久化周价格 |
| 官方静态 JSON | Java 官方资料预览、审核、采纳服务处理本 JSON 内的完整关系树 | 官方固定模型表 |
| 别名、术语/黑话、仲裁精华、紫卡别名/组合规则 | `persistent/curated/service/reference` 的维护与批量导入 | `wf_community_*` 人工资料表 |

WorldState 与仲裁各 JVM 使用 Spring 本地调度，不由 RDT 抢占为单实例任务；周期、时区、重试和容量由对应属性类管理。
`WarframePropertiesConfiguration` 只负责集中注册分域属性 Bean，不另建一套配置模型或兼容旧名称。
主体包按生命周期与数据来源划分为 `runtime`、`persistent/official`、`persistent/curated`。查询服务直接位于所属部分的 `service/<业务>`，数据对象位于对应 `pojo`。`plugin`、`controller`、`schedule`、`mq`、`storage` 保持模块根包，内部按来源及业务分组；不保留 `shared`、根 `rawdata`、根 `parser` 或 `catalog` 服务层。
旧通用社区来源、Raw 自动融合与仲裁 JPA 模型均已移除；既有管理端页面和稳定权限码仍保留，不能按 `community` 字面批量删除。
`scripts/` 仅承担开发工具职责，`tmp/` 仅存开发临时产物；应用运行时拉取 JSON、解析和数据库采纳全部由 Java 实现。
已存在数据库的退役表清理由 `docs/sql/01_remove_legacy_community_and_arbitration_runtime_tables.sql` 手工执行，应用不会自动删表。

## 固定版本整包接入

官方静态输入已切换为 `RC31RC/warframe-datasource-nnm` 的固定 tag/完整 commit，
不再独立下载 PublicExport。管理端先远程刷新 tag，再选择版本生成完整审核包；每个文件核对完整路径、
SHA-256、字节数、JSON 结构和 manifest 的记录数。预览保存冻结的双语记录差异，采纳不再次联网。
该仓库是 NnmBot 内置的默认官方数据源，不需要在 YAML 中重复配置仓库、Raw 地址或 tag 前缀；这些稳定身份由后端属性默认值提供。
审批事务以单例 head 行锁保护基线与版本切换，保存重复审批幂等性，并保留本地 wiki/market 字段。
不同 ref 和批次有独立后台任务身份；整包下载和采纳串行，待审核包及保留快照数量由配置限制。

当前 Nnm manifest 为 71 个静态 JSON，每个 JSON 有独立主表、实体和具体 Repository；
没有跨 JSON 总父表或统一根 Repository。只有本 JSON 内必要子表按 `bkey` 逻辑关联直接父行（子表 `bkey` 等于父表 `id`），
不声明数据库外键；[逐文件模型目录](reference/官方JSON与模型目录.md)与 `OfficialModelTreeTest` 对真实包逐字段校验类型并抽样整树往返。
本机 `data/latest` 的实际记录和字段形态以当前 manifest 与 `OfficialModelTreeTest` 输出为准。
语言、图片、文本图标等辅助文件也按固定字段契约导入；未变化文件不解析，relics 与 item-localizations
以流式方式逐个根对象解析。官方与自维护同身份并存时，资料命令优先完整自维护树；官方修改转自维护、
删除待重建及同 hash 重建语义见 [静态数据审核与维护](Warframe数据模型与导入.md)。

官方 PHP 响应先由 Jackson 绑定到 `runtime/rawdata/worldstate/OfficialWorldStateRawData`，
再转换成稳定的 `WorldStateDTO`；raw 对象、解析结果和原始响应在一次快照替换中一起发布。
原始模型使用具体字段、`@JsonProperty` 和必要的协议别名，Mongo 日期、ID、扩展整数及空奖励
由字段级 Jackson 反序列化器处理；`OfficialWorldStateMapper` 直接读取 getter 形成 DTO，
不把对象转回 JSON 树，不按字符串路径读取业务字段。对象单值在稳定 DTO 中展开，多值保留列表。
未识别字段仅在完整响应原文中保留，已知字段类型错误会携带字段路径失败，且不替换成功快照。
`Tmp` 是上游 JSON 字符串扩展，单独自动绑定到 `TmpRawData`；无法绑定时记录警告，不影响其他有效分区。
深渊轮换种子使用 `Long`，时间保留毫秒精度。WorldState 代码簿通过 `WarframeOfficialRecordReadService` 读取类型化关系树，与静态命令共用自维护优先规则；
不在运行时重解析用于审核的原始 JSON。

WorldState 通过 `runtime/mapper/worldstate` 进行类型化协议映射。`persistent/official/parser` 负责官方静态文件的流式读取和输入校验，`persistent/curated/parser/reference` 负责人工资料导入；`runtime/parser/arbitration`、`runtime/parser/riven` 处理各自动态源格式。解析器随数据归属放置，根目录不再设置 `parser`。

官方静态资料管理入口为 `WarframeDataManagementController`，世界状态入口为 `WarframeWorldStateManagementController`；拆分 Java 入口后保持现有 HTTP 路径和权限码：

| 按钮/行为 | HTTP 接口 | 权限 | 结果 |
| --- | --- | --- | --- |
| 官方静态资料拉取 | `POST /api/admin/v1/warframe/official/refresh` | `warframe:game:refresh` | 提交后台任务，完成后返回审核预览，不立即生效 |
| 审核采纳 | `POST /api/admin/v1/warframe/official/preview/{id}/apply` | `warframe:game:refresh` | 按预览采纳独立资料表变更 |
| 世界状态刷新 | `POST /api/admin/v1/warframe/world-state/refresh` | `warframe:world:refresh` | 提交后台任务，完成后返回 `WarframeWorldStateManagementVO` |
| 世界状态原文 | `POST /api/admin/v1/warframe/world-state/raw/query` | `warframe:world:view` | 返回 `WarframeWorldStateRawVO` |

前端请求统一在 `nnm-admin-web/src/api/warframe.ts`，静态页为 `views/warframe/data.vue`，
动态页为 `views/warframe/world-state.vue`；两个刷新按钮都等待任务结果，再更新对应视图。

官方 PHP 每次解析后原子替换不可变内存快照，并检查订阅。
`oracle.browse.wf/bounty-cycle` 仅作为补充动态源；expiry 必须与官方毫秒时间完全一致，
节点、挑战和候选奖励池只用已采纳静态包解释。社区任务与原始 Jobs/Nodes 分开保存，失败不沿用上一轮。
当前成品没有 ally agent 名称映射，相关身份保持未解析；静态奖励池不能冒充本轮确定奖励。

## 官方静态资料的读取边界

`WarframeOfficialSourceClient` 默认从配置的本地 NNM 数据目录读取 manifest 和文件，
只有显式选择 `REMOTE` 才访问 GitHub。本地版本直接从配置的数据目录查找 Git 仓库，
不假设目录深度；解析超时由 `local-version-timeout` 控制，中断或超时会终止子进程。
下载后按 manifest 的字节数、SHA-256 和字段合同校验，
`WarframeOfficialSnapshotParser` 逐条绑定具体输入 DTO，预览通过后由 `WarframeOfficialApprovalService`
在同一数据库事务中采纳主表和本 JSON 的必要子表。采纳与人工维护共用数据库指针行锁，锁覆盖事务提交。

`parseCompleteJson` 用于一条完整记录，拒绝尾随 JSON；`parseCurrentValue` 只读取流中当前值，
允许调用方继续读取同一数组或对象中的后续记录。这两个入口分别固定尾随值检查行为，
不受全局 ObjectMapper 设置影响；批量快照入口 `readRecords` 与 `forEachRecord` 分别返回列表和逐条消费。
原始 JSON 独立保留给审核和差异比较。
记录所属领域和文件路径由具体 Repository 的 `domain()` 确定，不在每行重复存储。

同一领域、忽略大小写相同 `bkey` 的官方与自维护记录可以并存。查询先选择启用的自维护主记录，
再装载该记录自己的整棵详情树，不从被覆盖的官方树拼接缺失字段。转为自维护时保留官方树；
删除或停用自维护版本后，官方版本重新参与查询。动态代码簿使用相同规则，
即使尚未采纳第一批官方数据，也会装载已有的自维护引用资料。

运行时摘要使用闭合标量 DTO 投影，完整详情通过具体 Q 类型的 QueryDSL 查询按 250 个父 ID 分批恢复。
字典子表同时读取 `memberKey`，保留原始字典键；字段形状码区分未提供、显式 null、空集合和具体值。
运行时查询不读取审核原文，不将详情行交付为 Hibernate 受管实体。
完整关系树及遗物候选筛选使用 PostgreSQL 的短只读 `REPEATABLE_READ` 事务，使摘要、主表与多级子表来自同一数据库快照；
审批在查询中途提交时，本次返回完整旧树，后续查询读取新树，避免跨版本拼接。

使用 `node scripts/database/verify-official-import.cjs` 可在独立 PostgreSQL 临时库验证手工 SQL、
71 文件的实际字段形状和代表性关系树，以及自维护优先、子表替换、审批回滚和非受管查询。
默认数据目录为相邻的 `warframe-datasource-nnm/data/latest`；可通过 `NNM_OFFICIAL_DATA_DIRECTORY` 指定。
测试结束后销毁临时库，不连接应用的开发数据库。

## 命令体系

沿用 NnmBot 的 `BotPlugin`、`@BotPluginCommand`、`CommandRegistry` 和统一执行链。
下文使用稳定命令代码，不承诺它就是当前触发词；别名、启停、作用域、输出模式、
机器人能力角色和收费均以管理端数据库配置为准，不复制参考项目的路由或命令别名表。
新增的 60 个缺失命令配置默认为禁用、TEXT，不自动分配机器人能力角色。

```text
平台消息 -> 注册/匹配 -> 统一权限、作用域、冷却及积分检查 -> Plugin
Plugin -> Service 接口 -> Repository / 动态快照 / 外部 API
Plugin -> CommandResult(text, 展示 VO) -> 统一文本/图片/文件/Markdown 输出
```

Repository 负责数据访问，Service 负责数据查询、来源决策和可复用业务处理；
Plugin 解释命令参数、调用 Service 并组装展示 VO，不直接查询 Repository。
`CommandResult.data` 是结构化展示源；不得向用户输出实体、原始审计 JSON、内部 ID 或伪造缺失值。

## 静态资料命令

下列资料均来自已采纳的职责表；“已实现”不意味着实际应用库已经导入。
普通查询形式为 `<已配置触发词> [关键词] [--page N]`。
选项放在末尾，支持相应选项的 `--name=value` 形式；页码从 1 开始，
裸数字仍是关键词，尤其不能把遗物编号解释为页码。
不同插件只接受自身列出的选项，不支持的选项应拒绝，不静默忽略。

| 命令 | 用途与返回 |
| --- | --- |
| `warframes`、`archwing` | 战甲/Archwing 属性、名称描述、图像引用、技能和拥有顺序。 |
| `weapons` | 武器类型、属性、数值和描述；紫卡分析独立使用固定 1.2，不读取武器倾向。 |
| `abilities` | 独立技能定义和所属装备关系，按完整技能身份关联。 |
| `companions` | 哨兵/宠物等同伴的分类、属性和关联资料。 |
| `mods`、`arcanes` | MOD/奥术定义与等级效果；可加 `--rank N`，等级从 0 开始。 |
| `mod-sets` | 套装定义和效果；`--rank N` 表示套装件数，从 1 开始。 |
| `resources`、`fish`、`gear` | 资源、鱼类物品、装备工具的类型、描述和实际存在的属性；鱼类物品不等于钓鱼攻略。 |
| `misc`、`quests`、`customs` | 杂项、任务、皮肤/纹章等外观资料，不伪装成统一资源类型。 |
| `animals`、`drones` | 捕获动物和无人机的专属定义、数值和说明。 |
| `boosters`、`credit-bundles`、`fusion-bundles`、`flavours` | 增幅器、信用点礼包、融合包、展示风格的实际字段。 |
| `bundles`、`booster-packs` | 普通礼包组件；随机礼包组件、重复规则与逐次稀有度权重，不将权重冒充最终概率。 |
| `recipes` | 产物、材料、费用、时间与氏族规模；`--kind ordinary\|dojo` 区分普通/道场配方。 |
| `item-components` | 递归组件关系、数量和父组件身份；可加 `--type <数据集代码>`。 |
| `item-drops` | 主物品/组件的掉落、数量及来源概率口径；不把遗物 `locations[]` 混成物品掉落。 |
| `reward-pools` | 完整奖励池、条目、数量、稀有度和可空概率；`--stage N` 按阶段筛选。 |
| `bounty-definitions` | 赏金静态名称、说明及有序阶段，不生成当前动态赏金。 |
| `keys`、`email-items` | 钥匙链、阶段物品/邮件/奖励，以及独立邮件模板定义。 |
| `enemies` | 敌人六分区及实际采纳的掉落层级；`--section avatars\|agents\|damage-controllers\|droptables\|hit-proxies\|ai-weapons`。 |
| `vendor-definitions` | 静态商人、商品、普通材料价格与随机 bin 价格；`--section items\|random-prices`，不推断实时库存。 |
| `nightwave-definitions` | 静态季节、挑战和有序奖励；`--section challenges\|rewards`，不替代当前电波。 |
| `intrinsics` | 内源之力定义和已采纳等级；按完整 LPS_* 代码关联。 |
| `achievements`、`challenges`、`systems`、`tilesets`、`virtuals` | 成就、挑战、系统、场景和虚拟实体的静态定义。 |
| `codex-objects`、`codex-lore`、`codex-songs`、`codex-fighters` | 图鉴物件、故事碎片、歌曲和 Frame Fighter 碎片四分区。 |
| `legacy-items`、`market-catalog`、`riven-attributes` | info-api 历史物品目录、历史 Market 商品与紫卡编码桥接；当前目录按整包成品迁移。 |
| `conclave-rules`、`archon-shards`、`event-rules`、`steel-path-catalog`、`tutorials`、`arcane-guides` | 武形秘仪、源力石、活动计分、钢铁静态目录、教程链接和旧奥术说明；不解释为当前任务/商品。 |
| `synth-targets` | 当前 71 文件没有合成目标地点资料；保留命令入口并明确提示未提供，不从敌人字段推断攻略。 |
| `fish-guide` | 钓鱼地点、时间、鱼饵等；可加 `--world plains\|vallis\|deimos`。 |
| `map-features` | 开放世界 GeoJSON 标注、位置和图层；可加 `--world`、`--layer <图层代码>`。 |
| `riven-unrollables` | 不可洗紫卡属性关系，不能把缺少关系理解为允许洗出。 |

`item-components` / `item-drops` 读取所选官方根树内已建模的组件、奖励和掉落关系；
不再从通用社区 Raw 数据集补拼组件，也不使用旧 `market_component` 后缀表。

### 遗物

- `relics`：`[关键词] --page N`，返回奖励、稀有度、概率、获取地点、精炼和入库元数据。
- `gold-trash` / `silver-trash`：`[关键词] --page N`，返回上一小时和上一日的市场成交统计；
  金垃圾筛选 100 杜卡德金币，银垃圾筛选 45–99，按加权金币/铂金比值降序，每时段默认前十项。
  静态物品名关联已采纳商品目录；缺失名称明确显示未收录。旧遗物奖励展示仅供遗物命令使用。
  遗物查询保留奖励及所属遗物，
  保留跨遗物重复奖励；不是实时价格或收益排行榜。
- 官方遗物完整树来自 `rewards/relics.json`；同身份自维护树整体优先，不跨来源拼接父子明细。
- `wf-relics-luxe-v1`、`wf-warframes-luxe-v1`、`wf-riven-weekly-luxe-v1` 是三个新增可选模板，
  初始禁用，未改既有命令的输出模式。需 Playwright，不更改全局默认渲染引擎。

## WorldState 命令

这些命令是 EXACT 匹配，不接受静态资料查询选项。
使用最后成功动态快照，经代码簿、翻译及静态定义补全；请求失败不覆盖旧快照。
内部路径无法解析时展示未知占位，并把原值保留在 `unresolvedReferences`，不能正常展示内部代码。

| 命令 | 返回 |
| --- | --- |
| `alerts` | 当前警报、节点、任务、有效期和含数量的奖励。 |
| `fissures`、`steel-fissures`、`void-storms` | 普通、钢铁、九重天裂隙分别筛选，显示纪元、任务、节点和有效期。 |
| `sorties`、`archon-hunts` | 突击/执刑官的首领、任务、词缀、节点；兼容轻量突击，缺条件明确未知。 |
| `invasions` | 双方阵营、进度、节点和双方奖励数量。 |
| `ostrons`、`solaris`、`entrati` | 希图斯、索拉里斯、英择谛的当前赏金；静态定义和奖励池仅作补充。 |
| `holdfasts`、`cavia`、`hex-bounties` | 扎里曼、科维兽、1999 赏金的动态任务与对应详情。 |
| `syndicate-missions` | 当前集团任务，不等于静态集团定义。 |
| `nightwave` | 当前电波和挑战名称、说明、期限。 |
| `conclave` | 当前竞技场挑战。当前静态包不提供 `conclaveData` 规则定义，不推测模式、分类、标题、说明或声望。 |
| `archimedeas`、`deep-archimedeas`、`hex-archimedeas` | Conquests 科研，后两者分别筛选 CT_LAB、CT_HEX。 |
| `descents` | 深层下降首条周轮换和挑战层级。 |
| `cycles`、`duviri-cycle` | 六种周期状态/到期时间，或双衍五状态及上游存在的附属信息。 |
| `circuit-normal`、`circuit-hard` | 普通回环/灵化轮换；choices 缺失不由周期算法伪造。 |
| `void-trader`、`vault-trader` | 虚空商人/宝库商人的到离开、地点及上游存在的库存。 |
| `steel-path` | 当前钢铁轮换奖励，不等于静态商店目录。 |
| `daily-deals`、`flash-sales` | 每日特惠/促销中的商品、折扣、数量和时间等已有字段。 |
| `calendar` | 官方实时日历，区别于静态 `calendar-1999`。 |
| `simaris` | 当前快照的 Simaris 资料，不从合成攻略推算。 |
| `events`、`news` | 活动详情/进度和新闻链接、时间等已有字段。 |
| `timestamp` | 最后成功 WorldState 快照中的官方时间，不是本机当前时间。 |
| `clan-initiative`、`construction-progress` | 氏族周任务、舰队建造进度。 |
| `global-upgrades`、`persistent-enemies`、`sentient-outposts` | 全局增益、持续敌人、先遣舰状态。 |

有效上游周期优先；地球/山谷可本地计算，希图斯/魔胎和扎里曼需要有效希图斯赏金结束锚点；
扎里曼按 2.5 小时阶段延伸近期锚点；双衍可独立推导五个两小时状态并保留 52 秒校准。
公式和有效期由 `WarframeCycleProperties` 配置；缺失有效锚点不猜测。
`WeeklyChallenges` / `BadlandNodes` 已解析，但没有确认稳定的命令语义，未虚构新命令。

### 仲裁排期

仲裁排期不属于静态 manifest。运行时定时任务按 `nnm.warframe.pull.arbitration.*`
配置拉取来源，解析器使用 WorldState 静态节点/任务/阵营代码簿补全；成功后原子替换当前 JVM 的
有界内存快照，不写数据库或 Redis。每个实例独立定时拉取；刷新失败保留本机最后成功快照，
并按重试周期再试。仲裁排期是运行时预测数据，不是静态官方业务表，也不走人工差异审核。

`arbitration`、`today-arbitration`、`efficient-arbitration`、`arbitration-table` 是独立 EXACT 插件，
共用 handler 和 Service。只查本 JVM 最近一次成功的内存快照，命令查询不会同步下载排期。

| 命令 | 筛选与返回 |
| --- | --- |
| `arbitration` | `activation <= now < expiry` 的当前任务。 |
| `today-arbitration` | 配置时区（默认上海）的自然日任务及缺口。 |
| `efficient-arbitration` | 未来任务按配置的任务/星球/阵营规则筛选，最多十项，不含当前项。 |
| `arbitration-table` | 未来七天范围前十项，可含当前任务。 |

精华表仅补精华/小时参考，不生成排期；各视图保留预测、抓取时间、陈旧与缺口提示。

## 市场、紫卡与输入解析

| 命令 | 行为与返回 |
| --- | --- |
| `market` | 别名/名称解析后查询 WM 订单、统计，支持满级等订单条件；默认每页五单，实时报价不入库。 |
| `market-riven` | 精确识别多词武器/Prime、正负属性和页码，查询 WM 拍卖；默认每页十条。 |
| `riven-definitions` | 紫卡属性基准或挑战；`--mode attributes\|challenges --page N`。 |
| `riven-weekly` | 官方周成交快照；`--platform pc\|ps4\|xb1\|swi --rerolled true\|false --page N`。 |
| `riven-analyse` | 消息图片下载、OCR 行证据、武器/词条识别、条件满足时的参考区间及未知项。 |
| `translation` | 已采纳字典的键、语言与显示文本，不把描述键当作物品名称。 |
| `wiki` | 解析目标并返回 Wiki 页面入口，不抓正文。 |
| `forum-loadout` | 检索 `wf_forum_article` 标题/分类/标签，返回文章入口，不返回正文。 |
| `calendar-1999` | 静态任务资料，不能解释为实时日历。 |
| `liches`、`sisters`、`perrin-sequence` | 共用术语表中的静态赤毒/信条/佩兰资料，不是实时库存或元素加成。 |
| `update-liches`、`update-sister`、`update-perrin-sequence` | 只返回管理维护提示，不发远程请求，不绕过预览采纳。 |

Warframe Market 输入识别与正式展示名称是两个阶段：官方成品 `market/trade-items.json`
提供本地化名称和商品 slug，四类人工白名单中的物品别名/术语用于确有需要的扩展；不会用旧
`market_component` 后缀数据集或 13 条旧英文后缀映射猜商品。目录存在并列同分候选时拒绝查询，
不能以 slug 字母顺序冒充唯一命中。“一套/套装”输入只在目录中选择对应套装条目。
紫卡市场按最长属性别名匹配，支持多词属性、独立“负/负面”和负任意；有歧义时拒绝，
不缩短回退，不吞掉下一个负面标记，仍校验最多三个正面和一个负面。

weeklyRivens 的 avg/median/min/max 是价格，`pop` 是相对交易热度，不能当作样本量或倾向系数。
结果标识平台、来源、抓取时间及历史成功快照状态，不伪装成即时单卡估值。

OCR 分离卡片等级与武器段位；符号后的水平空格仍属于当前行数值证据。
只有完整武器变体、卡片等级、词条组成、单位、类别和已采纳基准全部确认，才按
`FIXED_1_2_V1` 计算参考区间；倾向固定为 1.2，不读取武器来源记录中的倾向字段，并携带规则版本和武器类别来源。
倾向变化与历史倾向快照命令、数据模型及初始化表已移除；紫卡词条定义和类别系数基准仍保留。
新组合机制、拼接和未确认的双模式武器不强套旧公式；未确认项显式保留。
这不是当前游戏合法性、完美度承诺或交易价值鉴定。

## 订阅与系统命令

| 命令 | 输入和返回 |
| --- | --- |
| `subscribe`、`unsubscribe` | `主题 [条件...]`，例如“订阅平原”“订阅仲裁 谷神防御 谷神拦截”“订阅裂缝 钢铁虚空捕获 虚空歼灭古纪”；在已绑定群及成员上下文创建/取消规则。 |
| `watch-list` | 只列当前成员在当前群自己的启用规则，不暴露其他成员。 |
| `help` | 当前命令帮助，具体入口受数据库配置约束。 |
| `bind`、`sign-in` | 现有群组身份绑定/签到流程及结果，继续沿用 core 的校验与积分逻辑。 |
| `status` | 系统状态。 |
| `jm` | 现有 JM 下载命令，细节见 [JMComic下载说明](JMComic下载说明.md)。 |

订阅主题包含平原、奸商、特惠、钢精、警报、活动、入侵、圣所、扎里曼、六人组、六人组箱、仲裁、裂缝。
仲裁和裂缝的多个候选条件用空格或 `|` 分隔；裂缝支持普通/钢铁难度、纪元和九重天节点名，
不限定难度时包括普通、钢铁和虚空风暴；安魂、全能也计入古纪条件。
平原夜晚和仲裁默认提前十分钟；圣所、扎里曼及六人组仅匹配最高赏金档次的歼灭任务。
六人组箱按成品的 `VaniaSafeCracker` 挑战匹配，展示实际任务类型；当前样本是生存，不能标成捕获。

每条规则保存独立事件记忆，不以整组匹配列表或剩余时间作为身份。
A+B 变成 A、顺序调整和短暂消失均不重发；相同节点的新轮次可以再通知。
cache2k 内存预筛排除已认领身份，持久化行锁负责多实例最终认领；容量满时不驱逐活跃身份后重复发送。
当前投递保持“认领后发送”的至多一次行为，发送失败不会把游标回滚；成员重新启用通知会清空记忆。
群组成员列表的订阅按钮可以查询、启停或删除该成员的规则，后端校验群组、归并后成员及规则归属。

## 来源选择规则

静态业务资料只读取 Nnm 固定版本官方树和四类明确的人工白名单数据。
同一官方自然身份有自维护整树时整体优先；无对应官方定义的数据应如实标为未提供，不借用旧通用社区来源。
WorldState、仲裁排期、周紫卡价格和 Warframe Market 订单各自通过运行时快照或实时 API 提供，
不进入静态官方持久化表。bounty-cycle 是独立的周期辅助接口，不把它伪装成静态发布文件。

修改命令时同步数据用途注释、本文的业务语义及相应测试。
表/来源/插件元数据通过 `ProjectCatalogTest` 校验并按需重新生成目录，避免手写多份矩阵漂移。
