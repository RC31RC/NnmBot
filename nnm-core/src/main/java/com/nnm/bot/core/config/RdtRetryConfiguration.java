package com.nnm.bot.core.config;

import com.nnm.bot.common.exception.SystemException;
import io.github.rdt.support.RetryClassifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** RDT 消费者与定时任务共用的应用异常分类规则。 */
@Configuration(proxyBeanMethods = false)
public class RdtRetryConfiguration {

    @Bean
    public RetryClassifier rdtRetryClassifier() {
        // 不递归检查 cause，避免业务拒绝因底层原因被误判为可重试。
        return failure -> failure instanceof SystemException;
    }
}
