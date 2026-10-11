# RDT 使用规范与事务锁设计说明

## 1. 当前使用规范

NnmBot 对 RDT 的使用分成两类：

| 场景 | 方式 | 原因 |
| --- | --- | --- |
| 集群定时任务 | 类级 `@RdtScheduled` + `RdtScheduledTask` | 一眼看出该类是一个集群任务 |
| 消息消费者 | `@RdtQueueListener` | 一眼看出该类承担消费职责 |
| 同步方法互斥 | `@RdtLock` | 锁边界就是方法调用边界 |
| 分布式 ID | `DistributedIdGenerator` | 分配点需要显式看到序列名和下界 |
| 消息投递 | `MessageQueueTemplate` | 发送时机、模式和载荷需要显式可读 |
| 长连接归属 | `RedisOwnerLeaseService` | 生命周期跨越多个回调和线程 |

类级任务和消费者注解适合描述“这个类是什么”，方法级锁注解适合描述同步互斥边界；
编程式 API 适合描述“现在要分配、发送或锁定什么”。
不要为了形式统一，把所有调用都改成注解。

## 2. `@RdtLock` 的真实语义

```java
@RdtLock
@Transactional
public void update(long orderId) {
    lockManager.lock("order:" + orderId);
    // 查询和更新必须在加锁之后进行；后续可继续添加其他资源锁。
}
```

`lockManager` 是注入的 `DistributedLockManager`。注解没有 key、SpEL 或租约参数，
只建立同步作用域；实际资源由方法内的 `lockManager.lock(key[, options])` 指定。
切面退出时按获取顺序逆序释放锁，获取失败时清理本作用域已获取的锁。
默认 `LockOptions.defaults()` 等待三秒，租约为零，使用 Redisson watchdog；
通过 `LockOptions` 指定的正租约不会自动续期。

同一作用域重复加锁不重复获取，但会校验真实所有权；不允许把已持有的读锁当成写锁，
或在重入时悄悄更换租约。嵌套作用域分别管理自己的获取层级，必须在创建线程按嵌套顺序关闭。
跨线程不会继承作用域，也不能传递 `LockScope` 后在其他线程释放。

如果被锁方法本身就是事务边界，锁切面顺序 `-200` 位于默认事务 advisor 外侧，
锁覆盖该事务提交。如果外层已有事务，锁会绑定到当前事务同步器，方法返回后不立即
释放，而是在最外层事务提交或回滚完成后释放。`REQUIRES_NEW` 会挂起外层事务，
内部事务拥有独立的锁作用域，内部事务完成后再恢复外层作用域。

NnmBot 的 `updateMemberPoints` 不再额外开启外层事务，实际写事务由
`PointAccountServiceImpl.adjust` 创建。锁会跟随该同步事务在提交或回滚后释放。
锁不是数据库约束的替代品，唯一约束、幂等流水仍须保留。

## 3. 事务绑定与生命周期

当前已经支持以下写法：

```java
@RdtLock
@Transactional
public void updateOrder(long orderId) {
    lockManager.lock("order:" + orderId);
    lockManager.lock("account:" + accountId);
}
```

实现由 RDT Starter 中的 `TransactionalLockManager` 完成，核心锁作用域通过事务同步器
延迟实际释放。没有事务时保持原有的方法作用域释放行为。事务绑定只适用于创建锁的同步
线程，不会把锁句柄传播到线程池、WebSocket 回调或异步任务。

## 4. WebSocket 与 ThreadLocal

当前架构不满足“所有 WebSocket 消息都在同一线程”的前提：

- QQ Gateway 收到事件后会提交到 `applicationTaskExecutor`；
- OneBot 使用 Shiro 的异步执行器处理事件；
- JDK WebSocket 的完成回调、重连和心跳也可能来自不同执行线程；
- 线程池满载时的 `CallerRunsPolicy` 也不能作为稳定线程模型。

因此，`ThreadLocal` 只能用于一个线程内、一个同步调用链内的临时状态，并且必须在
`finally` 清理。不能用它保存 WebSocket 会话、机器人 owner、事务、分布式锁或需要跨异步
回调使用的消息上下文。

推荐做法是把 `BotMessageContext`、owner token、平台账号和消息 ID 作为参数或不可变对象
显式传递；需要数据库操作时在实际执行线程创建新的事务边界。
