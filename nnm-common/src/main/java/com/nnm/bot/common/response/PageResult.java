package com.nnm.bot.common.response;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import java.util.List;
import java.util.Objects;

/**
 * 与具体框架无关的分页响应，供各业务模块复用。
 */
public record PageResult<T>(
        @JsonPropertyDescription("记录")
        List<T> records,
        @JsonPropertyDescription("当前页")
        int current,
        @JsonPropertyDescription("每页数量")
        int size,
        @JsonPropertyDescription("总数量")
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
