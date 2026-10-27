package com.nnm.bot.core.plugin.response;

import com.nnm.bot.core.plugin.model.DeliveryReport;

/** A delivery failure may follow a successful partial send; callers must not send another response. */
public class ReplyDeliveryException extends RuntimeException {
    private final DeliveryReport report;

    public ReplyDeliveryException(DeliveryReport report, RuntimeException cause) {
        super("Reply delivery failed; no automatic resend", cause);
        this.report = report;
    }

    public DeliveryReport report() {
        return report;
    }
}
