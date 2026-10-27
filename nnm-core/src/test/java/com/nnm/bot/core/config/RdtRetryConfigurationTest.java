package com.nnm.bot.core.config;

import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.common.http.ExternalTimeoutException;
import io.github.rdt.exception.RdtRedisException;
import io.github.rdt.exception.RdtErrorCode;
import io.github.rdt.support.RetryClassifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RdtRetryConfigurationTest {
    private final RetryClassifier classifier = new RdtRetryConfiguration().rdtRetryClassifier();

    @Test
    void onlyApplicationSystemFailuresAreRetryable() {
        assertTrue(classifier.isRetryable(new SystemException("temporary failure")));
        assertTrue(classifier.isRetryable(new ExternalTimeoutException("GET", "http://localhost", null)));
        assertFalse(classifier.isRetryable(new BusinessException(CommonErrorCode.PARAM_INVALID)));
        assertFalse(classifier.isRetryable(new IllegalArgumentException("bad command")));
        assertFalse(classifier.isRetryable(new RdtRedisException(RdtErrorCode.REDIS_TIMEOUT, "library failure")));
    }

    @Test
    void causesNeverOverrideTheDeclaredFailureCategory() {
        SystemException system = new SystemException("temporary failure");
        assertFalse(classifier.isRetryable(new BusinessException(CommonErrorCode.DATA_CONFLICT, "rejected", system)));
        assertFalse(classifier.isRetryable(new IllegalStateException(system)));
    }
}
