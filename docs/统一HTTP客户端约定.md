# 统一 HTTP 客户端约定

> 本文记录 NnmBot 当前统一 HTTP 客户端的使用边界，以及对
> `ydl-foundation` 中 `YdlRestClientManager` 的借鉴结果。

## 1. 当前实现

通用 HTTP 基础设施位于 `nnm-common`：

- `RestClientConfig` 创建全局 `RestClient` Bean；
- `HttpClientProperties` 统一管理连接超时、读取超时、重试、慢请求日志和 TLS；
- `HttpClient` 负责类型化 GET、JSON POST 和旧 `HttpResult` 兼容接口；
- `ExternalHttpException` 表示收到非 2xx HTTP 响应；
- `ExternalTimeoutException` 表示连接或读取超时；
- `ExternalTransportException` 表示没有收到有效 HTTP 响应的传输故障；
- `ExternalPayloadException` 表示请求序列化或响应反序列化失败。

业务模块只注入 `com.nnm.bot.common.http.HttpClient`，不直接创建或注入
`RestClient`。`RestClient` 只属于 common 的基础设施边界。

## 2. 从 YdlRestClientManager 借鉴的内容

`YdlRestClientManager` 中适合本项目的设计已经落地：

1. 统一 Bean 管理和构造注入，避免各业务类重复创建客户端；
2. 显式使用项目的 Jackson `ObjectMapper`，保证日期、命名策略和模块配置一致；
3. 类型化响应和 `ParameterizedTypeReference` 泛型响应；
4. 统一请求耗时日志、慢请求识别和 URL 脱敏；
5. 统一外部 HTTP 异常分类；
6. 请求参数、超时和日志开关集中在 `@ConfigurationProperties`。

本项目没有直接复制 `YdlRestClientManager`，原因是当前业务规模还不需要维护
多个超时 profile，也没有将重试、熔断和幂等策略强行塞入所有外部调用的必要。

## 3. 请求策略

### GET

类型化 GET 默认对以下故障进行有限次数重试：

- 网络传输异常；
- 连接或读取超时；
- HTTP 429；
- HTTP 5xx。

重试次数和指数退避由 `nnm.http.*` 配置控制。GET 适合读取世界状态、静态数据和
经过明确设计的查询请求。

### POST

JSON POST 默认不自动重试。POST 可能产生发送消息、上传媒体或创建资源等副作用，
是否重试必须由具体业务根据幂等键、服务端语义和响应状态自行决定。

旧的 `post(String, String, Map)` 接口保留，用于逐步迁移已有调用方。新代码优先使用
`postJson`。

## 4. 返回值选择

简单对象使用 `Class<T>`：

```java
UserResponse response = httpClient.get(url, UserResponse.class);
```

列表、分页或嵌套泛型使用 `ParameterizedTypeReference<T>`：

```java
List<UserResponse> response = httpClient.get(
        url,
        Map.of(),
        new ParameterizedTypeReference<>() {});
```

外部 JSON 结构不稳定时，可以暂时使用 `JsonNode`，但应在 parser 边界尽快转换成
NnmBot 自己的 DTO 或领域模型，不能让原始 JSON 结构扩散到业务层。

## 5. 日志与敏感信息

默认只记录方法、去除 query/fragment 的 URL、状态码和耗时。请求与响应 payload
默认关闭，临时排查时也会按 `max-payload-log-length` 截断。

以下内容禁止写入日志：

- `access_token`、`client_secret`、代理用户名和密码；
- 完整授权请求 URL；
- 不必要的完整外部响应；
- 用户消息、权限数据和其他业务敏感数据。

`nnm.http.trust-all-certs` 默认必须为 `false`。只有定位本地代理证书链问题时才可临时
开启，问题确认后应立即关闭。

## 6. QQ 与 Warframe 的使用边界

- QQ 官方 Token、QQ 官方 API、Warframe 世界状态和 Market 请求统一使用 `HttpClient`；
- QQ 官方 Gateway 是 WebSocket 长连接，继续使用 Java `HttpClient` 的 WebSocket API，
  不改成同步 REST 调用；
- Market 数据可能随时变化，默认每次业务查询都请求上游；
- 未来可以在具体业务层增加短时缓存、single-flight、代理 profile 或限流，但不能
  通过修改 common 客户端给所有调用统一增加 Market 缓存；
- 代理配置属于外部数据源配置，应该按 Warframe/QQ 等系统分别选择，不提供开放代理接口。

## 7. 后续任务

- [ ] 为外部系统增加显式 HTTP 代理配置和认证脱敏；
- [ ] 在确有短超时场景时增加命名客户端 profile；
- [ ] 为代理、超时和外部服务错误增加集成测试；
- [ ] 为 Market 查询增加业务层 single-flight 和可配置的极短缓存策略；
- [ ] 为可重试 POST 设计显式幂等键后再开放重试；
- [ ] 增加外部请求指标，例如成功率、耗时分布、重试次数和最后失败原因。
