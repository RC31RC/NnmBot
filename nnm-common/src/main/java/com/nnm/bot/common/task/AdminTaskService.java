package com.nnm.bot.common.task;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * 管理端长任务端口。
 *
 * <p>提交接口与进度订阅解耦：业务服务只发布阶段进度，Web 层决定使用 SSE
 * 或普通查询读取状态。实现可以是进程内状态中心，也可以替换为持久化任务系统。</p>
 */
public interface AdminTaskService {

    /**
     * 创建任务并立即返回任务标识，工作负载由实现选择的执行器异步运行。
     */
    AdminTaskSubmission submit(
            String taskType, String requiredPermission, AdminTaskWork work);

    /** 返回任务当前快照，用于首次加载和 SSE 断线后的状态补偿。 */
    Optional<AdminTaskEvent> find(UUID taskId);

    /** 返回读取任务进度所需权限，SSE 与普通查询必须使用同一权限边界。 */
    Optional<String> requiredPermission(UUID taskId);

    /** 查找同类型仍在排队或执行的任务，供业务接口避免重复刷新。 */
    Optional<UUID> findActive(String taskType);

    /**
     * 订阅任务快照。实现应立即发送一次当前状态，关闭订阅后不得继续回调。
     */
    TaskSubscription subscribe(UUID taskId, Consumer<AdminTaskEvent> listener);

    @FunctionalInterface
    interface AdminTaskWork {
        Object execute(TaskProgressReporter reporter) throws Exception;
    }

    /** SSE 连接关闭时用于解除监听器，防止任务状态长期持有 Web 连接对象。 */
    interface TaskSubscription extends AutoCloseable {
        @Override
        void close();
    }
}
