package com.nnm.bot.core.plugin.model;

import com.nnm.bot.core.constant.CommandPresentationConstants.InteractionPolicy;

import com.nnm.bot.core.constant.BotMessageScope;
import com.nnm.bot.core.constant.BotPlatform;
import java.util.List;
import java.util.Objects;

/** Markdown 交互偏好及平台/会话覆盖；不限制命令返回的内容类型。 */
public record PresentationPolicy(InteractionPolicy interaction, List<Override> overrides) {
    public PresentationPolicy {
        Objects.requireNonNull(interaction, "interaction");
        overrides = overrides == null ? List.of() : List.copyOf(overrides);
        if (overrides.stream().map(item -> item.platform() + ":" + item.scope()).distinct().count() != overrides.size())
            throw new IllegalArgumentException("Duplicate presentation override");
    }

    public record Override(BotPlatform platform, BotMessageScope scope, InteractionPolicy interaction) {
        public Override {
            Objects.requireNonNull(platform, "platform");
            Objects.requireNonNull(interaction, "interaction");
        }
    }

    public PresentationPolicy resolve(BotPlatform platform, BotMessageScope scope) {
        Override selected = overrides.stream().filter(item -> item.platform() == platform && item.scope() == scope)
            .findFirst().orElseGet(() -> overrides.stream()
                .filter(item -> item.platform() == platform && item.scope() == null).findFirst().orElse(null));
        return selected == null ? this : new PresentationPolicy(selected.interaction(), List.of());
    }

    public static PresentationPolicy defaults() {
        return new PresentationPolicy(InteractionPolicy.PREFER_NATIVE, List.of());
    }
}
