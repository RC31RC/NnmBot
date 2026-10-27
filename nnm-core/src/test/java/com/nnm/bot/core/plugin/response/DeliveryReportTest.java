package com.nnm.bot.core.plugin.response;

import com.nnm.bot.core.constant.CommandDeliveryConstants.Outcome;

import com.nnm.bot.core.constant.CommandPresentationConstants.CompositionPolicy;
import com.nnm.bot.core.plugin.model.DeliveryReport;
import com.nnm.bot.core.constant.CommandDeliveryConstants.DeliveryStatus;
import com.nnm.bot.core.plugin.model.PlannedReplyAction;
import com.nnm.bot.core.constant.CommandDeliveryConstants.Presentation;
import com.nnm.bot.core.plugin.model.ReplyAction;
import com.nnm.bot.core.plugin.model.ReplyBlock;
import com.nnm.bot.core.plugin.model.ResponsePlan;
import com.nnm.bot.core.constant.CommandPresentationConstants.ViewFormat;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DeliveryReportTest {
    @Test
    void textFallbackHasItsOwnPresentationAndSharesTheOwningMessageOutcome() {
        DeliveryReport report = DeliveryReport.from(List.of(item(DeliveryStatus.UNKNOWN)),
                List.of(plan(Presentation.TEXT_FALLBACK)), List.of());

        assertThat(report.outcome()).isEqualTo(Outcome.UNKNOWN);
        assertThat(report.definitelyUnsent()).isFalse();
        assertThat(report.actions()).singleElement().satisfies(action -> {
            assertThat(action.logicalMessage()).isEqualTo(2);
            assertThat(action.part()).isEqualTo(1);
            assertThat(action.presentation()).isEqualTo(Presentation.TEXT_FALLBACK);
            assertThat(action.status()).isEqualTo(DeliveryStatus.UNKNOWN);
        });
    }

    @Test
    void skippedOptionalActionDoesNotTurnAcceptedContentIntoPartialFailure() {
        DeliveryReport report = DeliveryReport.from(List.of(item(DeliveryStatus.ACCEPTED)),
                List.of(plan(Presentation.OMITTED_INFEASIBLE)), List.of());

        assertThat(report.outcome()).isEqualTo(Outcome.COMPLETE);
        assertThat(report.actions()).singleElement().satisfies(action -> {
            assertThat(action.status()).isEqualTo(DeliveryStatus.NOT_SENT);
            assertThat(action.reason()).contains("Optional action omitted");
        });
    }

    @Test
    void actionWithoutOwningDeliveryCannotBeReportedAsAccepted() {
        DeliveryReport report = DeliveryReport.from(List.of(), List.of(plan(Presentation.COMMAND_TEXT)), List.of());
        assertThat(report.actions().getFirst().status()).isEqualTo(DeliveryStatus.NOT_SENT);
    }

    private DeliveryReport.Item item(DeliveryStatus status) {
        return new DeliveryReport.Item(2, 1, "bot", null, status, "receipt status");
    }

    private ResponsePlan plan(Presentation presentation) {
        return new ResponsePlan(2, 1, List.of(new ReplyBlock.Text("body")),
                List.of(new PlannedReplyAction(new ReplyAction.Command("next", "next", false), presentation)),
                null, CompositionPolicy.ALLOW_SPLIT, ViewFormat.TEXT, false);
    }
}
