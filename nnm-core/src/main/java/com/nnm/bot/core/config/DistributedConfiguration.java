package com.nnm.bot.core.config;

import io.github.rdt.spring.config.RdtProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "nnm.distributed", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(DistributedProperties.class)
public class DistributedConfiguration {
    public DistributedConfiguration(RdtProperties properties) {
        if (properties.instanceId() == null || properties.instanceId().isBlank()) {
            throw new IllegalArgumentException("rdt.instance-id is required for reliable broadcast recovery");
        }
        if (!properties.enabled() || !properties.queue().enabled() || !properties.schedule().enabled()) {
            throw new IllegalArgumentException("NnmBot distributed mode requires RDT queue and schedule");
        }
    }
}
