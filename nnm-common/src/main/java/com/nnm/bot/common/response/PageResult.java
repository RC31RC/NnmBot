package com.nnm.bot.common.response;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import java.util.List;
import java.util.Objects;

/**
 * 与具体框架无关的分页响应，供各业务模块复用。
 *
 * @param <T> 当前页记录类型
 */
public record PageResult<T>(@JsonPropertyDescription("记录") List<T> records, @JsonPropertyDescription("当前页") int current,
    @JsonPropertyDescription("每页数量") int size, @JsonPropertyDescription("总数量") long total) {

    /** 复制记录列表并校验分页元数据。
     *
     * @param records 当前页记录
     * @param current 从 1 开始的页码
     * @param size 每页记录数
     * @param total 全量记录数
     * @throws IllegalArgumentException 页码小于 1，或数量为负数
     */
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

    /** 创建不可变分页结果。
     *
     * @param records 当前页记录
     * @param current 从 1 开始的页码
     * @param size 每页记录数
     * @param total 全量记录数
     * @param <T> 记录类型
     * @return 分页结果
     */
    public static <T> PageResult<T> of(List<T> records, int current, int size, long total) {
        return new PageResult<>(records, current, size, total);
    }
}
