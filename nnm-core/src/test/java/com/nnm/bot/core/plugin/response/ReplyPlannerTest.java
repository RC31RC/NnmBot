package com.nnm.bot.core.plugin.response;

import com.nnm.bot.core.plugin.model.ReplyAction.Command;
import com.nnm.bot.core.plugin.model.ReplyBlock.Image;
import com.nnm.bot.core.config.ReplyProperties;
import com.nnm.bot.core.constant.BotMessageScope;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.message.model.BotMarkdownMessage;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.constant.BotMessageConstants.PartKind;
import com.nnm.bot.core.message.render.BotImageRenderer;
import com.nnm.bot.core.message.sender.BotMessageSenderRouter;
import com.nnm.bot.core.plugin.model.CommandAttachment;
import com.nnm.bot.core.plugin.model.CommandExecutionConfiguration;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.constant.CommandExecutionConstants.CommandResultStatus;
import com.nnm.bot.core.constant.CommandPresentationConstants.CompositionPolicy;
import com.nnm.bot.core.constant.CommandPresentationConstants.InteractionPolicy;
import com.nnm.bot.core.plugin.model.PresentationPolicy;
import com.nnm.bot.core.plugin.model.PlannedReplyAction;
import com.nnm.bot.core.constant.CommandDeliveryConstants.Presentation;
import com.nnm.bot.core.plugin.model.ReplyAction;
import com.nnm.bot.core.plugin.model.ReplyBlock;
import com.nnm.bot.core.plugin.model.ReplyMessage;
import com.nnm.bot.core.plugin.model.ResponsePlan;
import com.nnm.bot.core.constant.CommandPresentationConstants.ViewFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ReplyPlannerTest {
    private final ReplyPlanner planner = new ReplyPlanner();
    private final BotMessageSenderRouter senders = mock(BotMessageSenderRouter.class);
    private final ReplyProperties limits = new ReplyProperties();
    private final BotImageRenderer renderer = mock(BotImageRenderer.class);
    private final BotMessageContext context = BotMessageContext.of(BotPlatform.QQ_OFFICIAL,
            BotMessageScope.GROUP, "app", "sender", "group", "receipt", "", "", null);

    @BeforeEach
    void setUp() {
        @SuppressWarnings("unchecked")
        ObjectProvider<BotImageRenderer> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(renderer);
        when(senders.supports(any(), any())).thenReturn(true);
        when(senders.canReserve(any(), org.mockito.ArgumentMatchers.anyInt())).thenReturn(true);
        when(senders.supportsComposition(any(), any())).thenAnswer(call ->
                call.getArgument(1).equals(List.of(PartKind.TEXT, PartKind.IMAGE)));
        ReflectionTestUtils.setField(planner, "senders", senders);
        ReflectionTestUtils.setField(planner, "limits", limits);
        ReflectionTestUtils.setField(planner, "imageRendererProvider", provider);
    }

    @Test
    void markdownDataViewHasExplicitPlainTemplateRepresentationForNonMarkdownPlatforms() {
        when(senders.supports(context, com.nnm.bot.core.constant.BotChannelCapability.MARKDOWN_MESSAGE)).thenReturn(false);
        CommandExecutionConfiguration configuration = new CommandExecutionConfiguration(true, false,
                new PresentationPolicy(List.of(ViewFormat.MARKDOWN), CompositionPolicy.REQUIRE_SINGLE, InteractionPolicy.COMMAND_TEXT, List.of()),
                List.of(new CommandExecutionConfiguration.CommandTemplate("md", "orders", null, "# {{title}}", "{{title}}")), List.of(), "", 0, false);
        List<ResponsePlan> plans = planner.plan(context, configuration, CommandResult.view("orders", "标题", Map.of("title", "结果"))).orElseThrow();
        assertThat(plans).hasSize(1);
        assertThat(plans.getFirst().viewFormat()).isEqualTo(ViewFormat.MARKDOWN);
        assertThat(plans.getFirst().plainMarkdown()).isTrue();
        assertThat(planner.platformMessageCount(context, plans)).isOne();
        verifyNoInteractions(renderer);
    }

    @Test
    void repeatedInvocationsUseOnlyTheActualCurrentResultShape() {
        CommandExecutionConfiguration configuration = configuration(CompositionPolicy.ALLOW_SPLIT);
        Image image = new ReplyBlock.Image(new CommandAttachment("current.png", "image/png", new byte[]{1}));
        CommandResult first = new CommandResult(CommandResultStatus.SUCCESS, List.of(
                ReplyMessage.content(List.of(new ReplyBlock.Text("初次介绍"))),
                new ReplyMessage(List.of(new ReplyBlock.Text("详情"), image),
                        List.of(new ReplyAction.Command("继续", "next", false)), null, false)), Map.of());
        List<ResponsePlan> firstPlans = planner.compile(context, planner.plan(context, configuration, first).orElseThrow());
        List<ResponsePlan> secondPlans = planner.compile(context, planner.plan(context, configuration, result(image)).orElseThrow());
        assertThat(firstPlans).hasSizeGreaterThan(1);
        assertThat(secondPlans).hasSize(1);
        assertThat(secondPlans.getFirst().blocks()).containsExactly(image);
        assertThat(secondPlans.getFirst().actions()).isEmpty();
        List<ResponsePlan> errorPlans = planner.plan(context, configuration, CommandResult.rejected("参数错误")).orElseThrow();
        assertThat(errorPlans.getFirst().blocks()).containsExactly(new ReplyBlock.Text("参数错误"));
        verifyNoInteractions(renderer);
    }

    @Test
    void qualifiedMarkdownUsesOneNativeKeyboardAndUnqualifiedTargetUsesText() {
        org.mockito.Mockito.doAnswer(call -> ((List<?>) call.getArgument(1)).contains(PartKind.KEYBOARD))
                .when(senders).supportsComposition(any(), any());
        ReplyMessage reply = new ReplyMessage(List.of(markdown("正文")), List.of(new ReplyAction.Command("下一项", "next", true)), null, false);
        CommandResult result = new CommandResult(CommandResultStatus.SUCCESS, List.of(reply), Map.of());
        CommandExecutionConfiguration configuration = new CommandExecutionConfiguration(true, false,
                new PresentationPolicy(List.of(ViewFormat.TEXT), CompositionPolicy.ALLOW_SPLIT, InteractionPolicy.PREFER_NATIVE, List.of()), List.of(), List.of(), "", 0, false);
        List<ResponsePlan> nativePlans = planner.plan(context, configuration, result).orElseThrow();
        assertThat(nativePlans.getFirst().actions().getFirst().presentation()).isEqualTo(Presentation.NATIVE);
        assertThat(planner.platformMessageCount(context, nativePlans)).isOne();
        when(senders.supports(context, com.nnm.bot.core.constant.BotChannelCapability.NATIVE_KEYBOARD)).thenReturn(false);
        List<ResponsePlan> textPlans = planner.plan(context, configuration, result).orElseThrow();
        assertThat(textPlans.getFirst().actions().getFirst().presentation()).isEqualTo(Presentation.TEXT_FALLBACK);
        assertThat(textPlans.stream().flatMap(plan -> plan.blocks().stream())).anyMatch(ReplyBlock.Text.class::isInstance);
        assertThatThrownBy(() -> planner.compile(context, nativePlans)).hasMessageContaining("native keyboard");
    }

    @Test
    void markdownCanUseDeclaredTextForSingleMessageEvenWhenNativeMarkdownIsSupported() {
        List<ResponsePlan> plans = planner.plan(context, configuration(CompositionPolicy.REQUIRE_SINGLE), result(
                new ReplyBlock.Text("title"), markdown("body"))).orElseThrow();
        assertThat(plans).hasSize(1);
        assertThat(plans.getFirst().blocks()).containsExactly(new ReplyBlock.Text("title\nbody"));
        verifyNoInteractions(renderer);
    }

    @Test
    void requiredMarkdownWithoutTextCannotPretendToSatisfySingleComposition() {
        assertThat(planner.plan(context, configuration(CompositionPolicy.REQUIRE_SINGLE), result(
                new ReplyBlock.Text("title"), markdown(null)))).isEmpty();
    }

    @Test
    void preferSingleTriesCompleteTextBeforeSplittingMarkdown() {
        List<ResponsePlan> plans = planner.plan(context, configuration(CompositionPolicy.PREFER_SINGLE), result(
                new ReplyBlock.Text("title"), markdown("body"))).orElseThrow();
        assertThat(plans.getFirst().blocks()).containsExactly(new ReplyBlock.Text("title\nbody"));
    }

    @Test
    void allowSplitPreservesNativeMarkdownAndBlockOrder() {
        List<ResponsePlan> plans = planner.plan(context, configuration(CompositionPolicy.ALLOW_SPLIT), result(
                new ReplyBlock.Text("title"), markdown("body"))).orElseThrow();
        assertThat(planner.compile(context, plans)).extracting(ResponsePlan::blocks).containsExactly(
                List.of(new ReplyBlock.Text("title")), List.of(markdown("body")));
    }

    @Test
    void splittingKeepsSupportedContiguousMediaTogetherAndMentionsOnlyFirstPart() {
        Image image = new ReplyBlock.Image(new CommandAttachment("image.png", "image/png", new byte[]{1}));
        List<ResponsePlan> plans = planner.plan(context, configuration(CompositionPolicy.ALLOW_SPLIT), result(
                new ReplyBlock.Text("title"), image, new ReplyBlock.Text("footer"))).orElseThrow();
        List<ResponsePlan> compiled = planner.compile(context, plans);
        assertThat(compiled).extracting(ResponsePlan::blocks).containsExactly(
                List.of(new ReplyBlock.Text("title"), image), List.of(new ReplyBlock.Text("footer")));
        assertThat(compiled).extracting(ResponsePlan::part).containsExactly(0, 1);
        assertThat(compiled).extracting(ResponsePlan::mentionUserId).containsExactly("sender", null);
    }

    @Test
    void messageBudgetCountsLegalSegmentsInsteadOfEveryContentBlock() {
        limits.setMaxPlatformMessages(2);
        Image image = new ReplyBlock.Image(new CommandAttachment("image.png", "image/png", new byte[]{1}));
        List<ResponsePlan> plans = planner.plan(context, configuration(CompositionPolicy.ALLOW_SPLIT), result(
                new ReplyBlock.Text("title"), image, new ReplyBlock.Text("footer"))).orElseThrow();
        assertThat(planner.compile(context, plans)).hasSize(2);
        limits.setMaxPlatformMessages(1);
        assertThat(planner.plan(context, configuration(CompositionPolicy.ALLOW_SPLIT), result(
                new ReplyBlock.Text("title"), image, new ReplyBlock.Text("footer")))).isEmpty();
    }

    @Test
    void summaryOnlyRequiresEveryDataViewToDeclareASummary() {
        PresentationPolicy policy = new PresentationPolicy(List.of(ViewFormat.SUMMARY_ONLY),
                CompositionPolicy.PREFER_SINGLE, InteractionPolicy.COMMAND_TEXT, List.of());
        CommandResult result = result(new ReplyBlock.DataView<>("system.status", Map.of("status", "ok"), null));

        assertThat(planner.plan(mockTarget(), policy, List.of(), result.replies())).isEmpty();
    }

    @Test
    void summaryOnlyFallsBackToTextWhenTheResultHasNoSummary() {
        PresentationPolicy policy = new PresentationPolicy(List.of(ViewFormat.SUMMARY_ONLY, ViewFormat.TEXT),
                CompositionPolicy.PREFER_SINGLE, InteractionPolicy.COMMAND_TEXT, List.of());
        CommandResult result = result(new ReplyBlock.DataView<>("system.status", Map.of("status", "ok"), null));

        List<ResponsePlan> plans = planner.plan(mockTarget(), policy, List.of(), result.replies()).orElseThrow();

        assertThat(plans).singleElement().satisfies(plan -> assertThat(plan.viewFormat()).isEqualTo(ViewFormat.TEXT));
    }

    @Test
    void summaryOnlyIsPreferredWhenTheResultDeclaresAValidSummary() {
        PresentationPolicy policy = new PresentationPolicy(List.of(ViewFormat.SUMMARY_ONLY, ViewFormat.TEXT),
                CompositionPolicy.PREFER_SINGLE, InteractionPolicy.COMMAND_TEXT, List.of());
        CommandResult result = result(new ReplyBlock.DataView<>("system.status", Map.of("status", "ok"), "状态正常"));

        List<ResponsePlan> plans = planner.plan(mockTarget(), policy, List.of(), result.replies()).orElseThrow();

        assertThat(plans).singleElement().satisfies(plan -> {
            assertThat(plan.viewFormat()).isEqualTo(ViewFormat.SUMMARY_ONLY);
            assertThat(((ReplyBlock.DataView<?>) plan.blocks().getFirst()).summary()).isEqualTo("状态正常");
        });
    }

    @Test
    void summaryOnlyCountsTheDeclaredSummaryAgainstTheTextBudget() {
        limits.setMaxTextCharacters(3);
        PresentationPolicy policy = new PresentationPolicy(List.of(ViewFormat.SUMMARY_ONLY),
                CompositionPolicy.PREFER_SINGLE, InteractionPolicy.COMMAND_TEXT, List.of());
        CommandResult result = result(new ReplyBlock.DataView<>("system.status", Map.of(), "状态正常"));

        assertThatThrownBy(() -> planner.plan(mockTarget(), policy, List.of(), result.replies()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Reply text limit exceeded");
    }

    @Test
    void limitsActionsAcrossTheWholeResultNotOnlyPerLogicalMessage() {
        List<ReplyAction> actions = java.util.stream.IntStream.range(0, 11)
                .mapToObj(index -> (ReplyAction) new ReplyAction.Command(
                        "action-" + index, "command-" + index, false))
                .toList();
        CommandResult result = new CommandResult(CommandResultStatus.SUCCESS, List.of(
                new com.nnm.bot.core.plugin.model.ReplyMessage(
                        List.of(new ReplyBlock.Text("first")), actions.subList(0, 6), null, false),
                new com.nnm.bot.core.plugin.model.ReplyMessage(
                        List.of(new ReplyBlock.Text("second")), actions.subList(6, 11), null, false)), Map.of());

        CommandExecutionConfiguration configuration = configuration(CompositionPolicy.ALLOW_SPLIT);
        assertThatThrownBy(() -> planner.plan(mockTarget(), configuration.presentation(), configuration.templates(),
                result.replies()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Reply action limit exceeded");
    }

    @Test
    void choosesLowerPriorityViewWhenItMakesTheWholeReplyGroupFeasible() {
        limits.setMaxPlatformMessages(3);
        doReturn(false).when(senders).supportsComposition(any(), any());
        when(senders.canReserve(any(), org.mockito.ArgumentMatchers.intThat(count -> count <= 3)))
                .thenReturn(true);
        CommandExecutionConfiguration configuration = new CommandExecutionConfiguration(true, true,
                new PresentationPolicy(List.of(ViewFormat.IMAGE, ViewFormat.TEXT),
                        CompositionPolicy.ALLOW_SPLIT, InteractionPolicy.COMMAND_TEXT, List.of()),
                List.of(new CommandExecutionConfiguration.CommandTemplate("default", "system.status", "<p>x</p>", null, null)),
                List.of(), "", 0, false);
        ReplyMessage first = new ReplyMessage(List.of(new ReplyBlock.DataView<>("system.status", Map.of("status", "ok"), null),
                new ReplyBlock.Text("footer")), List.of(), null, false);
        ReplyMessage second = new ReplyMessage(List.of(new ReplyBlock.Text("left"),
                new ReplyBlock.Image(new CommandAttachment("image.png", "image/png", new byte[]{1}))),
                List.of(), null, false);

        List<ResponsePlan> plans = planner.plan(context, configuration,
                new CommandResult(CommandResultStatus.SUCCESS, List.of(first, second), Map.of())).orElseThrow();

        assertThat(plans).hasSize(2);
        assertThat(plans.getFirst().viewFormat()).isEqualTo(ViewFormat.TEXT);
        assertThat(planner.platformMessageCount(context, plans)).isEqualTo(3);
    }

    @Test
    void fallbackOrderIsDirectional() {
        PresentationPolicy imageFirst = new PresentationPolicy(List.of(ViewFormat.IMAGE, ViewFormat.TEXT),
                CompositionPolicy.PREFER_SINGLE, InteractionPolicy.COMMAND_TEXT, List.of());
        PresentationPolicy textFirst = new PresentationPolicy(List.of(ViewFormat.TEXT, ViewFormat.IMAGE),
                CompositionPolicy.PREFER_SINGLE, InteractionPolicy.COMMAND_TEXT, List.of());
        assertThat(imageFirst.allowsFallback(ViewFormat.IMAGE, ViewFormat.TEXT)).isTrue();
        assertThat(textFirst.allowsFallback(ViewFormat.IMAGE, ViewFormat.TEXT)).isFalse();
        assertThat(PresentationPolicy.defaults().allowsFallback(ViewFormat.IMAGE, ViewFormat.TEXT)).isFalse();
    }

    @Test
    void explicitTextFallbackPreservesDeclaredContentAndRendersNativeActionsAsText() {
        Command action = new ReplyAction.Command("下一项", "market next", false);
        ResponsePlan selected = new ResponsePlan(2, 0,
                List.of(new ReplyBlock.Text("标题"), markdown("正文")),
                List.of(new PlannedReplyAction(action, Presentation.NATIVE)), null,
                CompositionPolicy.REQUIRE_SINGLE, ViewFormat.MARKDOWN, false);
        PresentationPolicy policy = new PresentationPolicy(List.of(ViewFormat.MARKDOWN, ViewFormat.TEXT),
                CompositionPolicy.REQUIRE_SINGLE, InteractionPolicy.PREFER_NATIVE, List.of());

        ResponsePlan fallback = planner.explicitTextFallback(context, selected, policy).orElseThrow();

        assertThat(fallback.logicalMessage()).isEqualTo(2);
        assertThat(fallback.blocks()).containsExactly(new ReplyBlock.Text("标题\n正文\n下一项：market next"));
        assertThat(fallback.actions()).containsExactly(
                new PlannedReplyAction(action, Presentation.TEXT_FALLBACK));
        assertThat(fallback.viewFormat()).isEqualTo(ViewFormat.TEXT);
        assertThat(fallback.composition()).isEqualTo(CompositionPolicy.REQUIRE_SINGLE);
    }

    @Test
    void explicitTextFallbackNeverDropsFilesOrIgnoresConfiguredOrder() {
        PresentationPolicy policy = new PresentationPolicy(List.of(ViewFormat.MARKDOWN, ViewFormat.TEXT),
                CompositionPolicy.PREFER_SINGLE, InteractionPolicy.COMMAND_TEXT, List.of());
        ResponsePlan markdownAndFile = new ResponsePlan(0, 0,
                List.of(markdown("正文"), new ReplyBlock.File(new CommandAttachment("report.txt", "text/plain", new byte[]{1}))),
                List.of(), null, CompositionPolicy.PREFER_SINGLE, ViewFormat.MARKDOWN, false);
        PresentationPolicy textFirst = new PresentationPolicy(List.of(ViewFormat.TEXT, ViewFormat.MARKDOWN),
                CompositionPolicy.PREFER_SINGLE, InteractionPolicy.COMMAND_TEXT, List.of());
        ResponsePlan markdownOnly = new ResponsePlan(0, 0, List.of(markdown("正文")), List.of(), null,
                CompositionPolicy.PREFER_SINGLE, ViewFormat.MARKDOWN, false);

        assertThat(planner.explicitTextFallback(context, markdownAndFile, policy)).isEmpty();
        assertThat(planner.explicitTextFallback(context, markdownOnly, textFirst)).isEmpty();
    }

    @Test
    void explicitTextFallbackMustFitTheEffectiveTextBudget() {
        limits.setMaxTextCharacters(5);
        PresentationPolicy policy = new PresentationPolicy(List.of(ViewFormat.MARKDOWN, ViewFormat.TEXT),
                CompositionPolicy.PREFER_SINGLE, InteractionPolicy.COMMAND_TEXT, List.of());
        ResponsePlan markdown = new ResponsePlan(0, 0, List.of(markdown("a longer plain representation")), List.of(), null,
                CompositionPolicy.PREFER_SINGLE, ViewFormat.MARKDOWN, false);

        assertThat(planner.explicitTextFallback(context, markdown, policy)).isEmpty();
    }

    @Test
    void actionsRemainTypedAndBelongOnlyToTheLastPart() {
        Command action = new ReplyAction.Command("下一项", "next", false);
        ReplyMessage reply = new ReplyMessage(List.of(markdown(null)), List.of(action), null, false);
        List<ResponsePlan> plans = planner.plan(context, configuration(CompositionPolicy.ALLOW_SPLIT),
                new CommandResult(CommandResultStatus.SUCCESS, List.of(reply), Map.of())).orElseThrow();
        List<ResponsePlan> compiled = planner.compile(context, plans);

        assertThat(compiled).hasSize(2);
        assertThat(compiled.getFirst().actions()).isEmpty();
        assertThat(compiled.getLast().actions()).containsExactly(new PlannedReplyAction(action, Presentation.COMMAND_TEXT));
        assertThat(compiled.getLast().text()).isEqualTo("下一项：next");
    }

    @Test
    void optionalActionsCannotDisqualifyAnOtherwiseFeasibleSingleMessage() {
        Command action = new ReplyAction.Command("下一项", "next", false);
        ReplyMessage reply = new ReplyMessage(List.of(markdown(null)), List.of(action), null, false);
        List<ResponsePlan> plans = planner.plan(context, configuration(CompositionPolicy.REQUIRE_SINGLE),
                new CommandResult(CommandResultStatus.SUCCESS, List.of(reply), Map.of())).orElseThrow();

        assertThat(planner.compile(context, plans)).hasSize(1);
        assertThat(plans.getFirst().blocks()).containsExactly(markdown(null));
        assertThat(plans.getFirst().actions()).containsExactly(
                new PlannedReplyAction(action, Presentation.OMITTED_INFEASIBLE));
    }

    @Test
    void optionalActionsCannotConsumeTheOnlyRemainingReplySlot() {
        limits.setMaxPlatformMessages(1);
        ReplyMessage reply = new ReplyMessage(List.of(markdown(null)),
                List.of(new ReplyAction.Command("下一项", "next", false)), null, false);
        List<ResponsePlan> plans = planner.plan(context, configuration(CompositionPolicy.ALLOW_SPLIT),
                new CommandResult(CommandResultStatus.SUCCESS, List.of(reply), Map.of())).orElseThrow();

        assertThat(planner.platformMessageCount(context, plans)).isEqualTo(1);
        assertThat(plans.getFirst().actions().getFirst().presentation()).isEqualTo(Presentation.OMITTED_INFEASIBLE);
    }

    @Test
    void requiredActionCannotBeSilentlyOmittedToSatisfySingleMessage() {
        ReplyMessage reply = new ReplyMessage(List.of(markdown(null)),
                List.of(new ReplyAction.Command("必须确认", "confirm", true)), null, false);
        assertThat(planner.plan(context, configuration(CompositionPolicy.REQUIRE_SINGLE),
                new CommandResult(CommandResultStatus.SUCCESS, List.of(reply), Map.of()))).isEmpty();
    }

    @Test
    void disabledInteractionRejectsRequiredActionsButRecordsOptionalOmissions() {
        PresentationPolicy policy = new PresentationPolicy(List.of(ViewFormat.TEXT), CompositionPolicy.PREFER_SINGLE,
                InteractionPolicy.DISABLED, List.of());
        ReplyMessage reply = new ReplyMessage(List.of(new ReplyBlock.Text("body")),
                List.of(new ReplyAction.Command("下一项", "next", false)), null, false);
        List<ResponsePlan> plans = planner.plan(mockTarget(), policy, List.of(), List.of(reply)).orElseThrow();
        assertThat(plans.getFirst().text()).isEqualTo("body");
        assertThat(plans.getFirst().actions().getFirst().presentation()).isEqualTo(Presentation.OMITTED_DISABLED);
        ReplyMessage required = new ReplyMessage(reply.blocks(), List.of(new ReplyAction.Command("确认", "yes", true)), null, false);
        assertThat(planner.plan(mockTarget(), policy, List.of(), List.of(required))).isEmpty();
    }

    private ReplyTarget mockTarget() {
        ReplyTarget target = mock(ReplyTarget.class);
        when(target.supports(any())).thenReturn(true);
        when(target.canReserve(org.mockito.ArgumentMatchers.anyInt())).thenReturn(true);
        return target;
    }

    private CommandExecutionConfiguration configuration(CompositionPolicy composition) {
        return new CommandExecutionConfiguration(true, true,
                new PresentationPolicy(List.of(ViewFormat.TEXT), composition, InteractionPolicy.COMMAND_TEXT, List.of()),
                List.of(), List.of(), "", 0, true);
    }

    private CommandResult result(ReplyBlock... blocks) {
        return new CommandResult(CommandResultStatus.SUCCESS, List.of(ReplyMessage.content(List.of(blocks))), Map.of());
    }

    private ReplyBlock.Markdown markdown(String text) {
        return new ReplyBlock.Markdown(BotMarkdownMessage.content("**body**"), text);
    }
}
