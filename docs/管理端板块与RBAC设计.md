# NnmBot 管理端板块与 RBAC 设计

> 更新日期：2026-07-24

## 1. 当前开发形态

管理端采用前后端分离：

```text
/Users/ren/RcGit/
├── NnmBot/             Spring Boot 后端与 Bot 运行时
└── nnm-admin-web/      Vue 3 管理端
```

当前阶段前后端都需要持续开发，不进行生产打包。前端运行在
`http://localhost:3006`，通过 Vite 将 `/api` 代理到
`http://localhost:8080`。

认证鉴权统一使用 Sa-Token。后端负责最终身份与权限校验，前端只负责菜单、路由和按钮的可见性。密码哈希使用 BCrypt；不引入 Spring Security 认证链。

## 2. 当前前端板块

模板当前注册顺序如下：

1. 仪表盘
2. 系统管理
3. 页面模板
4. 组件中心
5. 功能示例
6. 文章管理
7. 结果页面
8. 异常页面
9. 系统安全
10. 帮助与文档

系统管理已经移动到仪表盘之后，包含：

- 用户管理
- 角色管理
- 菜单管理
- 操作审计
- 在线会话
- 个人中心（隐藏路由）
- 模板原有的嵌套菜单示例

文章、组件、示例、结果页等暂时作为前端能力参考保留。接入 NnmBot 业务后，应按实际功能逐步替换，不一次性重构模板目录。

## 3. RBAC 模型

当前后端关系：

```text
sys_user
  -> sys_user_role
  -> sys_role
  -> sys_role_permission -> sys_permission
  -> sys_role_menu       -> sys_menu
```

职责划分：

- 用户：登录主体，可绑定多个角色。
- 角色：权限和菜单的集合，并配置 `ALL` 或 `SELF` 数据范围。
- 权限：保护后端 API 与前端操作按钮。
- 菜单：描述导航、路由组件和显示顺序。

当前数据范围先应用于用户列表：拥有任一 `ALL` 角色时可以查看全部用户，
只有 `SELF` 角色时只能查看自己的用户记录。后续业务表接入数据范围时，应在
对应查询 Service 中按资源归属继续扩展，不在 Controller 中拼接过滤条件。

首批内置角色：

| 编码 | 名称 | 当前范围 |
| --- | --- | --- |
| `R_SUPER` | 超级管理员 | 全部系统菜单和权限 |
| `R_ADMIN` | 管理员 | 仪表盘、用户管理及用户权限 |
| `R_USER` | 只读用户 | 预留，后续随业务菜单配置 |

## 4. 系统管理权限

| 页面 | 权限码 | 用途 |
| --- | --- | --- |
| 用户 | `system:user:view` | 查看用户列表 |
| 用户 | `system:user:create` | 创建用户 |
| 用户 | `system:user:update` | 修改用户 |
| 用户 | `system:user:disable` | 启用或禁用用户 |
| 角色 | `system:role:view` | 查看角色列表 |
| 角色 | `system:role:create` | 创建角色 |
| 角色 | `system:role:update` | 修改角色 |
| 角色 | `system:role:delete` | 删除角色 |
| 角色 | `system:role:assign` | 分配菜单和权限 |
| 菜单 | `system:menu:view` | 查看菜单 |
| 菜单 | `system:menu:create` | 创建菜单 |
| 菜单 | `system:menu:update` | 修改菜单 |
| 菜单 | `system:menu:delete` | 删除菜单 |
| 审计 | `system:audit:view` | 查看操作审计 |
| 会话 | `system:session:view` | 查看在线会话 |
| 会话 | `system:session:kickout` | 强制指定会话下线 |

前端按钮通过 `v-auth` 或 `useAuth().hasAuth()` 检查 `/api/admin/v1/me`
返回的 `buttons`。路由中的 `meta.authList` 用于声明页面权限点，并仅在前端静态模式下作为开发期兜底。

后端接口使用 `@SaCheckPermission` 校验相同权限码，权限列表由 `StpInterface` 从数据库角色关系中加载。不能仅隐藏按钮而不保护 API。

## 5. 已完成接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `POST` | `/api/admin/v1/auth/login` | 用户名密码登录 |
| `POST` | `/api/admin/v1/auth/refresh` | 使用当前会话令牌换取新令牌，并使旧令牌失效 |
| `POST` | `/api/admin/v1/auth/logout` | 注销当前会话并使令牌失效 |
| `GET` | `/api/admin/v1/me` | 当前用户、角色和按钮权限 |
| `GET` | `/api/admin/v1/users` | 用户分页查询 |
| `POST` | `/api/admin/v1/users` | 创建用户并分配角色 |
| `PUT` | `/api/admin/v1/users/{userId}` | 修改用户资料、密码和角色 |
| `PUT` | `/api/admin/v1/users/{userId}/status` | 启用或禁用用户 |
| `PUT` | `/api/admin/v1/users/{userId}/roles` | 单独调整用户角色 |
| `GET` | `/api/admin/v1/roles` | 角色分页查询 |
| `POST` | `/api/admin/v1/roles` | 创建角色 |
| `PUT` | `/api/admin/v1/roles/{roleId}` | 修改角色和数据范围 |
| `DELETE` | `/api/admin/v1/roles/{roleId}` | 删除非内置角色 |
| `GET` | `/api/admin/v1/roles/{roleId}/grants` | 查询角色菜单与按钮授权 |
| `PUT` | `/api/admin/v1/roles/{roleId}/grants` | 保存角色菜单与按钮授权 |
| `GET` | `/api/admin/v1/permissions` | 权限点查询 |
| `GET` | `/api/admin/v1/menus` | 当前用户菜单查询 |
| `GET` | `/api/admin/v1/menus/all` | 菜单管理使用的完整菜单树 |
| `POST` | `/api/admin/v1/menus` | 创建菜单 |
| `PUT` | `/api/admin/v1/menus/{menuId}` | 修改菜单 |
| `DELETE` | `/api/admin/v1/menus/{menuId}` | 删除菜单 |
| `POST` | `/api/admin/v1/permissions` | 创建按钮权限 |
| `PUT` | `/api/admin/v1/permissions/{permissionId}` | 修改按钮权限 |
| `DELETE` | `/api/admin/v1/permissions/{permissionId}` | 删除按钮权限 |
| `GET` | `/api/admin/v1/operation-logs` | 分页查询操作审计 |
| `GET` | `/api/admin/v1/sessions` | 查询在线会话 |
| `DELETE` | `/api/admin/v1/sessions/{token}` | 强制指定会话下线 |

用户、角色、菜单和按钮权限的管理页面已改为调用上述接口，不再使用模板
Mock 数据。角色授权树将菜单和按钮权限分开勾选，避免选中菜单时隐式授予
页面内的全部操作。

当前刷新接口会创建新的 Sa-Token 会话令牌并注销旧令牌，但响应中的
`token` 与 `refreshToken` 仍指向同一个新令牌；需要独立刷新令牌时再设计
双令牌模型。

Sa-Token 目前使用默认内存 DAO，适合当前单机开发和运行。若后续部署多个
后端实例，必须接入 Redis 等共享 DAO，否则不同实例无法共享登录状态、在线
会话和强制下线结果。

## 6. 后续业务菜单

在现有系统管理基础上，建议按真实业务逐步增加：

- Bot 管理：实例、群组、命令插件、黑白名单、消息测试。
- Warframe 数据：世界状态、市场、遗物、紫卡、翻译别名、数据源状态。
- 订阅通知：订阅目标、订阅规则、通知历史、失败重试。
- 任务调度：轮询任务、数据刷新、执行记录、失败详情。
- 运维监控：健康状态、JVM 指标、外部 API、缓存和日志。

新增菜单时同时定义页面路由、后端权限码、`@SaCheckPermission` 和前端按钮权限。权限码发布后保持稳定，不随页面文案变化。

## 7. 开发约束

- 前端沿用 Art Design Pro 的 Vue 3、TypeScript、Element Plus 和现有视觉风格。
- 页面以精简、清晰、可操作为准，避免重复组件和无业务价值的抽象。
- 后端保持薄 Controller、Service 分层、DTO/实体分离和构造注入。
- 简单 DTO 映射直接构造，复杂且重复的映射再使用 MapStruct。
- 当前所有服务地址使用 localhost；临近部署时再引入环境地址和密钥。
- 开发验证运行测试、类型检查和开发服务器，不执行生产打包。
- 当前开发阶段不提交由 Art Design Pro 原仓库带来的部署地址和 Git 历史。
