# API 与代码结构说明

本文是阅读 NnmBot 管理端 API 和主要代码调用链的导航入口。接口统一挂在
`/api/admin/v1` 下；协议适配放在 Controller，业务规则放在 Service，数据库由
Repository 访问，外部 HTTP 由 Client 访问。

## 前后端入口

| 范围 | 入口 |
| --- | --- |
| 前端请求封装 | `nnm-admin-web/src/api/` |
| 前端认证与普通 HTTP | `nnm-admin-web/src/utils/http/` |
| 前端长任务 SSE | `nnm-admin-web/src/api/admin-task.ts`、`src/utils/http/sse.ts` |
| 登录及令牌 | `nnm-admin/src/main/java/com/nnm/bot/admin/controller/AuthController.java` |
| 账号、菜单和权限 | `nnm-admin/src/main/java/com/nnm/bot/admin/controller/RbacManagementController.java`、`RbacController.java` |
| 当前用户菜单和按钮权限 | `nnm-admin/src/main/java/com/nnm/bot/admin/controller/RbacController.java` |
| 管理任务状态及 SSE | `nnm-admin/src/main/java/com/nnm/bot/admin/controller/AdminTaskController.java` |
| Warframe 管理 API | `nnm-warframe/src/main/java/com/nnm/bot/warframe/controller/WarframeDataManagementController.java` |
| Warframe 管理页面 | `nnm-admin-web/src/views/warframe/` |

前端的 `v-auth`、`v-roles` 仅控制交互显示；后端使用 Sa-Token 和
`@SaCheckPermission` 进行实际鉴权。当前用户按钮权限由 `/api/admin/v1/me` 提供。

## 常用接口

以下路径均省略前缀 `/api/admin/v1`。接口通过统一 `ApiResponse` 返回；分页使用
`PageResult`。

| 方法 | 路径 | 权限 | 用途 |
| --- | --- | --- | --- |
| `POST` | `/auth/login` | 未登录 | 登录并取得管理端令牌 |
| `POST` | `/me` | 已登录 | 当前用户、菜单和按钮权限 |
| `POST` | `/warframe/community/datasets` | `warframe:community:view` | 列出社区数据集和来源元信息 |
| `POST` | `/warframe/community/{dataset}/query` | `warframe:community:view` | 分页查询记录及结构化详情 |
| `POST` | `/warframe/community/{dataset}/refresh` | `warframe:community:sync` | 下载、解析并生成 Redis 预览任务 |
| `POST` | `/warframe/community/refresh` | `warframe:community:sync` | 批量生成社区预览 |
| `POST` | `/warframe/community/{dataset}/upload-preview` | `warframe:community:update` | 上传文件并生成预览，不写数据库 |
| `POST` | `/warframe/community/{dataset}/preview/{previewId}` | `warframe:community:view` | 按 `sourceCode` + `changeType` 独立分页读取预览，分组包含整个批次的操作 ID |
| `POST` | `/warframe/community/{dataset}/merge` | `warframe:community:apply` | 按请求选择合并记录 |
| `POST` | `/warframe/community/{dataset}/records` | `warframe:community:update` | 新增自维护记录 |
| `POST` | `/warframe/community/{dataset}/records/{id}` | `warframe:community:update` | 编辑自维护记录 |
| `POST` | `/warframe/aliases/query` | `warframe:community:view` | 查询物品别名 |
| `POST` | `/warframe/aliases` | `warframe:community:update` | 新增物品别名 |
| `POST` | `/warframe/aliases/{id}` | `warframe:community:update` | 更新物品别名 |
| `POST` | `/warframe/aliases/{id}/delete` | `warframe:community:update` | 删除物品别名 |
| `POST` | `/tasks/{taskId}` | 创建任务时绑定的权限 | 查询长任务当前状态 |
| `GET` | `/tasks/{taskId}/events` | 创建任务时绑定的权限 | 订阅长任务 SSE 事件 |

WorldState、官方只读数据和物品别名等接口可从
`WarframeDataManagementController` 及前端
`nnm-admin-web/src/api/warframe.ts` 按路径继续查找。

## 社区数据刷新链

```text
community-datasets.vue
  -> src/api/warframe.ts
  -> WarframeDataManagementController
  -> WarframeCommunityDatasetService
  -> WarframeCommunitySourceLoader 并行下载、解析多个来源
  -> WarframeCommunityApiClient 在每个来源内按 CDN 顺序回退
  -> Service 对成功来源独立比对 / 结构化投影
  -> WarframeCommunityPreviewStore 保存 Redis 分片预览及来源摘要
  -> AdminTaskController SSE 通知完成并返回轻量预览摘要
  -> 前端用 previewId 查询完整差异
  -> 管理员逐条选择
  -> merge 接口
  -> Service 保护 SELF_MAINTAINED 后写入 Repository
  -> PostgreSQL
```

刷新和上传预览都不自动写业务数据库。预览使用 Redis 并有过期时间；只有管理员提交
合并请求才会修改记录。身份由 `sourceCode + datasetCode + recordKey + locale`
确定。社区数据为 `COMMUNITY`，自维护数据为 `SELF_MAINTAINED`；上游不能覆盖或删除
自维护记录。PublicExport 属于官方只读数据，不提供社区 CRUD。
预览按来源和新增/修改/删除折叠，支持来源、类型和本页选择；来源全选可以跨页，
每个“来源 + 变更类型”拥有独立的页码和记录缓存，切换一个分组的页码不会改变其他分组，
不需要把所有完整 JSON 返回浏览器。Redis 使用分片和最后发布的 header，
不是多键事务；数据库变更和幂等回执同事务提交后才消费 Redis。

预览的 `sources` 包含成功和失败来源，字段包括 `sourceCode`、`status`、脱敏 `endpoint`、
`elapsedMs`、`recordCount`、`errorMessage`、`operationCount` 和 `firstOperationIndex`。
失败来源原数据不变，不产生任何更新或删除候选；成功来源的 diff 仍可选择并采纳。
全部来源失败仍返回可查看的无操作预览，但不建立待确认索引，不阻塞下一次刷新。
来源与中英文记录按配置顺序独立保留，不能用并行完成顺序或同键去重覆盖英文。

PreviewStore 统一封装记录/差异分片、独立分组索引、GZIP 压缩、重复字段省略、旧格式兼容
及清理；业务 Service 保留并发基线校验、操作选择和事务提交时机。普通分页仅读取相交的
记录分片；来源和变更类型筛选使用 operation ID 到记录位置的索引，只读取当前页命中的记录
分片，正式合并校验完整快照；original 基线不因缩减 Redis 占用而丢弃。

## 长任务与 SSE

业务 Controller 负责校验业务权限并提交 `AdminTaskService`。SSE 统一由
`AdminTaskController` 提供，前端使用 `waitAdminTask` 订阅并处理断线重连、终态和错误。
任务所需权限与任务绑定，因此只持有任务 ID 不能绕过业务权限。大结果通过任务结果摘要
返回，完整数据应使用业务预览查询接口按需读取。

## 仲裁排期数据集

```text
管理端
  -> WarframeDataManagementController
  -> WarframeCommunityDatasetService
  -> ArbitrationScheduleParser -> WorldState 代码簿
  -> Redis 预览
  -> SSE + previewId
  -> 管理员选择
  -> PostgreSQL wf_arbitration_schedule
```

`wf_arbitration_schedule` 保存任务排期；`wf_community_arbitration_essence` 只保存
精华/效率补充规则。仲裁查询、今日仲裁、高效仲裁和仲裁表分别按当前任务、当天全部任务、
未来十条高精华任务和未来排期组织输出。

管理端“Warframe 社区数据 -> 仲裁排期”使用普通社区接口，`dataset` 为
`arbitration_schedule`。`query` 查询已采纳排期，`refresh` 生成分片预览，
`preview` 分页和分组，`merge` 提交选中操作；时间窗口外旧排期不生成删除候选。

元数据为 `refreshable=true`、`manageable=false`、`readOnly=true`；
只读禁止人工新增、上传、修改和删除，不禁止刷新、预览和采纳上游变更。
后端校验这些人工入口，前端按 `manageable && !readOnly` 控制入口；
统一批量刷新包含已配置的仲裁排期，没有独立仲裁定时任务直接写库。

旧 `/warframe/arbitration/refresh-preview`、`/warframe/arbitration/preview/{previewId}`、
`/warframe/arbitration/merge` 仍保留兼容，但不是当前管理端主入口；
旧专用预览和统一社区预览的 ID/协议不可互换。

## 本地联调结果

2026-09-29 使用本地 `8080` 后端、`3006` 管理端、PostgreSQL 和 Redis 验证：

- 管理员登录、`/me` 按钮权限和社区数据集列表成功；
- `arbys.txt` 刷新收到 `RUNNING`/`COMPLETED` SSE，生成 44,056 条 Redis 预览，
  未提交合并时 `wf_arbitration_schedule` 保持 1,018 条；
- `translation` 社区刷新收到完整 SSE，生成 9,882 条预览，未提交合并时数据库记录数不变；
- 预览详情包含来源身份、结构化字段、当前 JSON、上游 JSON 和原始 JSON。

以上是 2026-09-29 的历史联调，不代表 2026-10-08 的分片协议和统一仲裁入口已完成真实环境验收。
刷新与预览链路可以从管理端进入使用；逐条合并、自维护记录保护和过期预览拒绝
仍需在管理端操作窗口中继续验收。

## 登录令牌说明

当前应用组装模块引入 `sa-token-redis-template`，且分布式集成测试会断言实际
`SaTokenDao` 使用 RedisTemplate；登录会话具备共享存储所需组件，所有实例还必须连接同一
Redis 并使用一致的 Sa-Token 配置。当前完整测试 profile 暂时排除了该 Redis Dao，用于
Redis 可选的普通测试。

登录和续签使用普通 `StpUtil.login()` 机制，不启用自定义 access/refresh 双 Token。
响应中的 `refreshToken` 字段继续保留，作为旧前端协议的兼容字段；新代码不把它当作
独立刷新令牌使用。`POST /auth/refresh` 的请求体是 `{"token":"当前登录令牌"}`：
校验令牌和用户可用后执行 `StpUtil.renewTimeout()`，延长当前令牌有效期，返回同一个值，
不重新登录、不轮换令牌，也没有独立 access/refresh 双令牌模型。

前端 HTTP 和 SSE 共用单飞续签；SSE 遇到 401 后尝试续签一次，再用当前令牌重连。
这不意味着已经失效、退出或被禁用账号的令牌能恢复。当前 YAML 的绝对有效期为
`timeout: 7200`，不能仅凭 `active-timeout: 2592000` 或自动续签注释认定“活跃就能一直
登录”：当前前端主要在 401 后续签，绝对过期后无法靠该接口恢复。有效期内主动续期/
滑动有效期策略仍需单独闭环，具体状态见
`Warframe数据来源待决策与实现差距.md` 的最终验收项；本轮字段兼容处理未改变该策略。

## 数据库 SQL 边界

`docs/sql/init.sql` 是空库初始化基线，只用于新建或明确重建空库，不是升级脚本。
2026-10-10 已按要求删除两份独立修复 SQL；本轮未修改基线，翻译、奖励池字段和废弃社区表
仍有对齐缺口，不能将基线称为完整目标态。已有本地数据库不要直接重放 `init.sql`；
执行任何增量 SQL 前先比较目标表结构、约束和脚本适用基线。SQL 说明见 `docs/sql/README.md`。

后端不自动建表或迁移结构，Hibernate 使用 `ddl-auto: validate`。业务调用链中不拼接
原生 SQL；常规读写由 Spring Data JPA Repository 完成。
