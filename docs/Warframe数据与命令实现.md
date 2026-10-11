# Warframe 数据与命令实现

本文件记录 NnmBot 当前 Warframe 运行时数据边界、命令实现规则和维护约束。逐命令的运行时源、
数据表、离线参考项目和验证状态见 [Warframe命令与参考数据映射.md](Warframe命令与参考数据映射.md)；
新会话的项目整体背景、参考项目路径和社区来源总表见
[项目上下文与续作指南.md](项目上下文与续作指南.md)。

## 数据分层

| 数据类型 | 来源与存储 | 用途 |
| --- | --- | --- |
| 官方动态 | `worldState.php`，仅 JVM/cache2k 最后成功快照 | 当前警报、裂隙、入侵、集团任务等动态命令；解析失败不能覆盖旧快照。 |
| 官方静态 | PublicExport 导入 PostgreSQL，主要为 `wf_official_export_record` | canonical key、物品身份、英文兜底和官方结构。 |
| 社区静态 | `wf_community_*` 职责表 | 中文、别名、WorldState 代码簿、奖励池、遗物、紫卡词条和术语等可审计资料。 |
| Market 实时数据 | Warframe Market API，短期缓存 | 普通订单、统计和紫卡拍卖；订单与拍卖不持久化。 |
| 仲裁社区排期 | `https://browse.wf/arbys.txt`，人工采纳后落 `wf_arbitration_schedule` | 文本每行提供 Unix 秒时间戳和节点代码；通过 WorldState 代码簿补全节点、星球、任务类型和阵营。保存已采纳的未来排期和历史，不是官方 WorldState；刷新失败保留存档，由管理员重试；`bounds` 因上游未提供而保存 `{}`。 |
| 用户维护数据 | 明确的管理端或机器人写入表 | 必须定义来源、审核、覆盖、缓存和权限；不得伪装为官方动态数据。社区 JSON 刷新统一先下载、解析并进入 Redis 预览，再由管理员选择合并。 |

### Redis、PostgreSQL 与本地 cache2k 的边界

| 数据 | 推荐位置 | 原因 |
| --- | --- | --- |
| WorldState 当前快照 | Redis 权威快照，JVM 保存可选的派生快照 | 多实例需要读取同一份最新数据；刷新后通过 RDT 广播 hash 或 version。 |
| 机器人 owner、租约和连接状态 | Redis/RDT | 属于跨实例协调状态，不能只放在本地缓存。 |
| Market 订单、紫卡拍卖和外部查询结果 | 本地 cache2k | 短期数据，可重新请求；不应在 Redis 中复制大量结果。 |
| 物品解析索引和本地翻译索引 | 本地 cache2k | 可由 PostgreSQL 或共享快照重建，适合广播失效后按需加载。 |
| 图片、渲染结果和临时文件 | 本地 cache2k/本地磁盘 | 二进制数据较大，且默认目录属于当前实例。 |
| 官方目录、社区翻译和仲裁历史 | PostgreSQL | 需要审计、查询和稳定持久化，数据库是唯一权威来源。 |
| 刷新通知 | RDT Reliable Broadcast | 只传递 key、hash 或 version，让每个实例清理或重建本地缓存。 |

推荐的数据刷新链路是：

```text
权威数据写入 Redis 或 PostgreSQL
        |
        +-- RDT RELIABLE_BROADCAST(key/hash/version)
                |
                +-- 每个实例清理或重建自己的 cache2k
```

Redis 适合共享的当前状态和短生命周期协调数据，不适合承载图片、完整 Market 结果或
可审计的历史数据。cache2k 适合本地派生数据，但不能作为多实例之间的唯一事实来源。

WorldState 当前快照不能落为业务历史表。它与稳定的代码簿、物品目录和社区翻译属于不同生命周期，
刷新失败时继续使用最后成功快照。

## WorldState 处理链

```text
worldState.php
  -> WorldStateParser
  -> WarframeCycleService（补本地周期，读取时也重新计算）
  -> WarframeCommandData
  -> WarframeReferenceProjectionEnricher
  -> WarframeWorldStateDisplayMapper
  -> CommandResult
```

- Parser 兼容官方字段大小写、Mongo 包装时间和常见字段形态，将外部协议转换为 NnmBot 自有模型。
- `WarframeReferenceProjectionEnricher` 使用 WorldState 代码簿、社区翻译、官方目录和语言表解析内部引用。
- 无法解析的内部值不能作为正常文本输出：用户可见内容显示占位，原始值写入 `unresolvedReferences`，供补充代码表或奖励池。
- 世界状态插件的 `@BotPluginCommand` 下方必须说明快照段、实际读取表和每张表的用途。公共代码簿说明在
  `AbstractWorldStateCommandPlugin`，遗物公共表说明在 `AbstractRelicPlugin`。

官方快照缺周期段时，地球/山谷/扎里曼/双衍按已审计参考公式计算，希图斯/魔胎依赖有效的希图斯赏金结束锚点。
有效上游周期优先，缺失或过旧希图斯锚点不猜测。山谷、扎里曼基准和赏金修正、双衍 52 秒校准均由
`WarframeCycleProperties` 管理；扎里曼仍需要有效希图斯赏金锚点，双衍可在官方周期字段缺失时独立推导。
扎里曼按 2.5 小时阶段延伸近期旧锚点；双衍 expiry 保留 52 秒校准，不复制参考源码的分钟截断，
确保状态与到期时间使用同一半开区间。2026-09-29 周期及配置定向测试 11 项通过，真实输出待联调。

仲裁不再依赖官方快照中的 `arbitration`。管理端刷新链路为
`WarframeCommunityDatasetService -> client/parser -> Redis 预览 -> 管理员采纳 -> wf_arbitration_schedule`；
命令查询链路为
`plugin -> ArbitrationCommandHandler -> WarframeArbitrationService -> repository`。
普通查询和图片预热只读取最近成功采纳的数据，不会隐式请求第三方上游。
当前任务按左闭右开区间；今日按配置时区自然日；高效按 NyxBot 任务/星球/阵营规则筛选未来任务；
仲裁表取未来七天范围前十项。精华规则表只补展示效率，不生成排期。所有视图显示预测、抓取和缺口提示。

## 静态数据和参考项目

十个 Warframe 参考项目仅用于离线审计、字段复核、导入资料和行为对照：

`mod-generator`、`warframe-hub`、`warframe-info-api`、`warframe-items`、`warframe-nexus-query`、
`warframe-public-export-plus`、`warframe-relic-data`、`warframe-status`、
`warframe-worldstate-data`、`warframe-worldstate-parser`。

它们不是线上依赖。可采用资料必须进入 NnmBot 的职责表或代码后再消费，不允许命令执行时依赖其仓库、
GitHub、CDN 或 Wiki 页面。`NyxBot` 只作为额外的本地命令行为、别名和展示对照，不计入十个项目，
也不是运行时依赖。

来源职责保持分离：

- PublicExport 保存官方实体身份和结构；社区翻译、别名不覆盖官方主键。
- `warframe-worldstate-data` 的节点、任务、阵营、裂隙、突击和语言资料用于解释内部 WorldState 代码。
- 社区 `translation` 面向物品和用户输入，`state_translation` 面向动态状态；两者不能混为一个表。
- 遗物基础定义与遗物奖励、稀有度、掉落关联分开维护。
- `warframe-info-api`、`warframe-status` 的聚合缓存不能整体导入为权威数据源。

### 参考项目、WarframeStat 和仲裁排期的关系

本地参考目录中的项目并不是同一个产品，也不是都由 `docs.warframestat.us` 提供数据：

```text
DE worldState.php
  -> warframe-worldstate-parser（解析原始 WorldState）
  -> warframe-worldstate-data（节点、任务、阵营和多语言映射）
  -> warframe-status（WarframeStat API 服务）
  -> api.warframestat.us
  -> docs.warframestat.us（API 文档站）
```

- `warframe-status` 是 WarframeStat API 的服务端实现。
- `warframe-worldstate-parser` 负责解析 DE 的原始 WorldState。
- `warframe-worldstate-data` 提供解析和展示需要的静态代码映射。
- `warframe-hub` 是 WarframeStat API 的消费者，不是 API 服务端。
- `warframe-info-api` 是第三方二次封装；其旧版仲裁接口曾调用
  `https://wf.555590.xyz/api/arbys`，当前 NnmBot 不再依赖该接口。
- `docs.warframestat.us` 只描述 `api.warframestat.us` 的请求和响应，不是数据文件服务器。

`wf.555590.xyz` 同时承担两种不同职责：

1. `/socket` 和其他非 `/api/*` 路径反向代理 WarframeStat。
2. `/api/arbys` 是该站单独提供的仲裁排期接口。

因此，主页链接 WarframeStat 文档只是在说明反向代理部分的上游接口，不能推出
`/api/arbys` 的数据来自文档站。WarframeStat 的 `/pc/arbitration` 更适合表示当前
WorldState 中的仲裁状态，也不等于未来数年的完整排期。

### 批量仲裁排期源

`browse.wf` 公开了排期页面背后的原始文件：

```text
https://browse.wf/arbys.txt
```

每行格式为：

```text
Unix 秒时间戳,节点代码
1790316000,SolNode39
1790319600,SolNode25
```

截至 2026-09-25 的实际检查结果：

- 共 44,056 条小时级记录。
- 时间范围约为 2024-10-02 16:00 UTC 至 2029-10-12 07:00 UTC。
- 文件只提供时间戳和节点代码；节点名、任务类型和阵营仍需使用
  `warframe-worldstate-data` 的 `solNodes` 等映射补齐。
- 抽样对照同一时段的 `/api/arbys` 返回值，节点代码映射后的结果一致。

`browse.wf` 的项目说明明确表示仲裁排期页面使用 `arbys.txt`，因此它可以作为未来排期的
批量数据源。当前没有证据证明 `wf.555590.xyz/api/arbys` 的完整服务端实现和
`arbys.txt` 是同一套生成程序；只能确认抽样结果相符，不能在代码或文档中把这层关系写成
已经证实的事实。

当前 NnmBot 已使用 `arbys.txt` 作为主排期源。刷新流程为：

1. 客户端以 `text/plain` 获取文件，不在机器人查询时请求外部网络。
2. 解析器严格校验每行的时间戳、节点代码、重复时间和连续小时区间。
3. 节点代码由 `wf_community_worldstate_node` 解析，任务类型和阵营分别由
   `wf_community_worldstate_mission_type`、`wf_community_worldstate_faction` 解析。
4. 解析或代码簿补全失败时整批失败，保留数据库旧排期，不生成空字段。
5. 刷新生成 Redis 预览，管理员采纳后才写入 `wf_arbitration_schedule`，记录来源 URL 和抓取时间；
   没有独立仲裁定时任务绕过确认直接写库。

管理端仲裁刷新复用统一社区数据集的人工审核链路：下载和解析结果先进入 Redis 临时预览并通过
管理任务 SSE 报告进度，不自动写入排期表；管理员按操作 ID 选择需要采纳的新增、修改或删除
操作后，才在同一数据集锁和事务边界内合并到 `wf_arbitration_schedule`，未选择的记录保持原状。
仲裁排期可查询、刷新和采纳，但禁止人工新增、上传、修改和删除。删除候选仅限本次上游覆盖窗口。
2026-09-29 的旧专用仲裁入口曾在本地真实后端、PostgreSQL 和 Redis 环境中用 44,056 条
`arbys.txt` 记录验证；这不代表 2026-10-08 的统一仲裁入口和分片协议已完成真实环境验收。

`wf.555590.xyz/api/arbys` 仅保留在历史审计文档中作为参考项目曾使用的来源，不再作为
NnmBot 的默认或降级源。

### 社区术语数据的当前维护边界

`term` 数据集仍登记为 `wf_community_term` 的社区来源，以兼容历史记录的
`sourceCode + datasetCode + recordKey + locale` 身份和查询索引；但原远程 term 地址已失效，
不再作为运行时刷新来源。`TERM` 数据集只允许管理端上传 `term.json`，经过解析、结构化投影、
Redis 预览、SSE 进度和逐条差异选择后人工合并。上传和合并不会覆盖或删除
`SELF_MAINTAINED` 记录。

因此 `update-sister`、`update-liches` 和 `update-perrin-sequence` 当前不执行远程请求，
只提示管理端完成上述维护流程；三个查询命令仍从数据库中的术语记录读取静态资料，不能据此
推断实时库存、元素加成或其他动态状态。

## 命令实现规则

- `BotPlugin.handle()` 负责参数解释、调用数据 Service、业务筛选排序和展示 DTO 组装；Service 负责 HTTP、
  缓存、数据库和基础解析，不能返回命令文案。
- 普通 WM 订单使用独立市场 Service；`wm 破灭p满级` 的等级条件属于订单筛选。紫卡拍卖由单一
  `MarketRivenPlugin` 实现，支持正/负词条与“负任意”；没有可复用子类时不保留抽象拍卖插件。
- Market 目录、官方目录、翻译、术语和别名只用于解析用户输入和展示，实时订单/拍卖不入库。
- 裂隙须展示纪元，并正确区分普通、钢铁和九重天任务；纪元兼容数字、`Tn` 和 `VoidTn`。
- 信条、赤毒、终幕当前只查询共用的 `wf_community_term` 静态术语。原 term 远程来源已停用，
  管理端上传预览和人工合并是当前维护入口；三个机器人“更新”命令只返回维护提示，不执行远程请求。
  独立数据表、字段和审核模型仍未确认，不把静态术语解释为实时库存或元素加成。
- 紫卡分析已接入图片下载和 OCR 原文；完整武器/词条/数值分析未实现。NyxBot 源码规划见 mem，
  后续须确认变体和等级、补齐基础值、校验负面语义，再计算范围，不能用模糊匹配首项生成伪精度。

## 展示协议

`CommandResult.text` 是摘要，`data` 是唯一的结构化展示源。文字、图片、文件和 Markdown 不得维护各自不同的
业务字段集合。展示 DTO 不携带数据库实体、原始 JSON、内部主键或重复的上下游字段。

普通 WM 默认每页 5 单，紫卡拍卖默认每页 10 条。分页、精简字段和排序用于控制消息体积；
OneBot 的消息长度最终仍由适配器和平台决定，项目不自行按固定字符数拆分或截断。

## 更新与审计

管理端负责静态数据刷新、候选差异和人工维护入口。无独立意义的机器人更新命令应删除，不以“更新”名义
重复刷新 Market 物品目录、Market 紫卡目录或普通翻译。新增的社区资料必须记录稳定键、来源、哈希和覆盖语义；
`SELF_MAINTAINED` 数据不能被后续上游同步删除或覆盖。

`term` 是当前的例外维护边界：由于历史远程地址已停用，管理端上传是唯一刷新入口；仍沿用统一
社区服务的 Redis 预览、SSE、逐条差异选择和 PostgreSQL 合并流程，不允许机器人命令绕过审核直接写表。

变更命令、表职责或外部源时，必须同步：

1. 命令 `@BotPluginCommand` 附近的数据用途注释。
2. `docs/Warframe命令与参考数据映射.md`。
3. 对应真实快照、解析、Service 或插件回归测试。
