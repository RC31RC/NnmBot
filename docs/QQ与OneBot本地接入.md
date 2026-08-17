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
`BotMessageSenderRouter` 根据消息来源自动发回原平台。

## 2. SnowLuma / OneBot v11

NnmBot 默认启用 Shiro 的正向 WebSocket 客户端，主动连接 OneBot 实现端：

```text
ws://127.0.0.1:3001
```

在 SnowLuma 中启用正向 WebSocket 服务端。本机联调监听
`127.0.0.1:3001`；SnowLuma 与 NnmBot 不在同一台机器时，监听
`0.0.0.0:3001`，并通过防火墙限制来源地址。

连接地址和访问令牌通过环境变量配置。`scripts/run-dev.sh` 会自动加载仓库
根目录中被 Git 忽略的 `.env`：

```bash
cp .env.example .env
# 编辑 .env 中的 ONEBOT_WS_URL 和 ONEBOT_ACCESS_TOKEN
./scripts/run-dev.sh
```

`ONEBOT_ACCESS_TOKEN` 填 SnowLuma 中配置的原始 Token，不要手动添加
`Bearer ` 前缀，Shiro 会自动生成 `Authorization: Bearer <token>`。
SnowLuma 默认会生成 Token，可在 SnowLuma 运行目录的
`config/onebot_<QQ号>.json` 中查看，账号单独配置存在时以
`config/account_<QQ号>.json` 的 `access_token` 为准。SnowLuma 未启用
Token 时可以省略该变量，但无令牌服务不得暴露到公网。

连接成功后日志至少包含：

```text
Account <QQ号> connected
OneBot connected, botId=<QQ号>
```

`HTTP 401` 表示地址和端口已经连通，但 SnowLuma Token 缺失或不一致，
不是防火墙或监听地址问题；
`Connection refused` 表示目标端口没有监听或被防火墙拒绝。Shiro
`reconnect-interval` 的单位是秒，项目当前每 5 秒重试一次。

NnmBot 使用的是标准 OneBot v11 边界，SnowLuma 不要求修改项目内的
`OneBotMessageContextFactory` 或插件接口。以后切换其他兼容实现端时，业务插件同样无需改动。

## 3. QQ 官方机器人

QQ 官方适配器默认关闭。先在 QQ 开放平台取得机器人 `AppID` 和
`ClientSecret`，再使用环境变量启动：

```bash
export QQ_BOT_ENABLED=true
export QQ_BOT_APP_ID=your-app-id
export QQ_BOT_CLIENT_SECRET=your-client-secret
./scripts/run-dev.sh
```

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

```yaml
nnm:
  bot:
    qq-official:
      enabled: false
      intents: 100663296
      gateway-connect-timeout: 10s
      reconnect-delay: 5s
```

- `enabled=false` 时不建立官方 Gateway 连接，也不要求配置凭据；
- `enabled=true` 时启动阶段校验 `app-id` 与 `client-secret`；
- Token 只保存在 JVM 内存中，不写入数据库、缓存或日志；
- Gateway 回调、消息分发、心跳和重连复用项目已有有界执行器；
- 当前不引入 WebClient、额外 SDK 线程池或第二套插件体系。

## 7. 插件机制

当前 NnmBot 的 `BotPlugin` 是项目内部 SPI：

- 插件实现为 Spring Bean；
- `PluginDispatcher` 自动收集、排序和校验命令唯一性；
- 插件只接触 `BotMessageContext` 和统一发送器；
- OneBot/Shiro 与 QQ 官方事件对象都被限制在各自适配层。

Shiro SDK 还支持基于 `ServiceLoader` 和 `META-INF/services` 加载外部插件 JAR。
NnmBot 当前不启用外部插件目录作为主要扩展方式。等内部插件接口稳定后，再决定
是否提供独立插件 SDK 和外部 JAR 兼容层。
