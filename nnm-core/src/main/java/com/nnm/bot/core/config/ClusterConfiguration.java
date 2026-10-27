package com.nnm.bot.core.config;

import com.zaxxer.hikari.HikariPoolMXBean;
import io.github.rdt.spring.config.RdtProperties;
import io.github.rdt.spring.runtime.DatabasePressureProbe;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({ClusterProperties.class, BotNotificationProperties.class})
public class ClusterConfiguration {
    @Bean
    public DatabasePressureProbe rdtDatabasePressure(ObjectProvider<DataSource> sources) {
        return () -> sources.stream().filter(HikariDataSource.class::isInstance)
                .map(HikariDataSource.class::cast).mapToDouble(source -> {
                    HikariPoolMXBean pool = source.getHikariPoolMXBean();
                    return pool == null ? -1 : (double) pool.getActiveConnections() / source.getMaximumPoolSize();
                }).max().orElse(-1);
    }

    public ClusterConfiguration(RdtProperties properties) {
        if (properties.instanceId() == null || properties.instanceId().isBlank()) {
            throw new IllegalArgumentException("rdt.instance-id is required for reliable broadcast recovery");
        }
        if (!properties.enabled() || !properties.queue().enabled() || !properties.schedule().enabled()) {
            throw new IllegalArgumentException("NnmBot requires Redis-backed RDT queue and schedule, including single-instance deployments");
        }
    }
}
