package com.nnm.bot.warframe.persistent.curated.pojo.vo.watch;

import java.time.LocalDateTime;

/** 成员订阅管理视图，不向管理端暴露发送幂等游标。 */
public record WarframeMemberSubscriptionVO(
        Long id, String watchType, String watchKey, String watchValue,
        boolean enabled, boolean notifyEnabled, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
