# QQ 双通道本地接入

## 1. 组件关系

NnmBot 当前同时支持两条独立接入链路。

```text
个人 QQ
  -> SnowLuma / OneBot v11 实现端
  -> 正向 WebSocket 服务端
  -> NnmBot WebSocket 客户端
  -> Shiro SDK
  -> OneBot 适配器
  -> 平台无关消息上下文
  -> BotPlugin
  -> 统一消息发送器
```

```text
QQ 官方机器人
  -> QQ Open Platform Gateway
  -> QQ 官方适配器
  -> 平台无关消息上下文
  -> BotPlugin
  -> 统一消息发送器
  -> QQ Open Platform HTTP API
```

两条链路可以同时启用。插件不依赖 Shiro 或 QQ 官方事件类型，回复由
`BotMessageSenderRouter` 根据消息来源中的 `BotPlatform + botAccountId`
自动发回原渠道实例，多个同类型账号不会共用路由键。

## 2. SnowLuma / OneBot v11

NnmBot 复用 Shiro 的 OneBot 事件解析能力，并由项目自己的连接管理器建立正向
WebSocket。项目按“一条渠道实例记录对应一个 QQ 账号和一个 WebSocket 地址”管理连接，
并使用事件的 `self_id` 校验实际账号，例如：

```text
ws://127.0.0.1:3001
```

在 SnowLuma 中启用正向 WebSocket 服务端。本机联调监听
`127.0.0.1:3001`；SnowLuma 与 NnmBot 不在同一台机器时，监听
`0.0.0.0:3001`，并通过防火墙限制来源地址。

连接实例统一在管理端的“机器人管理”页面创建。先填写 WebSocket 地址和 Token，点击
“获取账号”；后端会建立一次短连接并调用 OneBot v11 标准动作 `get_login_info`，成功后把
响应中的 `data.user_id` 回填为机器人 QQ 号。保存后配置进入数据库，重启 NnmBot 后由正式
连接管理器加载。

`access-token` 填 SnowLuma 中配置的原始 Token，不要手动添加
`Bearer ` 前缀，NnmBot 会自动生成 `Authorization: Bearer <token>`。
SnowLuma 默认会生成 Token，可在 SnowLuma 运行目录的
`config/onebot_<QQ号>.json` 中查看，账号单独配置存在时以
`config/account_<QQ号>.json` 的 `access_token` 为准。SnowLuma 未启用
Token 时可以将 `access-token` 留空，但无令牌服务不得暴露到公网。

连接成功后日志至少包含：

```text
Account <QQ号> connected
OneBot connected, botId=<QQ号>
```

`HTTP 401` 表示地址和端口已经连通，但 SnowLuma Token 缺失或不一致，
不是防火墙或监听地址问题；
`Connection refused` 表示目标端口没有监听或被防火墙拒绝。
`runtime.reconnect-interval` 控制每个实例的重连间隔。

保存的机器人 QQ 号必须与后续 OneBot 事件中的 `self_id` 一致，否则项目会拒绝该连接，
防止消息被路由到错误账号。接入第二个 QQ 时在管理端新增实例；即使两个协议端恰好使用
相同地址，NnmBot 也会按两个独立实例建立连接。

正向 WS 不需要等待业务消息才能识别账号，`get_login_info` 就是标准查询方式。反向 WS
通常也能从握手头 `X-Self-ID` 或首个事件的 `self_id` 识别账号，但反向模式还需要服务端
鉴权、连接归属和多节点粘性，不应只为账号发现而引入。

NnmBot 使用的是标准 OneBot v11 边界，SnowLuma 不要求修改项目内的
`OneBotMessageContextFactory` 或插件接口。以后切换其他兼容实现端时，业务插件同样无需改动。

## 3. QQ 官方机器人

QQ 官方适配器默认关闭。先在 QQ 开放平台取得机器人 `AppID` 和 `ClientSecret`，再在
“机器人管理”页面选择“QQ 官方机器人”创建实例。保存后重启 NnmBot，启用的实例会自动
获取 Token 并连接 Gateway。

启用后 NnmBot 会：

1. 从 QQ 官方 Token API 获取并提前刷新 `access_token`；
2. 获取 Gateway 地址并建立 WebSocket；
3. 处理 `HELLO`、`IDENTIFY`、`RESUME`、心跳和重连；
4. 接收 C2C、群聊、频道和频道私信消息；
5. 通过官方 HTTP API 将插件回复发送回原会话。

当前文字回复支持 C2C、群聊、频道和频道私信。图片上传与发送先支持
C2C 和群聊；频道图片需要按官方频道富媒体能力单独扩展，当前会返回明确的不支持异常。

`token-url` 和 `api-base-url` 是 QQ 官方外部服务，不应改成 `localhost`。
本项目自身的后端、前端和 OneBot WebSocket 在开发阶段仍统一使用本机地址。

## 4. 与 AstrBot 共存

先在 AstrBot 的机器人配置中确认当前 QQ 连接类型：

- 如果是 `OneBot v11`，通常已经存在 NapCat 等协议实现端；
- 如果是 QQ 官方机器人，AstrBot 没有为 NnmBot 提供 OneBot v11 协议连接，
  NnmBot 仍需要单独的 OneBot v11 实现端。

AstrBot 原有连接可以继续保留，但两个业务程序可能同时收到同一账号或机器人的消息。
开发期间应避免在两边配置相同命令，否则可能产生重复回复。更稳妥的方式是：

1. 给 NnmBot 使用单独的测试 QQ；
2. 或暂时停用 AstrBot 中与 NnmBot 重复的命令插件；
3. 或只在测试群中启用 NnmBot。

## 5. 当前内置命令

连接成功后可以在群聊或私聊发送：

```text
帮助
状态
```

`帮助` 根据当前加载的项目插件生成菜单；`状态` 返回机器人账号、会话类型、
运行时长、JVM、内存和插件数量。

## 6. 配置与资源边界

- 实例的 `enabled=false` 时不建立连接；
- `enabled=true` 时启动阶段校验该协议要求的地址和凭据；
- OneBot Token 与 QQ Client Secret 使用 AES-GCM 加密后落库，列表和详情接口只返回
  “已配置”标记，不返回明文；
- 本地可使用开发密钥，正式部署必须通过 `NNM_BOT_CREDENTIAL_KEY` 提供独立密钥；
- Gateway 回调、消息分发、心跳和重连复用项目已有有界执行器；
- 当前不引入 WebClient、额外 SDK 线程池或第二套插件体系。

连接信息按职责拆成三张表：

- `nnmbot.bot_channel_instance`：公共身份、协议、显示名、账号和启用状态；
- `nnmbot.bot_onebot_config`：正向 WS 地址、Token 和重连间隔；
- `nnmbot.bot_qq_official_config`：AppID、Client Secret、官方 API/Gateway 与运行参数。

`application-bot.yml` 不再保存任何机器人实例、账号或协议连接参数；凭据加密主密钥仅是
进程启动必需的基础设施密钥，不属于机器人实例配置。没有数据库实例时所有机器人渠道保持关闭。当前实例配置在应用
启动时加载，因此新增、修改、启停或删除后需要重启后端才影响正式连接。

连接状态、最后错误和能力集合属于实时状态，由 `BotChannelRegistry` 保存在内存中，
不会回写配置表。管理端列表会合并数据库配置和注册表快照；尚未被当前进程加载的配置显示为
“待重启”。

OneBot 与 QQ 官方事件的原始字段并不相同。OneBot 一般使用 QQ 号、群号并可调用群成员
列表接口；QQ 官方机器人主要使用 OpenID，能读取的成员信息取决于开放平台事件、权限和
API 范围。业务功能应检查 `BotChannelCapability`，不能假设所有渠道都能枚举群成员。

## 7. 插件机制

当前 NnmBot 的 `BotPlugin` 是项目内部 SPI：

- 插件实现为 Spring Bean；
- `PluginDispatcher` 自动收集、排序和校验命令唯一性；
- 插件只接触 `BotMessageContext` 和统一发送器；
- OneBot/Shiro 与 QQ 官方事件对象都被限制在各自适配层。

Shiro SDK 还支持基于 `ServiceLoader` 和 `META-INF/services` 加载外部插件 JAR。
NnmBot 当前不启用外部插件目录作为主要扩展方式。等内部插件接口稳定后，再决定
是否提供独立插件 SDK 和外部 JAR 兼容层。
