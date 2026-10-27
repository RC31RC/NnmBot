package com.nnm.bot.core.plugin.model;

import com.nnm.bot.core.constant.CommandPresentationConstants.ViewFormat;

import com.nnm.bot.core.constant.CommandPresentationConstants.InteractionPolicy;

import com.nnm.bot.core.constant.CommandPresentationConstants.CompositionPolicy;

import com.nnm.bot.core.constant.BotMessageScope;
import com.nnm.bot.core.constant.BotPlatform;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/** 默认展示策略及平台/会话覆盖；只影响本次实际存在的数据视图。 */
public record PresentationPolicy(List<ViewFormat> viewOrder, CompositionPolicy composition,
                                 InteractionPolicy interaction, List<Override> overrides) {
    public PresentationPolicy {
        viewOrder = checkedOrder(viewOrder);
        Objects.requireNonNull(composition, "composition");
        Objects.requireNonNull(interaction, "interaction");
        overrides = overrides == null ? List.of() : List.copyOf(overrides);
        HashSet<String> keys = new HashSet<String>();
        for (Override override : overrides) {
            if (!keys.add(override.platform() + ":" + override.scope())) {
                throw new IllegalArgumentException("Duplicate presentation override");
            }
        }
    }

    public record Override(BotPlatform platform, BotMessageScope scope, List<ViewFormat> viewOrder,
                           CompositionPolicy composition, InteractionPolicy interaction) {
        public Override {
            Objects.requireNonNull(platform, "platform");
            viewOrder = checkedOrder(viewOrder);
            Objects.requireNonNull(composition, "composition");
            Objects.requireNonNull(interaction, "interaction");
        }
    }

    public PresentationPolicy resolve(BotPlatform platform, BotMessageScope scope) {
        Override selected = overrides.stream().filter(item -> item.platform() == platform && item.scope() == scope)
                .findFirst().orElseGet(() -> overrides.stream()
                        .filter(item -> item.platform() == platform && item.scope() == null).findFirst().orElse(null));
        return selected == null ? this : new PresentationPolicy(selected.viewOrder(), selected.composition(),
                selected.interaction(), List.of());
    }

    public static PresentationPolicy defaults() {
        return new PresentationPolicy(List.of(ViewFormat.TEXT), CompositionPolicy.PREFER_SINGLE,
                InteractionPolicy.COMMAND_TEXT, List.of());
    }

    public boolean allowsFallback(ViewFormat selected, ViewFormat fallback) {
        int index = viewOrder.indexOf(selected);
        return index >= 0 && viewOrder.indexOf(fallback) > index;
    }

    private static List<ViewFormat> checkedOrder(List<ViewFormat> order) {
        order = List.copyOf(Objects.requireNonNull(order, "viewOrder"));
        if (order.isEmpty() || order.size() != order.stream().distinct().count()) {
            throw new IllegalArgumentException("viewOrder must be nonempty and unique");
        }
        return order;
    }
}
