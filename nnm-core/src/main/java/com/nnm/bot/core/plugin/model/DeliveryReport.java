package com.nnm.bot.core.plugin.model;

import com.nnm.bot.core.constant.CommandDeliveryConstants.Outcome;

import com.nnm.bot.core.constant.CommandDeliveryConstants.DeliveryStatus;

import com.nnm.bot.core.constant.CommandDeliveryConstants.Presentation;
import java.util.List;

/** ACCEPTED 只表示平台接受，不表示用户已经阅读。 */
public record DeliveryReport(List<Item> items, List<ActionItem> actions,
                             List<String> cleanupFailures) {
    public DeliveryReport {
        items = List.copyOf(items);
        actions = List.copyOf(actions);
        cleanupFailures = List.copyOf(cleanupFailures);
    }

    public Outcome outcome() {
        if (items.isEmpty()) return Outcome.SILENT;
        if (items.stream().anyMatch(item -> item.status() == DeliveryStatus.UNKNOWN)) return Outcome.UNKNOWN;
        long accepted = items.stream().filter(item -> item.status() == DeliveryStatus.ACCEPTED).count();
        if (accepted == items.size()) return Outcome.COMPLETE;
        return accepted == 0 ? Outcome.FAILED : Outcome.PARTIAL;
    }

    public boolean definitelyUnsent() {
        return !items.isEmpty() && items.stream().allMatch(item ->
                item.status() == DeliveryStatus.REJECTED || item.status() == DeliveryStatus.NOT_SENT);
    }

    public record Item(int logicalMessage, int part, String instance,
                       String platformMessageId, DeliveryStatus status, String reason) {
    }

    public record ActionItem(int logicalMessage, int part, String label,
                             Presentation presentation, DeliveryStatus status, String reason) {
    }

    public static DeliveryReport from(List<Item> items, List<ResponsePlan> plans, List<String> cleanupFailures) {
        List<ActionItem> actions = plans.stream().flatMap(plan -> plan.actions().stream().map(action -> {
            Item delivery = items.stream().filter(item -> item.logicalMessage() == plan.logicalMessage()
                    && item.part() == plan.part()).findFirst().orElse(null);
            boolean included = action.presentation().usesText() || action.presentation() == Presentation.NATIVE;
            DeliveryStatus status = included && delivery != null ? delivery.status() : DeliveryStatus.NOT_SENT;
            String reason = switch (action.presentation()) {
                case NATIVE -> delivery == null ? "Message not attempted" : delivery.reason();
                case COMMAND_TEXT -> delivery == null ? "Message not attempted" : delivery.reason();
                case TEXT_FALLBACK -> "Native interaction unavailable; explicit text used";
                case OMITTED_DISABLED -> "Optional action disabled by presentation policy";
                case OMITTED_INFEASIBLE -> "Optional action omitted to preserve required content and reply budget";
            };
            return new ActionItem(plan.logicalMessage(), plan.part(), action.action().label(),
                    action.presentation(), status, reason);
        })).toList();
        return new DeliveryReport(items, actions, cleanupFailures);
    }
}
