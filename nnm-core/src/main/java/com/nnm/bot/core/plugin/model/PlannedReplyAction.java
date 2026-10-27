package com.nnm.bot.core.plugin.model;

import com.nnm.bot.core.constant.CommandDeliveryConstants.Presentation;

import java.util.Objects;

/** 动作保留独立身份；文字降级不丢失动作归属和实际展示方式。 */
public record PlannedReplyAction(ReplyAction action, Presentation presentation) {
    public PlannedReplyAction {
        Objects.requireNonNull(action, "action");
        Objects.requireNonNull(presentation, "presentation");
        if (action.required() && !presentation.usesText() && presentation != Presentation.NATIVE) {
            throw new IllegalArgumentException("Required action cannot be omitted");
        }
    }

}
