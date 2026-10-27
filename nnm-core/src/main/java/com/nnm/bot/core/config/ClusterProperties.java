package com.nnm.bot.core.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("nnm.cluster")
public record ClusterProperties(
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
        @DefaultValue("60s")
        Duration discoveryTimeout,
        @DefaultValue("120s")
        Duration componentFailureThreshold,
        @DefaultValue("30s")
        Duration componentStartupGrace,
        @DefaultValue("2m")
        Duration componentFailoverBackoff,
        @DefaultValue("128")
        int maxTaskSubscriptions) {
    public ClusterProperties {
        for (Duration value : new Duration[] {
                taskRetention, taskTimeout, taskPollInterval, ownerLease, reconcileInterval,
                messageDedupRetention, discoveryTimeout,
                componentFailureThreshold, componentStartupGrace, componentFailoverBackoff}) {
            if (value == null || value.isNegative() || value.isZero()) {
                throw new IllegalArgumentException("Cluster durations must be positive");
            }
        }
        if (ownerLease.compareTo(reconcileInterval.multipliedBy(3)) < 0) {
            throw new IllegalArgumentException("owner-lease must be at least 3 reconcile intervals");
        }
        if (componentFailureThreshold.compareTo(componentStartupGrace) < 0) {
            throw new IllegalArgumentException(
                    "component-failure-threshold must cover component-startup-grace");
        }
        if (maxTaskSubscriptions < 1) {
            throw new IllegalArgumentException("max-task-subscriptions must be positive");
        }
    }

}
