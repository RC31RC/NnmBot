# NnmBot 与 NyxBot 及 charge-basic 对比与演进规划

> 文档日期：2026-07-27  
> 适用项目：`/Users/ren/RcGit/NnmBot`  
> 参考项目：`/Users/ren/RcGit/NyxBot`、`/Users/ren/RcGit/charge-basic`

## 1. 背景与结论

NnmBot 是依托 NyxBot 重新设计的进化版。两者的业务目标一致，都是面向 Warframe 玩家提供 QQ/OneBot 机器人服务，核心能力包括：

- 查询 Warframe 世界状态；
- 查询市场、遗物、紫卡和其他静态数据；
- 通过群聊命令返回文字或图片结果；
- 按群组和用户订阅世界状态变化并主动通知；
- 通过管理端维护机器人、数据和运行状态。

当前 NnmBot 更准确的定位是“已经搭好模块边界、认证 RBAC 和基础设施，但机器人业务闭环尚未完成的单机版重构工程”，而不是 NyxBot 的功能等价替代品。当前日志中插件加载数量为 `0`，世界状态轮询仍停留在获取 JSON；管理端已能支撑登录和系统权限管理，机器人业务管理仍待实现。

建议采用以下总原则：

1. 先让单机版完成一个可用的 Warframe Bot 闭环：消息接收、命令匹配、数据查询、消息发送。
2. 继承 NyxBot 已验证的功能模型和数据处理经验，但重新组织领域模型、插件接口和通知链路。
3. 借鉴 charge-basic 的工程化方法，不直接复制其面向分布式系统的基础设施。
4. 将数据源、命令、通知和渲染做成可替换模块，为后续新功能和多实例部署保留演进空间。

## 2. 三个项目的定位

| 项目 | 定位 | 当前最值得借鉴的内容 |
| --- | --- | --- |
| NnmBot | NyxBot 的进化版，当前以单机和模块化为主 | Java 21、模块边界、`BotPlugin` SPI、渲染抽象、统一 HTTP 封装 |
| NyxBot | 已有较完整业务能力的 Warframe QQ Bot | 功能菜单、OneBot 事件处理、世界状态变化检测、订阅通知、Warframe 数据初始化 |
| charge-basic | 通用业务基础组件和工程实践集合 | 配置属性、自动配置、异常体系、HTTP 封装、线程池、事务、幂等和 Outbox 思路 |

三者不是同一层次的项目。NyxBot 主要提供业务参考，charge-basic 主要提供工程设计参考，NnmBot 负责结合当前需求重新取舍。

## 3. NnmBot 当前状态

### 3.1 模块结构

当前顶层模块如下：

- `nnm-common`：统一响应、分页、异常、业务断言、重试退避和 HTTP 客户端；
- `nnm-core`：机器人消息事件、插件 SPI、插件分发、有界执行器和调度器；
- `nnm-render`：Thymeleaf、OpenHtmlToPdf、Playwright 渲染；
- `nnm-warframe`：Warframe 领域模型、API 客户端、世界状态服务；
- `nnm-scheduler`：世界状态轮询任务；
- `nnm-admin`：管理端登录、Sa-Token RBAC、系统管理和运行监控接口；
- `nnm-app`：Spring Boot 启动模块、配置、数据库迁移。

这个拆分方向是合理的。尤其是将 `core`、`warframe`、`render` 和 `scheduler` 分开，有利于后续增加非 Warframe 插件和其他数据源。

### 3.2 已有骨架

NnmBot 已经具备以下可继续发展的基础：

- `BotPlugin` 定义了 `match`、`handle`、`command`、`description`、`order`；
- `PluginDispatcher` 会按顺序遍历插件并处理消息；
- `WorldState` 已使用 Java record 建立了基础领域模型；
- `WorldStateService` 已预留变化检测器列表和 Spring 事件发布器；
- `ChangeDetector`、`MessageBuilder` 已抽象出变化检测和消息构建职责；
- HTTP 客户端已提供 GET 重试、超时、429/5xx 判断；
- 渲染模块已支持图片模板和两种渲染引擎；
- 数据库 V1 已预留用户、管理员、订阅、通知历史和别名表；
- 应用已启用 Spring Scheduling、cache2k、Flyway 和 Actuator；Caffeine 仅保留为评估回退。

### 3.3 当前未形成闭环的部分

当前最重要的问题不是缺少更多模块，而是现有模块还没有连成可运行业务：

1. 没有实际的 OneBot 消息事件监听器将 `AnyMessageEvent` 转换并交给 `PluginDispatcher`。
2. 当前没有实际的 `BotPlugin` 实现，因此插件分发器启动时加载 `0` 个插件。
3. `WorldStatePollingTask` 获取 JSON 后仍有 `TODO`，没有反序列化成 `WorldState`。
4. `WarframeApiClient` 失败时返回 `null`，调用方无法区分网络失败、限流、数据错误和空结果。
5. `WorldStateService` 检测到变化时只记录日志，没有发布领域事件或发送通知。
6. `MessageBuilder` 只有接口，没有世界状态通知实现。
7. `nnm-admin` 已完成 Sa-Token 登录认证和 RBAC 基础管理，机器人配置和业务数据维护仍待实现。
8. RBAC 实体、Repository、应用服务和管理接口已形成闭环，其他业务表仍需随功能逐步实现。
9. 通用执行器已改为有界线程池并提供背压，但渲染和数据刷新等高成本任务仍需按业务增加专用限流。
10. 开发和生产环境已统一使用 PostgreSQL、Flyway 和 `ddl-auto: validate`，H2 仅用于自动化测试。
11. HTTP 客户端已默认启用标准 TLS 证书校验，代理专用客户端和代理认证仍待按实际服务实现。
12. `OpenHtmlToPdfEngine` 采用 PDF 再转 PNG 的路径，长内容分页、字体和图片资源需要专项测试。
13. Playwright 当前每次调用都可能启动和关闭 Chromium，适合高保真少量渲染，不适合高频通知场景。

## 4. NyxBot 功能菜单

以下菜单来自 NyxBot 的 `CommandConstants`、`Codes` 以及对应插件实现。它们是 NnmBot 的业务基线，不代表当前 NnmBot 已实现。

### 4.1 基础功能

| 功能 | 命令或别名 | 权限 |
| --- | --- | --- |
| 帮助菜单 | `帮助`、`HELP`、`help`、`指令`、`命令`、`菜单` | 普通用户 |
| 运行状态 | `检查版本`、`版本`、`运行状态`、`状态` | 普通用户 |

帮助菜单实际还承担功能发现职责。NnmBot 后续应从插件元数据自动生成菜单，避免维护一份独立且容易过期的字符串列表。

### 4.2 世界状态和任务查询

| 功能 | 命令或别名 |
| --- | --- |
| 警报 | `警报` |
| 突击 | `突击` |
| 执刑官猎杀 | `执刑官猎杀`、`猎杀`、`执行官`、`执政官`、`执刑官` |
| 虚空商人 | `奸商` |
| 仲裁 | `仲裁`、`仲裁表` |
| 每日特惠 | `每日特惠`、`特惠` |
| 入侵 | `入侵` |
| 普通裂隙 | `裂隙`、`裂缝` |
| 九重天裂隙 | `九重天裂隙`、`九重天`、`九重天裂缝` |
| 钢铁裂隙 | `钢铁裂隙`、`钢铁裂缝` |
| 钢铁奖励 | `钢铁奖励` |
| 平原循环 | `平原`、`夜灵平原`、`夜灵平野`、`福尔图娜`、`魔胎之境`、`扎里曼` |
| 希图斯赏金 | `希图斯`、`地球赏金` |
| 英择谛赏金 | `英择谛`、`火卫二赏金` |
| 索拉里斯赏金 | `索拉里斯`、`金星赏金` |
| 双衍王境轮换 | `轮换`、`双衍王境` |
| 电波 | `电波` |
| Warframe 1999 日历 | `1999`、`日历` |

### 4.3 市场、物品和辅助查询

| 功能 | 命令或别名 | 典型输入 |
| --- | --- | --- |
| Warframe.market 物品订单 | `/WM`、`WM`、`MARKET`、`市场` | `WM ash prime` |
| 紫卡市场 | `/WR`、`WR`、`WMR` | `WR soma` |
| 紫卡市场查询 | `/RM`、`RM` | `RM soma` |
| 赤毒武器 | `/CD`、`CD`、`赤毒` | `CD 赤毒布莱顿` |
| 信条武器 | `/XT`、`XT`、`信条` | `XT 信条武器` |
| 佩兰数列/信条武器辅助 | `佩兰`、`佩兰数列`、`信条武器` | 无参数 |
| 金垃圾 | `金垃圾` | 无参数 |
| 银垃圾 | `银垃圾` | 无参数 |
| 遗物查询 | `核桃`、`查核桃` | `核桃 Meso A1` |
| 遗物开启辅助 | `开核桃`、`砸核桃` | 具体遗物输入 |
| 紫卡分析 | `紫卡分析`、`分析紫卡` | 带武器和词条 |
| 翻译 | `翻译...` | 带英文或中文名称 |

### 4.4 管理员数据维护

以下命令在 NyxBot 中限制为超级管理员，且部分要求私聊使用：

- `更新WM物品`
- `更新WM紫卡`
- `更新信条`
- `更新翻译`

NnmBot 不应在第一阶段照搬全部管理命令。建议先提供后台或受保护的服务方法，再为高频操作补充管理员命令。

### 4.5 订阅通知

NyxBot 提供：

- `订阅`
- `取消订阅`

订阅模型并非简单的“群组 + 类型”开关，而是：

- 一个群组对应一个订阅组；
- 一个订阅组下可以有多个用户；
- 一个用户下可以有多个订阅规则；
- 规则可包含任务类型、遗物等级和入侵奖励过滤条件；
- 推送时按用户规则筛选，并在群消息中 `@` 匹配用户；
- 群组被踢出时可以清理对应订阅；
- `NotificationHistory` 通过事件类型、过期时间等信息避免重复通知。

NnmBot 的 V1 表结构只实现了订阅的最小雏形，尚未覆盖 NyxBot 的用户级规则关系，需要在真正实现订阅前重新设计。

### 4.6 NyxBot 中仍有待完善的功能

NyxBot 的命令常量中还存在部分未完整实现或依赖外部数据的能力：

- 紫卡倾向变动；
- 翻译；
- 佩兰数列；
- 信条武器；
- 紫卡市场；
- 部分 Riven 相关功能。

NnmBot 应将这些列为“继承后验证”的功能，而不是只根据命令常量判断为已完成。

## 5. NyxBot 可继承的业务经验

### 5.1 世界状态数据链路

NyxBot 的 `WarframeDataSource` 体现了较完整的数据生命周期：

1. 初始化阶段下载或读取本地缓存；
2. 翻译、别名、节点、武器、战甲、遗物等静态数据分阶段初始化；
3. 各类数据初始化任务可以并行执行；
4. 单个数据源失败时允许降级，不影响所有功能；
5. 世界状态保存在本地缓存中；
6. 数据初始化完成后启动状态轮询。

NnmBot 应保留这种“远程数据 + 本地缓存 + 部分可用”的思路，但实现得更简单：

- 第一阶段只需要缓存最近一次成功的世界状态；
- 静态数据按功能逐步加入，不需要一开始初始化 NyxBot 的所有数据表；
- API 失败时应返回明确的 `ApiResult` 或抛出领域可识别异常；
- 数据源需要带版本、抓取时间和来源，方便后台查看数据是否过期。

### 5.2 变化检测和通知

NyxBot 将变化检测器、消息构建器、订阅应用服务和通知历史分开，这是 NnmBot 当前接口设计可以继续发展的方向。

建议将 NnmBot 的链路固定为：

```text
WorldStatePollingTask
    -> WorldStateParser
    -> WorldStateService
    -> WorldStateChangedEvent
    -> ChangeDetector
    -> NotificationService
    -> NotificationTargetRepository
    -> MessageSender
```

其中：

- `WorldStateService` 只负责接收新状态和维护当前状态；
- `ChangeDetector` 只负责识别变化；
- `NotificationService` 负责订阅匹配、通知历史和发送编排；
- `MessageSender` 屏蔽 OneBot 发送文字、图片和合并消息的差异；
- 渲染模块只负责把领域数据变成图片，不直接访问订阅或机器人对象。

### 5.3 数据初始化与降级

NyxBot 的分阶段初始化、失败降级、本地缓存和后台数据刷新值得保留。NnmBot 应补充以下约束：

- 每个数据源都有独立的超时、重试和缓存策略；
- 静态数据刷新不应阻塞消息处理；
- 刷新失败必须保留旧数据；
- 刷新过程需要可观察：开始时间、结束时间、成功数、失败原因；
- 同一数据源不允许并发刷新；
- 管理员触发刷新时需要权限检查和操作日志。

### 5.4 工具与全局配置审查

本次只吸收了有明确调用点、无业务状态且能统一基础设施行为的能力：

- `RetryBackoff`：纯静态、无状态的有上限指数退避计算，已接入统一 HTTP 客户端；
- `HttpClientProperties`：集中管理连接超时、读取超时、重试次数、退避上限和 TLS 开关；
- `WarframeApiProperties`：集中管理世界状态、Market 地址、语言和平台；
- `WarframePollingProperties`：集中管理轮询开关和间隔，测试环境不访问真实外部 API；
- `WarframeCacheProperties`：分别管理世界状态、元数据、翻译和数据源 tag 的容量与生命周期；
- URL 使用 `URI` 和 `UriComponentsBuilder` 构造，不直接拼接用户输入的路径。

以下 NyxBot 实现不直接迁移：

- 大而全的 `StringUtils`、动态编译正则的 `MatcherUtils`，以及当前没有调用点的集合和压缩工具；
- 通过静态字段持有 Spring Bean、缓存或线程池的 `SpringUtils`、`CacheUtils`、`AsyncUtils`；
- 吞掉异常或不能保证原子替换的文件工具和缓存持久化工具；
- 自动读取系统代理、执行系统命令或修改 JVM 全局认证器的代理工具；
- 静态 HTTP 客户端和默认信任全部 TLS 证书的实现；
- 旧 `Date` API、业务耦合的消息解析和发送工具。

后续新增工具时不以“工具类数量”为目标。只有跨模块重复出现、异常语义明确、能够独立测试且
不持有可变全局状态的逻辑才进入 `nnm-common`；业务域专用能力留在对应模块。外部地址、
超时、重试、缓存容量和生命周期统一通过类型安全配置管理，避免散落常量和 `@Value`。

## 6. charge-basic 可借鉴的工程实践

charge-basic 当前使用 Java 17、Spring Boot 3.2.12，并包含自动配置、统一 HTTP、异常、事务、线程池、消息可靠性、存储和安全等通用能力。

### 6.1 适合当前单机阶段引入的思想

#### 统一配置属性

将配置集中到类型安全的 `@ConfigurationProperties` 中，例如：

- `nnm.warframe.api.*`
- `nnm.warframe.polling.*`
- `nnm.bot.message.*`
- `nnm.notification.*`
- `nnm.render.*`

避免在业务类中大量散落 `@Value`。尤其是 API 地址、语言、请求超时、重试次数、缓存时长和开关应统一管理。

#### 统一异常和结果模型

HTTP、数据解析、命令参数错误、权限不足和消息发送失败应有不同的错误分类。不要用 `null` 作为所有失败的返回值。

当前已从 charge-basic 的实现思路中吸收：

- `BusinessAssert`：统一业务前置条件和 `BusinessException` 构造；
- `PageResult<T>`：提供跨模块分页结果，并保持管理端现有 JSON 字段不变；
- 写操作按实际边界直接使用 Spring `@Transactional`，查询方法暂不统一添加事务注解；
- HTTP 请求保留真实成功状态码，不再把所有成功请求写死为 `200`。

建议至少区分：

- `ExternalApiException`：外部 API 请求失败；
- `ExternalDataParseException`：响应格式无法解析；
- `CommandParseException`：用户输入参数错误；
- `BotPermissionException`：无权执行；
- `MessageSendException`：OneBot 发送失败。

#### 可插拔发送器

建议定义：

```java
public interface MessageSender {
    SendResult sendText(MessageTarget target, String text);
    SendResult sendImage(MessageTarget target, byte[] image);
}
```

当前实现可以直接调用 Shiro，未来可以增加测试发送器、日志发送器或其他聊天平台适配器。

#### 统一线程池

当前已将无界虚拟线程执行器替换为可配置的 Spring 有界线程池和独立调度池：

- 通用异步任务：核心线程 2、最大线程 8、队列 100；
- 队列饱和时使用调用方执行策略形成背压；
- 定时任务使用 2 个调度线程；
- 关闭应用时等待任务完成，默认最多 30 秒；
- 参数统一由 `nnm.executor.*` 管理并在创建线程池前校验。

后续仍应按任务类型进一步隔离：

- 外部 HTTP 和消息发送：先使用通用有界线程池，确认吞吐后再决定是否使用受限虚拟线程；
- 图片渲染：限制并发的专用线程池；
- 数据刷新：单数据源互斥执行器；
- 定时轮询：单独的调度线程。

不能创建无界执行器，也不能用静态工具类持有全局线程池。

#### 本次明确不迁移的实现

以下 charge-basic 能力经过评估后不适合直接搬入当前项目：

- `ChargeRequestContext`：依赖隐式 `ThreadLocal` 状态，原实现还包含硬编码身份，不适合异步任务；
- `ChangeTrackerUtil`：使用反射并耦合 Hutool 和业务枚举，当前 RBAC 审计采用显式变更记录更清晰；
- `ChargeThreadPoolUtil`：静态注入和全局状态不符合当前构造注入与生命周期管理方式；
- `ChargeTransactionExecutor`：当前没有编程式事务调用点，不为尚未出现的场景增加闲置抽象；
- Redis、MQ、Dubbo、Nacos、PowerJob、分布式锁和 MyBatis-Plus：与当前单机、JPA 技术边界不匹配。

这些结论针对当前阶段。后续出现明确调用点或多实例需求时，应重新按问题本身评估，而不是机械保持或机械迁移。

#### 本地幂等记录

NyxBot 的通知历史和 charge-basic 的消息幂等思想都适用于单机。`notification_history` 应保留唯一约束，并让通知服务在发送前后定义清晰的状态：

- `PENDING`：准备发送；
- `SENT`：发送成功；
- `FAILED`：达到重试上限；
- `EXPIRED`：事件已过期。

当前只有“事件是否存在”的表结构，建议后续增加状态、重试次数、最后错误和更新时间。

### 6.2 当前不建议直接引入的能力

由于 NnmBot 暂时是单机项目，以下能力不应作为当前基础依赖：

- Dubbo；
- Nacos；
- RocketMQ；
- Redis 分布式锁；
- Sentinel；
- 多服务拆分；
- 分布式 ID；
- 面向多实例的全套 Outbox/MQ 治理链路。

这些能力只有在出现多实例部署、跨进程任务、异步业务解耦或可靠事件投递需求时才有必要。当前可以先用数据库唯一约束、本地锁、单机调度和本地缓存解决问题。

### 6.3 适合后续演进时采用的模式

当 NnmBot 从单机扩展为多实例时，再考虑：

- Outbox 记录世界状态变化和通知任务；
- Redis 或数据库锁保证同一数据源只有一个刷新者；
- MQ 分发变化事件；
- 独立通知服务；
- 分布式任务调度；
- 对消息发送做消费幂等和死信处理。

## 7. 推荐目标架构

### 7.1 业务模块建议

在现有模块基础上，推荐形成以下业务边界：

```text
nnm-core
  message-ingress       OneBot 事件接收、消息规范化
  command               命令元数据、参数解析、权限校验
  plugin                插件 SPI、插件注册、分发
  messaging             文本/图片/合并消息发送抽象

nnm-warframe
  worldstate             世界状态 DTO、解析、领域模型
  market                 Warframe.market 查询
  relic                  遗物与奖励池
  riven                  紫卡和倾向
  subscription           订阅、规则、匹配
  notification           变化检测、通知历史、通知编排
  data                   静态数据刷新、别名和翻译

nnm-render
  template               模板和视图模型
  engine                 渲染引擎

nnm-scheduler
  polling                世界状态轮询
  refresh                静态数据刷新
  cleanup                通知历史和过期缓存清理

nnm-admin
  auth                   登录、Sa-Token、用户权限
  bot                    机器人和群组配置
  data                   数据刷新和缓存状态
  observability          日志、指标、任务状态
```

这不是要求一次性创建全部包，而是用于约束职责。当前优先完成 `message-ingress`、`command`、`worldstate` 和 `notification` 的最小实现。

### 7.2 命令系统建议

当前已引入启动期 `CommandRegistry`。精确命令由索引直接路由，参数命令仍保留插件自定义匹配，
这样既能保持现有插件接口的兼容性，也能避免所有消息都遍历全部插件。后续随着菜单增加，
仍需避免单纯依赖每个插件自行维护元数据：

- 命令别名分散；
- 参数解析重复；
- 权限信息无法自动生成帮助菜单；
- 插件匹配顺序可能产生冲突；
- 无法统一生成命令文档和测试用例。

插件元数据使用不可变描述对象：

```java
public record CommandDescriptor(
        String code,
        String name,
        Set<String> aliases,
        String description,
        String permission,
        MatchType matchType,
        int order
) {}
```

当前调用链：

```text
原始消息
  -> 消息清洗
  -> CommandRegistry 精确索引
  -> 自定义插件回退匹配
  -> 参数解析
  -> 权限和场景校验
  -> 插件处理
  -> 统一发送结果
```

`CommandRegistry` 启动时校验稳定编码、旧 `BotPlugin.command()`、命令名称和别名的唯一性，
帮助菜单从已注册描述对象自动生成。数据库后续只保存启用状态、别名覆盖、权限覆盖和冷却时间，
不保存任意正则或 Java 类名。

### 7.3 世界状态模型建议

当前 `WorldState` 使用 NnmBot 自己的 record，是正确方向，但需要增加一个专门的外部 DTO 映射层：

```text
warframe.stat.us JSON
  -> ExternalWorldStateDto
  -> WorldStateMapper
  -> WorldState
```

不要让外部 API 的字段命名、可空字段和时间格式直接污染领域模型。映射层应处理：

- 缺失字段；
- 时间戳和 ISO 时间；
- 外部 API 列表为空或为 `null`；
- 外部字段新增；
- 外部数据类型不一致；
- 世界状态版本或抓取时间。

### 7.4 通知模型建议

建议在现有表的基础上逐步调整为：

```text
notification_target
  bot_id
  group_id
  enabled

notification_user
  target_id
  user_id
  user_name

notification_rule
  user_id
  event_type
  mission_type
  tier
  reward_filter

notification_history
  event_type
  event_id
  target_id
  status
  retry_count
  last_error
  sent_at
  created_at
```

第一阶段可以只支持群组级订阅；第二阶段再增加用户级过滤和 `@` 用户通知。这样可以降低首次实现复杂度，同时不阻断后续兼容 NyxBot 的高级订阅模型。

## 8. 分阶段路线图

### P0：可运行闭环

目标：启动后能接收并响应基础命令。

- 增加 OneBot 消息事件监听入口；
- 接通 `PluginDispatcher`；
- 实现 `帮助` 和 `状态`；
- 实现一个统一文本发送器；
- 实现世界状态 JSON 反序列化和 mapper；
- 实现 `警报`、`裂隙`、`突击`、`奸商`、`仲裁`、`平原`；
- 为每个命令补充单元测试和消息样例；
- 将 API 失败从 `null` 改为明确结果或异常；
- 修复开发环境和生产环境的数据库迁移约定；
- 保持生产环境 TLS 证书校验，代理认证使用独立配置和客户端。

P0 完成后，NnmBot 才具备“机器人项目”的最小可用性。

### P1：补齐 NyxBot 核心功能

- 完成所有世界状态菜单；
- 增加 Warframe.market 物品订单查询；
- 增加别名和翻译；
- 增加遗物查询和奖励池；
- 增加紫卡查询和基础分析；
- 实现群组订阅和取消订阅；
- 增加变化事件、通知构建和消息发送；
- 增加通知历史唯一约束和失败重试；
- 按最近过期时间动态调整世界状态轮询间隔；
- 增加本地状态缓存和 API 失败降级；
- 增加文字和图片两种输出模式。

### P2：管理与运维

- Sa-Token 登录、会话续期和刷新兼容接口；
- 用户、管理员、机器人权限；
- 群组和用户黑白名单；
- 数据源状态和手动刷新；
- 任务执行记录；
- 日志查询和错误详情；
- Actuator、指标和基础告警；
- 管理订阅组、订阅用户和规则；
- 统一配置展示，但敏感配置必须脱敏。

### P3：NnmBot 的新增能力

这些功能应建立在 P0/P1 稳定之后：

- 自然语言 Warframe 助手；
- Market 智能比价和价格趋势；
- 根据目标物品生成刷取路径；
- 配装和 Build 推荐；
- Wiki/RAG 问答；
- 用户偏好和个性化菜单；
- 主动情报摘要；
- 多数据源自动切换；
- 插件市场或外部插件加载机制；
- 多平台适配，例如 QQ 之外的聊天平台。

## 9. 当前优先级明确的改进项

| 优先级 | 改进项 | 原因 |
| --- | --- | --- |
| P0 | 增加 OneBot 事件入口 | 没有入口，插件体系无法运行 |
| P0 | 实现至少一个插件和帮助菜单 | 验证插件 SPI、分发和发送闭环 |
| P0 | 完成世界状态解析 | 轮询结果当前没有业务价值 |
| P0 | 接通变化事件和通知发送 | `ChangeDetector` 和 `MessageBuilder` 当前没有实际效果 |
| P0 | 统一外部 API 错误模型 | 避免 `null` 引发空指针和错误提示不清 |
| 已完成 | 修复 TLS 默认配置 | 默认校验证书，仅保留显式临时排查开关 |
| 已完成 | 统一分页、业务断言和写事务边界 | 减少管理模块重复 DTO 和异常/事务样板代码 |
| 已完成 | 约束通用线程池和插件注册 | 避免无界并发、重复命令和单插件异常扩大 |
| 已完成 | 将命令元数据集中化 | `CommandDescriptor`、启动期 `CommandRegistry`、精确索引和自定义匹配回退已落地 |
| P1 | 重新设计订阅关系表 | 兼容 NyxBot 的用户级规则过滤 |
| P1 | 增加通知幂等和失败状态 | 防止重复推送并支持恢复 |
| P1 | 增加数据缓存和动态轮询 | 降低外部 API 不稳定和限流影响 |
| P2 | 完善管理端 | 支撑长期运行和数据维护 |
| P2 | 增加渲染回归测试 | 防止图片输出分页、字体和资源问题 |

## 10. 单机阶段的边界

单机阶段应优先保证可维护性和可恢复性，而不是提前为集群设计所有组件。

建议当前使用：

- PostgreSQL 单实例，H2 仅用于自动化测试；
- cache2k 本地业务缓存，具体选择和迁移任务见
  `docs/Warframe外部数据源、代理与缓存演进任务.md`；
- Spring Scheduling；
- Java 21 + Spring 有界线程池；
- 数据库唯一约束实现通知幂等；
- `synchronized`、本地锁或单线程执行器保证同一数据源不并发刷新；
- 本地文件缓存保存最近一次成功数据；
- Actuator 和日志记录任务状态。

当前已使用显式 `SpringCache2kCacheManager` 作为 JVM 业务缓存实现，并为世界状态、元数据、
翻译和数据源 tag 建立独立的容量与生命周期配置。Caffeine 依赖和 YAML 配置按当前评估要求
暂时保留但不生效，不允许在 Caffeine 上新增业务缓存；业务 loader 和旧值降级仍需随数据模型
实现。

暂不引入：

- Redis；
- MQ；
- Dubbo；
- Nacos；
- 分布式锁；
- 多服务部署；
- 复杂 Outbox 治理。

单机设计仍应保留接口边界，例如 `MessageSender`、`DataProvider`、`NotificationStore` 和 `WorldStateParser`。这样未来替换实现时不会把基础设施选择扩散到业务代码。

## 11. 测试建议

### 11.1 当前必须补充

- `PluginDispatcher`：已覆盖排序、重复命令、匹配异常和处理异常，后续随真实插件补充端到端测试；
- OneBot 事件入口：群消息、私聊消息、空消息、非文本消息；
- 命令别名和参数解析；
- `WorldStateMapper`：完整 JSON、字段缺失、空列表、非法时间；
- `ChangeDetector`：首次加载、增加、删除、过期和重复事件；
- 通知历史唯一约束和重复发送；
- HTTP 客户端：200、429、5xx、网络异常、重试次数；
- 图片渲染：模板、中文字体、长内容和资源加载；
- 权限：普通用户、管理员、超级管理员。

### 11.2 后续补充

- API 失败时使用本地缓存；
- 数据刷新并发控制；
- 群组订阅和用户过滤；
- 机器人被移出群后的订阅清理；
- 管理端 Sa-Token 过期和权限边界；
- 应用重启后通知历史和当前状态恢复；
- Playwright 浏览器生命周期和资源释放。

## 12. 事实依据与文件索引

### NnmBot

- 根构建和模块：`pom.xml`
- 插件接口：`nnm-core/src/main/java/com/nnm/bot/core/plugin/BotPlugin.java`
- 插件分发：`nnm-core/src/main/java/com/nnm/bot/core/plugin/PluginDispatcher.java`
- 执行器：`nnm-core/src/main/java/com/nnm/bot/core/config/ExecutorConfig.java`
- 世界状态模型：`nnm-warframe/src/main/java/com/nnm/bot/warframe/domain/model/WorldState.java`
- 世界状态服务：`nnm-warframe/src/main/java/com/nnm/bot/warframe/application/WorldStateService.java`
- API 客户端：`nnm-warframe/src/main/java/com/nnm/bot/warframe/infrastructure/client/WarframeApiClient.java`
- 轮询任务：`nnm-scheduler/src/main/java/com/nnm/bot/scheduler/task/WorldStatePollingTask.java`
- 数据库迁移：`nnm-app/src/main/resources/db/migration/V1__init_schema.sql`
- 主配置：`nnm-app/src/main/resources/application.yaml`
- 开发配置：`nnm-app/src/main/resources/application-dev.yaml`
- 生产配置：`nnm-app/src/main/resources/application-prod.yaml`

### NyxBot

- 命令常量：`src/main/java/com/nyx/bot/enums/CommandConstants.java`
- 命令和权限：`src/main/java/com/nyx/bot/enums/Codes.java`
- 数据初始化：`src/main/java/com/nyx/bot/data/WarframeDataSource.java`
- 订阅服务：`src/main/java/com/nyx/bot/modules/warframe/application/SubscriptionApplicationService.java`
- 通知服务：`src/main/java/com/nyx/bot/modules/warframe/application/NotificationApplicationService.java`
- 插件实现：`src/main/java/com/nyx/bot/modules/warframe/plugin/`
- 权限实现：`src/main/java/com/nyx/bot/permissions/`
- 管理端控制器：`src/main/java/com/nyx/bot/modules/*/controller/`

### charge-basic

- 根构建和技术版本：`pom.xml`
- 自动配置：`charge-basic-common/src/main/java/com/ydl/charge/basic/common/autoconfigure/`
- 自动配置入口：`charge-basic-common/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
- HTTP 封装：`charge-basic-common/src/main/java/com/ydl/charge/basic/common/util/RestClientUtil.java`
- Outbox：`charge-basic-common/src/main/java/com/ydl/charge/basic/common/mq/producer/outbox/`
- 通用配置属性：`charge-basic-common/src/main/java/com/ydl/charge/basic/common/autoconfigure/properties/`

## 13. 最终建议

NnmBot 当前最需要的不是继续扩张模块数量，而是完成第一个可验证闭环。建议下一轮开发严格按以下顺序推进：

```text
OneBot 消息入口
  -> 帮助插件
  -> 统一消息发送
  -> 世界状态解析
  -> 基础查询插件
  -> 变化检测
  -> 订阅通知
  -> 管理端和数据刷新
```

完成这条链路后，再逐项移植 NyxBot 的市场、遗物、紫卡和高级订阅能力。charge-basic 的配置、异常、HTTP、幂等和模块化思想可以持续吸收，但基础设施应随着实际部署规模演进，避免单机项目过早承担分布式系统的复杂度。
