package com.nnm.bot.common.retry;

import java.time.Duration;
import java.util.Objects;

/**
 * 无状态的重试延迟计算，供外部调用客户端复用。
 */
public final class RetryBackoff {

    private RetryBackoff() {}

    /**
     * 根据失败次数计算指数退避时长，并限制在最大值内。
     *
     * @param initial 首次失败后的等待时长
     * @param maximum 等待上限
     * @param failedAttempt 从 1 开始的连续失败次数
     * @return 当前尝试应等待的时长
     * @throws IllegalArgumentException 时长为负、上限小于初始值或失败次数小于 1
     */
    public static Duration exponential(Duration initial, Duration maximum, int failedAttempt) {
        Objects.requireNonNull(initial, "initial must not be null");
        Objects.requireNonNull(maximum, "maximum must not be null");
        if (initial.isNegative() || maximum.isNegative()) {
            throw new IllegalArgumentException("retry delays must not be negative");
        }
        if (maximum.compareTo(initial) < 0) {
            throw new IllegalArgumentException("maximum must not be shorter than initial");
        }
        if (failedAttempt < 1) {
            throw new IllegalArgumentException("failedAttempt must be at least 1");
        }

        int exponent = Math.min(failedAttempt - 1, Long.SIZE - 2);
        try {
            Duration delay = initial.multipliedBy(1L << exponent);
            return delay.compareTo(maximum) > 0 ? maximum : delay;
        } catch (ArithmeticException ignored) {
            return maximum;
        }
    }
}
