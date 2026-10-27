package com.nnm.bot.core.plugin.model;

import com.nnm.bot.core.constant.CommandPresentationConstants.ViewFormat;

import java.util.List;
import java.util.Objects;

/** 插件承诺的数据视图契约；HTML/Markdown 模板改变排版，不能创造新的数据能力。 */
public record CommandView(String key, List<ViewFormat> formats) {
    public CommandView {
        if (key == null || !key.matches("[a-z][a-z0-9_.-]{0,79}")) {
            throw new IllegalArgumentException("Invalid view key");
        }
        formats = List.copyOf(Objects.requireNonNull(formats, "formats"));
        if (formats.isEmpty() || formats.size() != formats.stream().distinct().count()) {
            throw new IllegalArgumentException("View formats must be nonempty and unique");
        }
    }

    public static CommandView textImageAndMarkdown(String key) {
        return new CommandView(key, List.of(ViewFormat.TEXT, ViewFormat.IMAGE, ViewFormat.MARKDOWN));
    }
}
