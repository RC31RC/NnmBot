package com.nnm.bot.core.config;

import com.nnm.bot.common.exception.SystemException;
import io.github.rdt.support.RetryClassifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Application exception semantics shared by RDT consumers and scheduled tasks. */
@Configuration(proxyBeanMethods = false)
public class RdtRetryConfiguration {

    @Bean
    public RetryClassifier rdtRetryClassifier() {
        // Do not inspect causes: a business rejection must not become retryable because of its cause.
        return failure -> failure instanceof SystemException;
    }
}
