package com.nnm.bot.core.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * 分布式任务、实例认领、故障切换和发现流程的时间预算。
 *
 * @param taskRetention 任务结果保留时长
 * @param taskTimeout 单项任务执行超时
 * @param taskPollInterval 任务状态轮询间隔
 * @param ownerLease 实例所有权租约时长，至少覆盖三次协调周期
 * @param reconcileInterval 集群实例协调间隔
 * @param messageDedupRetention 消息去重记录保留时长
 * @param discoveryTimeout 渠道发现超时
 * @param componentFailureThreshold 组件判定故障前的等待时长
 * @param componentStartupGrace 组件启动宽限时长
 * @param componentFailoverBackoff 故障切换重试间隔
 * @param maxTaskSubscriptions 单个任务允许的最大订阅数
 */
@ConfigurationProperties("nnm.cluster")
public record ClusterProperties(@DefaultValue("2h") Duration taskRetention, @DefaultValue("30m") Duration taskTimeout,
    @DefaultValue("2s") Duration taskPollInterval, @DefaultValue("30s") Duration ownerLease,
    @DefaultValue("10s") Duration reconcileInterval, @DefaultValue("1d") Duration messageDedupRetention,
    @DefaultValue("60s") Duration discoveryTimeout, @DefaultValue("120s") Duration componentFailureThreshold,
    @DefaultValue("30s") Duration componentStartupGrace, @DefaultValue("2m") Duration componentFailoverBackoff,
    @DefaultValue("128") int maxTaskSubscriptions) {
    public ClusterProperties {
        for (Duration value : new Duration[] {taskRetention, taskTimeout, taskPollInterval, ownerLease,
            reconcileInterval, messageDedupRetention, discoveryTimeout, componentFailureThreshold,
            componentStartupGrace, componentFailoverBackoff}) {
            if (value == null || value.isNegative() || value.isZero()) {
                throw new IllegalArgumentException("Cluster durations must be positive");
            }
        }
        if (ownerLease.compareTo(reconcileInterval.multipliedBy(3)) < 0) {
            throw new IllegalArgumentException("owner-lease must be at least 3 reconcile intervals");
        }
        if (componentFailureThreshold.compareTo(componentStartupGrace) < 0) {
            throw new IllegalArgumentException("component-failure-threshold must cover component-startup-grace");
        }
        if (maxTaskSubscriptions < 1) {
            throw new IllegalArgumentException("max-task-subscriptions must be positive");
        }
    }

}
