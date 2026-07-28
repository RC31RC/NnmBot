# QQ 与 OneBot 本地接入

## 1. 组件关系

NnmBot 是机器人业务程序，不负责直接登录 QQ。完整链路为：

```text
QQ
  -> OneBot v11 协议实现端（例如 NapCat）
  -> 反向 WebSocket
  -> Shiro SDK
  -> NnmBot 消息入口
  -> BotPlugin
  -> 统一消息发送器
```

AstrBot 和 NnmBot 都属于 OneBot 的业务服务端。AstrBot 本身不能作为 NnmBot
连接 QQ 的透明代理。如果 AstrBot 当前通过 NapCat 连接 QQ，可以继续使用现有 NapCat，
为 NnmBot 新增一条反向 WebSocket 客户端配置。

## 2. NnmBot 本地地址

NnmBot 默认启用 Shiro 的反向 WebSocket 服务端：

```text
ws://127.0.0.1:8080/ws/shiro
```

可选访问令牌通过环境变量配置：

```bash
export ONEBOT_ACCESS_TOKEN=your-local-token
./scripts/run-dev.sh
```

未配置 `ONEBOT_ACCESS_TOKEN` 时，本地连接不校验令牌。该设置只适合
`127.0.0.1` 本机开发，不应将无令牌的 WebSocket 服务暴露到公网。

## 3. 使用现有 AstrBot 环境

先在 AstrBot 的机器人配置中确认当前 QQ 连接类型：

- 如果是 `OneBot v11`，通常已经存在 NapCat 等协议实现端；
- 如果是 QQ 官方机器人，AstrBot 没有为 NnmBot 提供 OneBot v11 协议连接，
  NnmBot 仍需要单独的 OneBot v11 实现端。

使用现有 NapCat 时，在其 WebUI 的网络配置中新增：

```text
类型：WebSockets 客户端
启用：是
URL：ws://127.0.0.1:8080/ws/shiro
Token：与 ONEBOT_ACCESS_TOKEN 一致
```

AstrBot 原有连接可以继续保留，但两个业务程序会同时收到同一 QQ 账号的消息。
开发期间应避免在两边配置相同命令，否则可能产生重复回复。更稳妥的方式是：

1. 给 NnmBot 使用单独的测试 QQ；
2. 或暂时停用 AstrBot 中与 NnmBot 重复的命令插件；
3. 或只在测试群中启用 NnmBot。

## 4. 当前内置命令

连接成功后可以在群聊或私聊发送：

```text
帮助
状态
```

`帮助` 根据当前加载的项目插件生成菜单；`状态` 返回机器人 QQ、会话类型、
运行时长、JVM、内存和插件数量。

## 5. 插件机制

当前 NnmBot 的 `BotPlugin` 是项目内部 SPI：

- 插件实现为 Spring Bean；
- `PluginDispatcher` 自动收集、排序和校验命令唯一性；
- 插件只接触 `BotMessageContext` 和统一发送器；
- OneBot/Shiro 的事件对象被限制在适配层。

Shiro SDK 还支持基于 `ServiceLoader` 和 `META-INF/services` 加载外部插件 JAR。
NnmBot 当前不启用外部插件目录作为主要扩展方式。等内部插件接口稳定后，再决定
是否提供独立插件 SDK 和外部 JAR 兼容层。
