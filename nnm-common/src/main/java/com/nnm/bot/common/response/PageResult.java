package com.nnm.bot.common.response;

import java.util.List;
import java.util.Objects;

/**
 * Framework-neutral paged response shared by application modules.
 */
public record PageResult<T>(
        List<T> records,
        int current,
        int size,
        long total
) {

    public PageResult {
        records = List.copyOf(Objects.requireNonNull(records, "records must not be null"));
        if (current < 1) {
            throw new IllegalArgumentException("current must be greater than zero");
        }
        if (size < 0) {
            throw new IllegalArgumentException("size must not be negative");
        }
        if (total < 0) {
            throw new IllegalArgumentException("total must not be negative");
        }
    }

    public static <T> PageResult<T> of(
            List<T> records, int current, int size, long total) {
        return new PageResult<>(records, current, size, total);
    }
}
