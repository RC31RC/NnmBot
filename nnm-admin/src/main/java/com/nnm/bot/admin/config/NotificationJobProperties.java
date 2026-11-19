package com.nnm.bot.admin.config;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** 管理端预约通知的派发宽限、过期和历史保留策略。 */
@Getter
@Setter
@Component
@ConfigurationProperties("nnm.bot.notifications.jobs")
public class NotificationJobProperties {
    /** 调度时间到达后允许队列延迟派发的最长宽限。 */
    private Duration dispatchGrace = Duration.ofMinutes(10);
    /** 标记长时间没有终态的任务为过期所需等待时长。 */
    private Duration staleTimeout = Duration.ofMinutes(10);
    /** 历史任务记录的保留时长。 */
    private Duration historyRetention = Duration.ofDays(7);
    /** 单个用户可同时拥有的待处理预约上限。 */
    private int maxPendingPerUser = 20;

    /** 校验任务保留时间及单用户积压上限。
     *
     * @throws IllegalArgumentException 生命周期配置超出允许范围
     */
    @PostConstruct
    public void validate() {
        if (dispatchGrace == null || dispatchGrace.toSeconds() < 1 || dispatchGrace.compareTo(Duration.ofDays(1)) > 0
            || staleTimeout == null || staleTimeout.toMinutes() < 2 || staleTimeout.compareTo(Duration.ofDays(1)) > 0
            || historyRetention == null || historyRetention.toDays() < 1 || historyRetention.toDays() > 90
            || maxPendingPerUser < 1 || maxPendingPerUser > 200) {
            throw new IllegalArgumentException("Invalid notification job lifecycle configuration");
        }
    }
}
