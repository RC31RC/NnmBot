package com.nnm.bot.core.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("nnm.distributed")
public record DistributedProperties(
        @DefaultValue("true")
        boolean enabled,
        @DefaultValue("2h")
        Duration taskRetention,
        @DefaultValue("30m")
        Duration taskTimeout,
        @DefaultValue("2s")
        Duration taskPollInterval,
        @DefaultValue("30s")
        Duration ownerLease,
        @DefaultValue("10s")
        Duration reconcileInterval,
        @DefaultValue("1d")
        Duration messageDedupRetention,
        @DefaultValue("2d")
        Duration worldStateRetention,
        @DefaultValue("60s")
        Duration discoveryTimeout,
        @DefaultValue("128")
        int maxTaskSubscriptions) {
    public DistributedProperties {
        for (Duration value : new Duration[] {
                taskRetention, taskTimeout, taskPollInterval, ownerLease, reconcileInterval,
                messageDedupRetention, worldStateRetention, discoveryTimeout}) {
            if (value == null || value.isNegative() || value.isZero()) {
                throw new IllegalArgumentException("Distributed durations must be positive");
            }
        }
        if (ownerLease.compareTo(reconcileInterval.multipliedBy(3)) < 0) {
            throw new IllegalArgumentException("owner-lease must be at least 3 reconcile intervals");
        }
        if (maxTaskSubscriptions < 1) throw new IllegalArgumentException("max-task-subscriptions must be positive");
    }
}
