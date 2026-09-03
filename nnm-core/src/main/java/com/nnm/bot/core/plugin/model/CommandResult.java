package com.nnm.bot.core.plugin.model;

import cn.hutool.core.util.ObjectUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 插件的稳定返回协议：可复制文本和供模板使用的键值对数据彼此独立。
 *
 * @param text 供用户复制或阅读的文字结果
 * @param data 供 HTML 模板使用的键值对数据
 * @param attachments 需要发送的文件附件
 * @param status 命令业务处理状态
 */
public record CommandResult(String text, Map<String, Object> data,
                            List<CommandAttachment> attachments,
                            CommandResultStatus status) {

    public CommandResult {
        text = ObjectUtil.isNull(text) ? "" : text.trim();
        data = immutableData(data);
        attachments = CollUtil.isEmpty(attachments) ? List.of() : List.copyOf(attachments);
        status = ObjectUtil.defaultIfNull(status, CommandResultStatus.SUCCESS);
    }

    public CommandResult(String text, Map<String, Object> data,
                         List<CommandAttachment> attachments) {
        this(text, data, attachments, CommandResultStatus.SUCCESS);
    }

    public CommandResult(String text, Map<String, Object> data) {
        this(text, data, List.of(), CommandResultStatus.SUCCESS);
    }

    public static CommandResult text(String text) {
        return new CommandResult(text, Map.of(), List.of(), CommandResultStatus.SUCCESS);
    }

    public static CommandResult of(String text, Map<String, Object> data) {
        return new CommandResult(text, data, List.of(), CommandResultStatus.SUCCESS);
    }

    public static CommandResult file(String text, CommandAttachment attachment) {
        return new CommandResult(
                text, Map.of(), List.of(attachment), CommandResultStatus.SUCCESS);
    }

    public static CommandResult withAttachments(String text,
                                                Map<String, Object> data,
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
        return !data.isEmpty();
    }

    public boolean hasAttachments() {
        return !attachments.isEmpty();
    }

    private static Map<String, Object> immutableData(Map<String, Object> source) {
        if (MapUtil.isEmpty(source)) {
            return Map.of();
        }
        Map<String, Object> copy = new LinkedHashMap<>();
        source.forEach((key, value) -> copy.put(
                Objects.requireNonNull(key, "result data key must not be null"),
                value));
        return Collections.unmodifiableMap(copy);
    }
}
