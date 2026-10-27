# Warframe 数据模型与导入

## Java 目录与职责

`com.nnm.bot.warframe` 先按数据生命周期组织业务，再在持久化部分区分官方模型与本项目维护的数据。`persistent` 表示权威记录由数据库或文件保存，不表示记录不可修改；缓存和定时刷新本身不改变业务归属。

```text
warframe/
├── config/                         # 整个模块的 Spring 装配、HTTP 与 cache2k 配置
├── constant/                       # 模块命令代码、视图键、缓存名称和管理错误码
├── util/                           # 无状态的物品/武器名称规范化
├── runtime/
│   ├── client/                     # WorldState、赏金补充、仲裁和 Market 等动态源访问
│   ├── config/                     # 对应动态源的地址、超时、刷新与计算配置
│   ├── constant/                   # 动态源和实时查询专用枚举
│   ├── rawdata/                    # 动态上游响应原始字段；WorldState Jackson 适配随协议存放
│   ├── parser/                     # 仲裁排期、周紫卡等动态源格式解析
│   ├── pojo/{dto,vo,query}/         # 稳定动态数据、展示对象和查询条件
│   ├── service/                    # worldstate、arbitration、market、riven 服务及 impl
│   ├── cache/                      # 本实例成功快照、动态查询缓存和图片缓存
│   ├── mapper/                     # 动态协议到稳定 DTO、展示 VO 的映射
│   └── event/worldstate/           # 本实例世界状态更新事件
├── persistent/
│   ├── official/
│   │   ├── client/                 # 静态文件下载和 GitHub 版本协议
│   │   ├── config/                 # 导入、图片索引和静态代码簿配置
│   │   ├── constant/               # 官方数据集、维护状态和静态引用类型
│   │   ├── parser/                 # 官方 JSON 类型化输入、结构校验和流式解析
│   │   ├── pojo/{entity,dto,vo,query}/
│   │   │                           # 每份 JSON 的模型，及批次、审核、查询对象
│   │   ├── repository/             # 71 份 JSON 的具体 Repository 及必要查询支持
│   │   ├── service/                # dataset、record、equipment、crafting、items、rewards 等
│   │   ├── cache/worldstate/       # 从已采纳关系表构建的静态代码簿与失效处理
│   │   ├── mapper/                 # 各业务的官方字段映射、导入结果展示映射
│   │   └── event/                  # 官方写事务提交后的数据变化通知
│   └── curated/
│       ├── client/                 # 人工维护数据的受限 HTTPS 导入
│       ├── config/                 # 人工导入、论坛及订阅配置
│       ├── constant/               # 人工白名单、论坛状态等
│       ├── parser/                 # 白名单输入解析与校验
│       ├── pojo/{entity,dto,vo,query}/
│       │                           # reference、forum、watch 各自的数据对象
│       ├── repository/             # 别名/术语、论坛文章、订阅规则持久化
│       ├── service/                # reference、forum、watch 服务及 impl
│       ├── cache/                  # 玩家输入解析索引失效、订阅去重状态
│       ├── mapper/                 # 人工维护数据与展示对象转换
│       └── event/watch/            # 接收世界状态变化并转交订阅 Service
├── plugin/
│   ├── runtime/                    # worldstate、arbitration、market、riven 命令
│   └── persistent/
│       ├── official/               # 装备、制作、物品、遗物、字典等静态查询命令
│       └── curated/                # reference、forum、watch 命令
├── controller/
│   ├── runtime/worldstate/         # 动态世界状态查询、原文与刷新入口
│   └── persistent/                 # official 管理；curated/reference、forum、watch 管理
├── schedule/
│   ├── runtime/                    # 各实例动态源轮询、仲裁图片预热、周紫卡刷新
│   └── persistent/                 # 官方静态字典/图片索引刷新；论坛临时文件清理
├── mq/
│   ├── consumer/                   # 内部继续按 persistent/来源/业务分组
│   ├── message/
│   ├── messagedescriptor/
│   └── consumerdescriptor/
└── storage/persistent/official/    # 冻结原始 JSON、快照和审核差异文件存储
```

没有独立 `catalog` 服务层，也没有根 `shared`、`parser`、`rawdata` 或混装 `pojo`。业务 Service 可依赖归属明确的另一来源 Service：玩家输入解析由 `curated/service/reference` 结合人工别名和官方名称完成；官方名称、引用、关系及静态代码簿查询归 `official`。

官方写服务通过 `WarframeOfficialDataChangePublisher` 在事务提交后发布本地事件，玩家输入索引自行失效并发送跨实例 MQ；回滚不触发失效，MQ 消费者仅清理本地缓存。世界状态本地事件与跨实例缓存消息保持各自作用域。订阅监听器只转交事件，规则匹配、事件认领、去重和通知发送位于 `curated/service/watch`。

`guide` 查询目前缺少实际钓鱼攻略/坐标数据，合成目标查询也没有对应地点数据源；这次归包保留既有空结果语义，不新增推测模型或数据表。所有目录均对应实际代码，不为对称性建立空包。

## 来源与维护状态

| 来源类别 | 持久化边界 |
| --- | --- |
| 官方 Nnm 静态模型 | `persistent/official/pojo/entity/<业务>`、`persistent/official/repository/<业务>`、`wf_official_*` |
| 社区人工资料 | `persistent/curated/pojo/entity/reference`、`persistent/curated/repository/reference`、`wf_community_*` |
| 论坛 | `persistent/curated/pojo/entity/forum`、`persistent/curated/repository/forum`、论坛业务表 |
| 用户/群组订阅 | `persistent/curated/pojo/entity/watch`、`persistent/curated/repository/watch`、订阅规则表 |

官方模型支持自维护记录。`OFFICIAL`/`SELF_MAINTAINED` 是官方模型中的记录维护状态，不是另一类数据源，不建立 `product/catalog` 来源或相应表。资料查询服务直接放入 `persistent/official/service/<业务>`；别名术语、论坛和订阅服务放入 `persistent/curated/service/<业务>`，不再建立 `catalog` 服务层。

社区导入仅处理物品别名、术语/黑话、仲裁精华、紫卡别名和组合规则。原通用社区来源登记和自动融合已移除，没有旧新双写或兼容路径。WorldState、仲裁排期、周紫卡和 Market 实时响应各走运行时链路，不归入静态官方表或社区导入。

## 每个 JSON 的独立模型

当前契约覆盖 71 个官方静态 JSON。每个文件有独立主实体、主表、具体 Repository 和类型化 DTO；批次/文件元数据 Repository 归入 `persistent/official/repository/snapshot`，自维护重建标记 Repository 归入 `persistent/official/repository/maintenance`；完整对应关系见 [官方JSON与模型目录](reference/官方JSON与模型目录.md)。生成器以 `official-models.json` 的明确字段合同生成模型，不用字段猜测替代模型。

例如 `equipment/weapons.json` 对应 `OfficialWeaponRoot`、`OfficialWeaponRepository`、`OfficialWeaponDTO`。`Root` 只表示这个 JSON 内一条业务记录的主实体，实体原名保持不变，不是跨 JSON 的父实体。仅必要对象集合拆子表；现有 JSON 字段覆盖、缺失值和表层级不因目录整理变化。

主表声明 `@OneToMany(mappedBy=..., cascade=ALL, orphanRemoval=true)`，子表通过 `@ManyToOne` 和 `@JoinColumn(...NO_CONSTRAINT)` 反指直接父表。主表 `id` 是本地主键，主表 `bkey` 是文件内稳定业务键；子表有自己的 `id`，其 `bkey` 为直接父表的 `id`，孙表同样指向直接父表而非最上层主表。子表 `bkey` 使用 `bigint`，不再新增 `parent_id`。普通关联 ID 列不建物理外键；应用在写入和删除时维护完整性，子表不建立独立写 Repository。

## record 与 snapshot 的含义

`snapshot` 表示官方文件的冻结输入及其版本、下载和审核管理上下文，例如文件 SHA、冻结快照、审批差异和已采纳版本。`persistent/official/pojo/dto/snapshot` 中是这些管理数据，不是一张承载全部官方物品的总表。

`record` 表示某个 JSON 内的一条业务记录。`persistent/official/pojo/dto/record` 中的公共摘要服务于跨文件搜索、分页和后台管理；`WarframeOfficialRecordReadDTO` 是带可选具体 DTO 的只读投影。它们不等于统一父实体或统一根 Repository。

**`WarframeOfficialRecordReadService` 是读取门面，不是 Repository 或数据表。** 它做三件事：通过 `OfficialRepositoryRegistry` 按 `WarframeOfficialDataDomain` 分派到各 JSON 自己的主表 Repository；同一业务键存在官方/自维护版本时整条记录优先选自维护版本；通过 `OfficialTableRelations` 将所选主记录的子表组装到 `WarframeOfficialRecordReadDTO.data`。它服务于跨 JSON 搜索、命令代码簿和暂未逐 JSON 类型化的通用读取。

71 个具体 DTO 既是类型化导入合同，也是关系表详情的非受管回读对象。`OfficialTableRelations` 按持久化合同恢复具体 DTO，`WarframeOfficialRecordReadDTO.data` 保存这些 DTO 的公共父类型 `OfficialObjectDTO`，不再构造 `JsonNode`。业务查询直接访问目标 Repository，并读取具体 DTO 的访问器；跨文件搜索和后台管理可使用公共摘要，但不把读取门面当成新的总 Repository。源记录里的商品 ID 等字段保留各自业务含义，不充当子表的本地主键或父级关联键。

Java 的 `record XxxDTO(...)` 另指 Java 不可变数据载体语法，与目录名 record、数据库行或 dataset 都不是同一概念。

## 解析、预览与采纳

1. 官方静态源使用固定 tag/完整 commit，先核验 manifest、路径、SHA-256、字节数和记录合同。
2. `persistent/official/parser` 把单个 JSON 记录绑定到其独立 DTO，再转换为所属 JPA 模型；不把外部 JSON 直接绑定 Entity。未知字段应更新合同和生成器后处理，不能静默丢弃。
3. 原始 JSON 保留用于差异审核和精度、空值审计。业务查询读取已采纳关系表，不通过审计原文还原业务数据。
4. 下载、文件存储、校验与业务审批分别属于 `persistent/official/client`、`storage/persistent/official`、`persistent/official/parser` 与 `persistent/official/service/dataset`。Service 只编排这些协作对象和 Repository。
5. 预览冻结差异，采纳不再次联网；head 行锁保护基线与版本切换。官方同身份与自维护数据并存时，查询优先完整自维护记录，不能混入另一条记录的子表。
6. 修改官方记录会进入自维护流程；删除官方记录后的重建提示、单文件/整包采纳、同 hash 重新解析和事务回滚以审批服务和测试为准。

社区人工输入、JSON 上传及允许的 HTTPS raw 输入绑定 `persistent/curated/pojo/dto/reference` 白名单对象，共用校验、差异预览和选择采纳流程。预览不生成删除项，选中的操作以一个事务提交；未选中记录保持原值。查询参数在 `persistent/curated/pojo/query/reference`，输出在 `persistent/curated/pojo/vo/reference`。

## 运行时数据

WorldState 与仲裁在每个 JVM 拉取、解析并保存有界快照；失败保留最近成功值并输出陈旧或覆盖缺口。周紫卡按平台使用 cache2k，Market 按真实目录身份访问实时 API。静态包仅作名称、任务、奖励池等代码簿，不推导实时任务或本轮奖励。

`runtime/pojo/dto/worldstate/WorldStateDTO` 是运行时快照 DTO；`runtime/mapper/worldstate` 将其投影为命令数据并映射展示 VO，`runtime/event/worldstate` 定义本地 Spring 状态更新事件，`persistent/curated/event/watch` 负责本地订阅监听，`runtime/cache/worldstate` 保存运行时快照与其查询索引。Spring 本地事件不进入 MQ。具体命令语义见 [Warframe数据与命令实现](Warframe数据与命令实现.md)。

固定协议输入均使用具体字段和 `@JsonProperty`，生产解析不使用 `JsonNode`、`readTree()` 或 `valueToTree()`。WorldState 先绑定 `runtime/rawdata/worldstate/OfficialWorldStateRawData`，再由 `OfficialWorldStateMapper` 转为 `WorldStateDTO`；补充赏金、仲裁、Market、周紫卡使用 `runtime/rawdata` 对应业务包中的原始类型。官方版本发现协议归入 `persistent/official/client/github/protocol`。单值对象在解析结果中按业务含义展平，真实多值内容保留为明确列表。

补充赏金由 `BountyCycleRawData` 自动绑定，然后通过访问器读取节点、挑战和轮次，结合已采纳的静态引用批量补全。官方已有任务优先；补充结束时间必须与官方毫秒完全相同。错类型、缺失必填字段或多余 JSON 不合并；补充源故障仍保留官方字段与有效静态奖池。Market 的一版与二版字段差异只作为实际外部协议适配保留。

`parser` 保留官方逐记录流式绑定、社区容器与字段校验、仲裁文本协议、周紫卡校验和 Market 原始对象转换职责。它不负责路由 Repository、维护业务缓存或组装页面。管理端原文诊断面板可展示未知字段映射，但业务解析仅使用已声明字段。集合和文本的防御性分支使用 Hutool；需要终止操作的业务前置条件使用 `BusinessAssert`，不把普通空集合分支改成异常。

方法使用中文 Javadoc 说明用途、每个入参和返回值；向外传播的受检异常写明抛出条件。只在外部协议错误转换、必要的快照恢复、逐通知隔离和资源清理边界捕获异常；普通编排和定时入口直接传播失败，由上层边界统一处理。

官方静态数据刷新按钮调用 `POST /api/admin/v1/warframe/official/refresh`，提交 `ref` 与可选 `productPath`。接口返回后台任务标识，任务完成后前端取得冻结预览并显示审核差异；采纳后才更新生效数据。对应后端为 `WarframeDataManagementController.refreshOfficial`，前端为 `src/api/warframe.ts` 的 `fetchRefreshWarframeOfficial` 和 `src/views/warframe/data.vue` 的 `refreshOfficial`。

## 生成与验证

- `scripts/database/generate-official-models.cjs`：实体、Repository、模型合同及 SQL 生成入口。
- `scripts/database/generate-official-inputs.cjs`：逐 JSON 类型化 DTO。
- `scripts/database/generate-official-dataset-matrix.cjs`：从实际模型生成 71 行目录，不再输出推测自然键的万行字段清单。
- `OfficialModelTreeTest`：实际数据字段类型验证及代表关系树持久化往返。
- `WarframePackageStructureTest`：独立主表/Repository、来源目录、DTO/VO/Query 和调用边界。

代表树测试不表示全部真实记录均已导入。数据库检查只用隔离 PostgreSQL，实际本地数据库由开发者按 [SQL说明](sql/README.md) 手工维护。
