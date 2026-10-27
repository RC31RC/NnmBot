# 管理 API 与前端合同

后端接口前缀 `/api/admin/v1`；前端仓库 `nnm-admin-web`，本地地址为 `http://localhost:3006`，请求本地后端 8080。HTTP 参数由 Controller 接收并声明权限；业务由接口及 `impl` 中的 Service 执行。

## 数据约定

| 用途 | Java 目录/后缀 | HTTP 约定 |
| --- | --- | --- |
| 查询条件 | `pojo/query`、`Query` | 分页/筛选通过明确查询对象接收 |
| 创建、修改和登录输入 | `pojo/dto`、`DTO` | 校验注解保留，增改可复用 |
| Repository 和服务传输 | `pojo/dto`、`DTO` | 不返回受管实体 |
| 前端响应 | `pojo/vo`、`VO` | Controller assembler 转换；字段合同与前端类型对应 |
| 分页 | `nnm-common` 的 `PageResult` | `{records,current,size,total}` |

归包和 Java 后缀不改变 JSON 属性名。状态码及错误模型使用 common 约定。HTTP JSON 统一使用配置的 Jackson 2 converter，与类型化 DTO 及 ISO 日期合同一致，不能用测试专用 converter 掩盖启动配置。

## 入口与权限

| 入口 | 实现位置/职责 |
| --- | --- |
| 登录、续期、退出、当前用户 | Admin `AuthController`；Sa-Token 和 BCrypt |
| 用户、角色、菜单、权限 | Admin RBAC Controller；`system:*:*` 权限码与等级约束 |
| 渠道实例、状态 SSE | Admin bot/channel；独立启停及只读状态 |
| 命令、模板、关联组 | Admin bot/command；展示策略、模板和关联参数验证 |
| 逻辑群、成员、通知 | Admin bot/group 和 notification；主动发现、确认绑定、异步发送 |
| 官方静态与自维护资料 | Warframe official Controller；版本、预览、采纳、记录维护 |
| 社区人工资料 | Warframe community Controller；查询、预览及选择采纳 |
| 论坛 | Warframe forum Controller；文章、审核和附件 |
| 后台任务查询与事件 | Admin `AdminTaskController`；任务所有权和访问策略 |

精确 URI、方法和输入字段以 Controller 及 OpenAPI 为准；文档不复制第二份会漂移的完整接口清单。角色编码使用 `R_`，权限码使用 `域:资源:动作`。前端 `v-auth`、`v-roles` 只改善交互，后端 `@SaCheckPermission` 是权限边界；按钮以 `/me` 返回的 `buttons` 为准。

## 登录与 SSE

续期继续使用当前 Sa-Token，不建立第二套 refresh token。当前绝对会话有效期为配置中的 7200 秒；前端遇到 401 时的续期流程不能复活已绝对过期的 token。

任务 SSE 先校验访问权限，页面初始结果和后续事件使用同一状态合同。实例状态广播在每个 JVM 合并采集，再分发给各页面；连接超时、错误和主动关闭必须幂等清理订阅及心跳。跨线程身份显式传递，不通过 ThreadLocal 延续请求身份。

## 展示和运行配置

展示策略支持按平台/会话覆盖、组合消息与交互策略；关联组支持目标命令声明的类型化参数。通知预览与实际执行复用执行/输出规划，详情见 [模板约定](命令模板数据约定.md)。资源输入使用已上传、受控的资源 ID，不把任意外链当下载任务。

前端原有页面、菜单和功能示例继续展示。验证使用 `vue-tsc` 和改动文件 lint；接口 JSON 的值、枚举、日期与嵌套分页由实际 MVC 合同测试核对，不只比较 Java 类名。
