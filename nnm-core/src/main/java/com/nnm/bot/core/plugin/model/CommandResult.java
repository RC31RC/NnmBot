package com.nnm.bot.core.plugin.model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import cn.hutool.core.util.ObjectUtil;

import cn.hutool.core.collection.CollUtil;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Collection;

/**
 * 插件的稳定返回协议：摘要文字与可直接展示的数据对象彼此独立。
 *
 * @param text 始终以文字发送的摘要，不重复承载订单等明细
 * @param data 已处理的展示 POJO、集合或 Map，所有输出模式使用同一对象
 * @param attachments 需要发送的文件附件
 * @param status 命令业务处理状态
 */
public record CommandResult(String text, Object data, List<CommandAttachment> attachments, CommandResultStatus status) {

    public CommandResult {
        text = ObjectUtil.isNull(text) ? "" : text.trim();
        data = immutableData(data);
        attachments = CollUtil.isEmpty(attachments) ? List.of() : List.copyOf(attachments);
        status = ObjectUtil.defaultIfNull(status, CommandResultStatus.SUCCESS);
    }

    public CommandResult(String text, Object data,
                         List<CommandAttachment> attachments) {
        this(text, data, attachments, CommandResultStatus.SUCCESS);
    }

    public CommandResult(String text, Object data) {
        this(text, data, List.of(), CommandResultStatus.SUCCESS);
    }

    public static CommandResult text(String text) {
        return new CommandResult(text, Map.of(), List.of(), CommandResultStatus.SUCCESS);
    }

    public static CommandResult of(String text, Object data) {
        return new CommandResult(text, data, List.of(), CommandResultStatus.SUCCESS);
    }

    public static CommandResult file(String text, CommandAttachment attachment) {
        return new CommandResult(
                text, Map.of(), List.of(attachment), CommandResultStatus.SUCCESS);
    }

    public static CommandResult withAttachments(String text,
                                                Object data,
                                                List<CommandAttachment> attachments) {
        return new CommandResult(text, data, attachments, CommandResultStatus.SUCCESS);
    }

    /**
     * 返回可发送给用户的业务拒绝结果，例如参数错误或上传内容未通过校验。
     */
    public static CommandResult rejected(String text) {
        return new CommandResult(text, Map.of(), List.of(), CommandResultStatus.REJECTED);
    }

    public boolean successful() {
        return status == CommandResultStatus.SUCCESS;
    }

    public boolean hasText() {
        return !text.isBlank();
    }

    public boolean hasData() {
        if (data == null) return false;
        if (data instanceof Map<?, ?> map) return !map.isEmpty();
        if (data instanceof Collection<?> collection) return !collection.isEmpty();
        if (data instanceof CharSequence text) return !text.isEmpty();
        if (data.getClass().isArray()) return Array.getLength(data) > 0;
        return true;
    }

    public boolean hasAttachments() {
        return !attachments.isEmpty();
    }

    private static Object immutableData(Object source) {
        if (source == null) {
            return Map.of();
        }
        if (source instanceof Map<?, ?> map) {
            Map<String, Object> copy = new LinkedHashMap<>();
            map.forEach((key, value) -> {
                if (!(Objects.requireNonNull(key, "result data key must not be null") instanceof String name)) {
                    throw new IllegalArgumentException("result data key must be a string");
                }
                copy.put(name, value);
            });
            return Collections.unmodifiableMap(copy);
        }
        if (source instanceof Collection<?> collection) {
            return Collections.unmodifiableList(new ArrayList<>(collection));
        }
        // POJO 由命令层提供不可变快照；在此保留类型，不进行反射或 JSON 往返转换。
        return source;
    }
}
