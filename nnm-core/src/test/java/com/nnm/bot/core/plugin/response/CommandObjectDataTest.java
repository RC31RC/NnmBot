package com.nnm.bot.core.plugin.response;

import com.nnm.bot.core.constant.CommandDeliveryConstants.Presentation;

import com.nnm.bot.core.constant.CommandDeliveryConstants.Outcome;

import com.nnm.bot.core.plugin.model.CommandExecutionConfiguration.CommandTemplate;
import com.nnm.bot.core.plugin.model.ReplyAction.Command;
import com.nnm.bot.core.plugin.model.ReplyBlock.DataView;
import java.nio.file.Path;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.core.config.ReplyProperties;
import com.nnm.bot.core.constant.BotMessageScope;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.message.exception.BotMessageRejectedException;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.message.model.BotMarkdownMessage;
import com.nnm.bot.core.message.model.BotMessageSendPermit;
import com.nnm.bot.core.message.model.BotMessageReceipt;
import com.nnm.bot.core.message.model.PreparedBotMessage;
import com.nnm.bot.core.message.exception.BotMessageNotSentException;
import com.nnm.bot.core.message.render.BotImageRenderer;
import com.nnm.bot.core.message.sender.BotMessageSenderRouter;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.model.CommandAttachment;
import com.nnm.bot.core.plugin.model.CommandDescriptor;
import com.nnm.bot.core.plugin.model.CommandExecutionConfiguration;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.constant.CommandExecutionConstants.CommandResultStatus;
import com.nnm.bot.core.constant.CommandPresentationConstants.CompositionPolicy;
import com.nnm.bot.core.plugin.model.DeliveryReport;
import com.nnm.bot.core.constant.CommandDeliveryConstants.DeliveryStatus;
import com.nnm.bot.core.constant.CommandPresentationConstants.InteractionPolicy;
import com.nnm.bot.core.plugin.model.ReplyAction;
import com.nnm.bot.core.plugin.model.PresentationPolicy;
import com.nnm.bot.core.plugin.model.ReplyBlock;
import com.nnm.bot.core.plugin.model.ReplyMessage;
import com.nnm.bot.core.plugin.model.ResponsePlan;
import com.nnm.bot.core.plugin.model.RuntimeCommand;
import com.nnm.bot.core.constant.CommandPresentationConstants.ViewFormat;
import com.nnm.bot.core.plugin.model.CommandView;
import com.nnm.bot.core.plugin.model.CommandInvocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.test.util.ReflectionTestUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CommandObjectDataTest {
    private final CommandResponseDispatcher dispatcher = new CommandResponseDispatcher();
    private final CommandReplyRouter router = mock(CommandReplyRouter.class);
    private final ReplyDeliveryExecutor delivery = mock(ReplyDeliveryExecutor.class);
    private final BotMessageSenderRouter senders = mock(BotMessageSenderRouter.class);
    private final ReplyPlanner planner = new ReplyPlanner();
    private final BotMessageContext context = BotMessageContext.of(BotPlatform.ONEBOT,
            BotMessageScope.GROUP, "bot", "user", "group", "receipt", "", "", null);
    private final BotImageRenderer renderer = mock(BotImageRenderer.class);
    private final OrderData data = new OrderData("灰烬 Prime 套装", List.of(new Order("seller", 50)));
    private int sendAttempts;
    private int closed;
    private IntFunction<BotMessageReceipt> creation = index -> new BotMessageReceipt("platform-" + index);
    private Runnable cleanup = () -> closed++;

    @BeforeEach
    void setUp() {
        @SuppressWarnings("unchecked")
        ObjectProvider<BotImageRenderer> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(renderer);
        when(senders.supports(any(), any())).thenReturn(true);
        when(senders.canReserve(any(), anyInt())).thenReturn(true);
        when(senders.supportsComposition(any(), any())).thenReturn(true);
        ReflectionTestUtils.setField(planner, "senders", senders);
        ReflectionTestUtils.setField(planner, "limits", new ReplyProperties());
        ReflectionTestUtils.setField(planner, "imageRendererProvider", provider);
        ReflectionTestUtils.setField(dispatcher, "deliveryExecutor", delivery);
        ReflectionTestUtils.setField(dispatcher, "replyRouter", router);
        ReflectionTestUtils.setField(dispatcher, "planner", planner);
        ReflectionTestUtils.setField(dispatcher, "limits", new ReplyProperties());
        ReflectionTestUtils.setField(dispatcher, "imageRendererProvider", provider);
        ReflectionTestUtils.setField(dispatcher, "objectMapper", new ObjectMapper());
        CommandDataTextFormatter formatter = new CommandDataTextFormatter();
        ReflectionTestUtils.setField(formatter, "objectMapper", new ObjectMapper());
        ReflectionTestUtils.setField(dispatcher, "dataTextFormatter", formatter);
        @SuppressWarnings("unchecked")
        ObjectProvider<CommandRelatedActionProvider> relatedProvider = mock(ObjectProvider.class);
        when(relatedProvider.getIfAvailable()).thenReturn(null);
        ReflectionTestUtils.setField(dispatcher, "relatedActionAppender",
                new RelatedActionAppender(relatedProvider, new ReplyProperties()));
        when(router.select(eq(context), any(), any(), any())).thenAnswer(call -> {
            List<ResponsePlan> plans = planner.plan(context, call.getArgument(2), call.getArgument(3))
                    .orElseThrow(() -> new IllegalStateException("infeasible"));
            List<BotMessageSendPermit> permits = java.util.stream.IntStream.range(0, planner.platformMessageCount(context, plans))
                    .mapToObj(index -> BotMessageSendPermit.unrestricted()).toList();
            return new CommandReplyRouter.Selection(context, plans, permits);
        });
        when(delivery.prepare(any(), any(), any())).thenAnswer(call -> new PreparedBotMessage(
                () -> creation.apply(sendAttempts++), () -> cleanup.run(), call.getArgument(2)));
    }

    @Test
    void markdownAndExplicitPlainTemplateCompileIndependentlyFromHtml() {
        MarkdownViewRenderer markdown = new MarkdownViewRenderer();
        ReflectionTestUtils.setField(markdown, "limits", new ReplyProperties());
        ReflectionTestUtils.setField(dispatcher, "markdownViews", markdown);
        CommandExecutionConfiguration configuration = new CommandExecutionConfiguration(true, true, PresentationPolicy.defaults(),
                List.of(new CommandExecutionConfiguration.CommandTemplate("html-first", "market.orders", "<html>preserved</html>", null, null),
                        new CommandExecutionConfiguration.CommandTemplate("market", "market.orders", null,
                        "# {{itemName}}\n{{#orders}}- {{userName}}: {{platinum}}{{/orders}}", "{{itemName}}：{{#orders}}{{userName}} {{platinum}}{{/orders}}")), List.of(), "", 0, false);
        DataView<OrderData> view = new ReplyBlock.DataView<>("market.orders", data, null);
        ResponsePlan nativePlan = new ResponsePlan(0, 0, List.of(view), List.of(), null, CompositionPolicy.PREFER_SINGLE, ViewFormat.MARKDOWN, false);
        ReplyBlock nativeBlock = ReflectionTestUtils.invokeMethod(dispatcher, "compileView", null, configuration, nativePlan, view, context);
        assertThat(nativeBlock).isInstanceOfSatisfying(ReplyBlock.Markdown.class, block -> {
            assertThat(block.message().content()).isEqualTo("# 灰烬 Prime 套装\n- seller: 50");
            assertThat(block.plainText()).isEqualTo("灰烬 Prime 套装：seller 50");
        });
        ResponsePlan plainPlan = new ResponsePlan(0, 0, List.of(view), List.of(), null, CompositionPolicy.PREFER_SINGLE, ViewFormat.MARKDOWN, true);
        ReplyBlock plain = ReflectionTestUtils.invokeMethod(dispatcher, "compileView", null, configuration, plainPlan, view, context);
        assertThat(plain).isEqualTo(new ReplyBlock.Text("灰烬 Prime 套装：seller 50"));
        assertThat(configuration.templateHtml("market.orders")).isEqualTo("<html>preserved</html>");
        verifyNoInteractions(renderer);
    }

    @Test
    void keepsDtoTypeAndRecursivelySnapshotsCollections() {
        assertThat(view(CommandResult.view("market.orders", "摘要", data)).data()).isSameAs(data);
        ArrayList<String> nested = new ArrayList<>(List.of("before"));
        Map<String, Object> mutable = new LinkedHashMap<>();
        mutable.put("items", nested);
        CommandResult result = CommandResult.view("market.orders", "", mutable);
        nested.set(0, "after");
        mutable.clear();
        assertThat(((Map<?, ?>) view(result).data()).get("items")).isEqualTo(List.of("before"));
        assertThatThrownBy(() -> result.replies().clear()).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void textContainsTitleOnceAndCompleteNestedData() {
        dispatch(PresentationPolicy.defaults(), false, CommandResult.view("market.orders", "摘要", data));
        ResponsePlan plan = sent().getFirst();
        String text = ((ReplyBlock.Text) plan.blocks().getFirst()).content();
        assertThat(text).startsWith("摘要\n").contains("seller", "50", "灰烬 Prime 套装");
        assertThat(text.split("摘要", -1)).hasSize(2);
        assertThat(text).doesNotContain("Order[", "OrderData[");
    }

    @Test
    void summaryOnlyCompilesTheDeclaredSummaryAsText() {
        PresentationPolicy policy = new PresentationPolicy(List.of(ViewFormat.SUMMARY_ONLY, ViewFormat.TEXT),
                CompositionPolicy.PREFER_SINGLE, InteractionPolicy.COMMAND_TEXT, List.of());

        dispatch(policy, false, CommandResult.viewWithSummary(
                "market.orders", "摘要", "当前有 1 条订单", data));

        assertThat(sent()).singleElement().satisfies(plan -> {
            assertThat(plan.viewFormat()).isEqualTo(ViewFormat.SUMMARY_ONLY);
            assertThat(plan.blocks()).containsExactly(new ReplyBlock.Text("摘要\n当前有 1 条订单"));
        });
    }

    @Test
    void undeclaredViewFailsBeforePreparationAndStillReleasesOwnedFiles() throws Exception {
        Path directory = Files.createTempDirectory("undeclared-view-");
        Path file = Files.writeString(directory.resolve("result.txt"), "content");
        CommandAttachment resource = CommandAttachment.temporaryFile("result.txt", "text/plain", file, directory);
        CommandResult result = new CommandResult(CommandResultStatus.SUCCESS, List.of(ReplyMessage.content(List.of(
                new ReplyBlock.DataView<>("other.view", data, null), new ReplyBlock.File(resource)))), Map.of());
        ReplyDeliveryException failure = catchThrowableOfType(() -> dispatch(PresentationPolicy.defaults(), false, result),
                ReplyDeliveryException.class);
        assertThat(failure).hasRootCauseMessage("Undeclared view: other.view");
        assertThat(failure.report().definitelyUnsent()).isTrue();
        assertThat(directory).doesNotExist();
        verifyNoInteractions(router, delivery, renderer);
    }

    @Test
    void imageReceivesDtoFieldsAndRemainsInOrderedComposition() throws Exception {
        when(renderer.renderHtml(anyString(), anyMap())).thenReturn(png());
        dispatch(imagePolicy(CompositionPolicy.PREFER_SINGLE), true,
                CommandResult.view("market.orders", "摘要", data));
        assertThat(sent().getFirst().blocks()).extracting(Object::getClass)
                .containsExactly(ReplyBlock.Text.class, ReplyBlock.Image.class);
        ArgumentCaptor<Map<String, Object>> capture = ArgumentCaptor.captor();
        verify(renderer).renderHtml(eq("<html>template</html>"), capture.capture());
        assertThat(capture.getValue().get("orders")).isEqualTo(List.of(Map.of("userName", "seller", "platinum", 50)));
    }

    @Test
    void requireSingleChoosesDeclaredCompleteTextBeforeRenderingOrSending() {
        when(senders.supportsComposition(any(), any())).thenReturn(false);
        dispatch(imagePolicy(CompositionPolicy.REQUIRE_SINGLE), true,
                CommandResult.view("market.orders", "摘要", data));
        assertThat(sent()).hasSize(1);
        assertThat(((ReplyBlock.Text) sent().getFirst().blocks().getFirst()).content()).contains("seller", "50");
        verifyNoInteractions(renderer);
    }

    @Test
    void missingTemplateUsesDeclaredTextWithoutRendering() {
        dispatch(imagePolicy(CompositionPolicy.PREFER_SINGLE), false,
                CommandResult.view("market.orders", "摘要", data));
        assertThat(((ReplyBlock.Text) sent().getFirst().blocks().getFirst()).content()).contains("seller", "50");
        verifyNoInteractions(renderer);
    }

    @Test
    void explicitLogicalMessagesAreNeverMerged() {
        CommandResult result = new CommandResult(CommandResultStatus.SUCCESS, List.of(
                ReplyMessage.content(List.of(new ReplyBlock.Text("first"))),
                ReplyMessage.content(List.of(new ReplyBlock.Text("second")))), Map.of());
        DeliveryReport report = dispatch(PresentationPolicy.defaults(), false, result);
        assertThat(sent()).extracting(ResponsePlan::logicalMessage).containsExactly(0, 1);
        assertThat(report.outcome()).isEqualTo(Outcome.COMPLETE);
    }

    @Test
    void acceptedThenExplicitRejectionIsPartialAndRemainingMessagesAreNotSent() {
        creation = index -> {
            if (index == 1) throw new BotMessageRejectedException("rejected", false);
            return new BotMessageReceipt("first");
        };
        ReplyDeliveryException failure = catchThrowableOfType(() -> dispatch(PresentationPolicy.defaults(), false, threeMessages()),
                ReplyDeliveryException.class);
        assertThat(failure.report().outcome()).isEqualTo(Outcome.PARTIAL);
        assertThat(failure.report().items()).extracting(DeliveryReport.Item::status).containsExactly(
                DeliveryStatus.ACCEPTED, DeliveryStatus.REJECTED, DeliveryStatus.NOT_SENT);
        assertThat(failure.report().definitelyUnsent()).isFalse();
        assertThat(failure.report().items().getFirst().platformMessageId()).isEqualTo("first");
        assertThat(sendAttempts).isEqualTo(2);
        assertThat(closed).isEqualTo(3);
    }

    @Test
    void explicitRejectionUsesOneDeclaredTextFallbackOnTheSameSelectedInstance() {
        Command action = new ReplyAction.Command("下一项", "market next", false);
        ReplyMessage reply = new ReplyMessage(List.of(new ReplyBlock.Markdown(
                BotMarkdownMessage.content("**rich body**"), "plain body")), List.of(action), null, false);
        CommandResult result = new CommandResult(CommandResultStatus.SUCCESS, List.of(reply), Map.of());
        PresentationPolicy policy = new PresentationPolicy(List.of(ViewFormat.MARKDOWN, ViewFormat.TEXT),
                CompositionPolicy.REQUIRE_SINGLE, InteractionPolicy.PREFER_NATIVE, List.of());
        when(router.reserveFallback(context)).thenReturn(BotMessageSendPermit.unrestricted());
        creation = index -> {
            if (index == 0) throw new BotMessageRejectedException("media rejected", true);
            return new BotMessageReceipt("fallback-accepted");
        };

        DeliveryReport report = dispatch(policy, false, result);

        assertThat(sendAttempts).isEqualTo(2);
        assertThat(report.outcome()).isEqualTo(Outcome.COMPLETE);
        assertThat(report.items()).singleElement().satisfies(item -> {
            assertThat(item.status()).isEqualTo(DeliveryStatus.ACCEPTED);
            assertThat(item.platformMessageId()).isEqualTo("fallback-accepted");
            assertThat(item.reason()).contains("text fallback");
        });
        assertThat(report.actions()).singleElement().satisfies(item ->
                assertThat(item.presentation()).isEqualTo(Presentation.TEXT_FALLBACK));
        assertThat(sent()).hasSize(2);
        assertThat(sent().get(1).blocks()).containsExactly(new ReplyBlock.Text("plain body\n下一项：market next"));
        verify(router).reserveFallback(context);
    }

    @Test
    void safeRejectionDoesNotFallbackWithoutDeclaredPlainText() {
        ReplyMessage reply = ReplyMessage.content(List.of(new ReplyBlock.Markdown(
                BotMarkdownMessage.content("**rich body**"), null)));
        creation = index -> { throw new BotMessageRejectedException("media rejected", true); };

        ReplyDeliveryException failure = catchThrowableOfType(() -> dispatch(PresentationPolicy.defaults(), false,
                new CommandResult(CommandResultStatus.SUCCESS, List.of(reply), Map.of())), ReplyDeliveryException.class);

        assertThat(failure.report().items()).singleElement()
                .extracting(DeliveryReport.Item::status).isEqualTo(DeliveryStatus.REJECTED);
        assertThat(sendAttempts).isEqualTo(1);
        verify(router, never()).reserveFallback(any());
    }

    @Test
    void fallbackIsBlockedBeforeLaterReservedMessageSequences() {
        ReplyMessage first = ReplyMessage.content(List.of(new ReplyBlock.Markdown(
                BotMarkdownMessage.content("**rich body**"), "plain body")));
        ReplyMessage second = ReplyMessage.content(List.of(new ReplyBlock.Text("second message")));
        PresentationPolicy policy = new PresentationPolicy(List.of(ViewFormat.MARKDOWN, ViewFormat.TEXT),
                CompositionPolicy.REQUIRE_SINGLE, InteractionPolicy.COMMAND_TEXT, List.of());
        creation = index -> { throw new BotMessageRejectedException("media rejected", true); };

        ReplyDeliveryException failure = catchThrowableOfType(() -> dispatch(policy, false,
                new CommandResult(CommandResultStatus.SUCCESS, List.of(first, second), Map.of())), ReplyDeliveryException.class);

        assertThat(failure.report().items()).extracting(DeliveryReport.Item::status)
                .containsExactly(DeliveryStatus.REJECTED, DeliveryStatus.NOT_SENT);
        assertThat(sendAttempts).isEqualTo(1);
        verify(router, never()).reserveFallback(any());
    }

    @Test
    void timeoutIsUnknownAndNeverReplayed() {
        IllegalStateException timeout = new IllegalStateException("timeout");
        creation = index -> { throw timeout; };
        ReplyDeliveryException failure = catchThrowableOfType(() -> dispatch(PresentationPolicy.defaults(), false, threeMessages()),
                ReplyDeliveryException.class);
        assertThat(failure).hasCause(timeout);
        assertThat(failure.report().outcome()).isEqualTo(Outcome.UNKNOWN);
        assertThat(failure.report().definitelyUnsent()).isFalse();
        assertThat(sendAttempts).isEqualTo(1);
        assertThat(closed).isEqualTo(3);
    }

    @Test
    void preparationFailureDoesNotCreateEvenTheFirstMessageAndClosesEarlierPreparations() {
        doReturn(new PreparedBotMessage(() -> {
            sendAttempts++;
            return new BotMessageReceipt("must-not-create");
        }, () -> closed++, BotMessageSendPermit.unrestricted()))
                .doThrow(new IllegalStateException("upload failed"))
                .when(delivery).prepare(any(), any(), any());
        ReplyDeliveryException failure = catchThrowableOfType(() -> dispatch(PresentationPolicy.defaults(), false, threeMessages()),
                ReplyDeliveryException.class);
        assertThat(failure.report().items()).allMatch(item -> item.status() == DeliveryStatus.NOT_SENT);
        assertThat(sendAttempts).isZero();
        assertThat(closed).isEqualTo(1);
    }

    @Test
    void leaseLossInsideAdapterBeforeHttpSubmissionIsNotUnknown() {
        creation = index -> { throw new BotMessageNotSentException("lease expired"); };
        ReplyDeliveryException failure = catchThrowableOfType(() -> dispatch(PresentationPolicy.defaults(), false, threeMessages()),
                ReplyDeliveryException.class);
        assertThat(failure.report().items()).allMatch(item -> item.status() == DeliveryStatus.NOT_SENT);
        assertThat(failure.report().definitelyUnsent()).isTrue();
    }

    @Test
    void cleanupFailureDoesNotEraseAcceptanceOrChangeSettlementEvidence() {
        cleanup = () -> { throw new IllegalStateException("cleanup failed"); };
        DeliveryReport report = dispatch(PresentationPolicy.defaults(), false, threeMessages());
        assertThat(report.outcome()).isEqualTo(Outcome.COMPLETE);
        assertThat(report.definitelyUnsent()).isFalse();
        assertThat(report.items()).extracting(DeliveryReport.Item::platformMessageId)
                .containsExactly("platform-0", "platform-1", "platform-2");
        assertThat(report.cleanupFailures()).hasSize(3);
    }

    @Test
    void ownerLossBeforeCreationIsDefinitelyUnsent() {
        doThrow(new IllegalStateException("owner lost")).when(router).validate(any(), any());
        ReplyDeliveryException failure = catchThrowableOfType(() -> dispatch(PresentationPolicy.defaults(), false, threeMessages()),
                ReplyDeliveryException.class);
        assertThat(failure.report().items()).allMatch(item -> item.status() == DeliveryStatus.NOT_SENT);
        assertThat(failure.report().definitelyUnsent()).isTrue();
        verifyNoInteractions(delivery);
    }

    private DeliveryReport dispatch(PresentationPolicy policy, boolean template, CommandResult result) {
        List<CommandTemplate> templates = template ? List.of(new CommandExecutionConfiguration.CommandTemplate(
                "market", "market.orders", "<html>template</html>", null, null)) : List.<CommandExecutionConfiguration.CommandTemplate>of();
        CommandExecutionConfiguration configuration = new CommandExecutionConfiguration(true, true, policy, templates,
                List.of(), "", 0, false);
        CommandDescriptor descriptor = new CommandDescriptor("market", "市场", List.of(), "", "", MatchType.PREFIX, 1);
        return dispatcher.dispatch(context, new RuntimeCommand(new BotPlugin() {
            @Override
            public List<CommandView> views() {
                return List.of(new CommandView("market.orders",
                        List.of(ViewFormat.TEXT, ViewFormat.IMAGE, ViewFormat.SUMMARY_ONLY)));
            }

            @Override
            public CommandResult handle(CommandInvocation invocation) {
                throw new UnsupportedOperationException();
            }
        }, descriptor, configuration), result);
    }

    private List<ResponsePlan> sent() {
        ArgumentCaptor<ResponsePlan> capture = ArgumentCaptor.forClass(ResponsePlan.class);
        verify(delivery, atLeastOnce()).prepare(eq(context), capture.capture(), any());
        return capture.getAllValues();
    }

    private static ReplyBlock.DataView<?> view(CommandResult result) {
        return (ReplyBlock.DataView<?>) result.replies().getFirst().blocks().getLast();
    }

    private static PresentationPolicy imagePolicy(CompositionPolicy composition) {
        return new PresentationPolicy(List.of(ViewFormat.IMAGE, ViewFormat.TEXT), composition,
                InteractionPolicy.COMMAND_TEXT, List.of());
    }

    private static CommandResult threeMessages() {
        return new CommandResult(CommandResultStatus.SUCCESS, List.of("one", "two", "three").stream()
                .map(value -> ReplyMessage.content(List.of(new ReplyBlock.Text(value)))).toList(), Map.of());
    }

    private static byte[] png() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB), "png", output);
        return output.toByteArray();
    }

    public record OrderData(String itemName, List<Order> orders) {}
    public record Order(String userName, int platinum) {}
}
