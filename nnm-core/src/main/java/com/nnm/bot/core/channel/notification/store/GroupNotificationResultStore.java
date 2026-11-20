package com.nnm.bot.core.channel.notification.store;

import com.nnm.bot.core.channel.notification.constant.NotificationConstants.GroupResultStatus;

import com.nnm.bot.core.channel.notification.GroupNotificationResult;
import com.nnm.bot.core.config.BotNotificationProperties;
import io.github.rdt.state.SharedStateStore;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/** 使用共享状态存储通知结果，并按配置保留有限时间。 */
@Component
public class GroupNotificationResultStore {
    /** 保存可跨节点读取的通知终态。 */
    @Resource
    private SharedStateStore states;
    /** 提供通知结果保留时长。 */
    @Resource
    private BotNotificationProperties properties;

    /**
     * 查询单个通知结果。
     *
     * @param id 通知任务标识
     * @return 已保存结果；不存在或仍在执行时为空
     */
    public Optional<GroupNotificationResult> find(UUID id) {
        return states.get(key(id), GroupNotificationResult.class);
    }

    /**
     * 批量查询终态结果，过滤尚未完成的记录。
     *
     * @param ids 通知任务标识
     * @return 已完成任务的结果映射
     */
    public Map<UUID, GroupNotificationResult> findAll(Collection<UUID> ids) {
        Map<String, GroupNotificationResult> values =
            states.getAll(ids.stream().map(this::key).toList(), GroupNotificationResult.class);
        return ids.stream().filter(id -> values.containsKey(key(id)))
            .filter(id -> values.get(key(id)).status() != GroupResultStatus.IN_PROGRESS)
            .collect(Collectors.toMap(id -> id, id -> values.get(key(id))));
    }

    /**
     * 按配置的保留时长保存通知结果。
     *
     * @param id 通知任务标识
     * @param result 通知结果快照
     */
    public void save(UUID id, GroupNotificationResult result) {
        states.put(key(id), result, properties.retention());
    }

    private String key(UUID id) {
        return "group-notification-result:" + id;
    }
}
