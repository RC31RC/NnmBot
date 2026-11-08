package com.nnm.bot.core.plugin.response;

import com.nnm.bot.common.exception.SystemException;
import com.nnm.bot.core.constant.CommandExecutionConstants.CommandErrorCode;
import com.nnm.bot.core.plugin.model.ReplyBlock.File;
import com.nnm.bot.core.plugin.model.ReplyBlock.Image;
import com.nnm.bot.core.plugin.model.ReplyBlock.Markdown;
import com.nnm.bot.core.plugin.model.ReplyBlock.Text;
import com.nnm.bot.core.config.ReplyProperties;
import com.nnm.bot.core.constant.BotChannelCapability;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.constant.BotMessageConstants.PartKind;
import com.nnm.bot.core.message.sender.BotMessageSenderRouter;
import com.nnm.bot.core.plugin.model.CommandExecutionConfiguration;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.constant.CommandPresentationConstants.CompositionPolicy;
import com.nnm.bot.core.constant.CommandPresentationConstants.InteractionPolicy;
import com.nnm.bot.core.plugin.model.PresentationPolicy;
import com.nnm.bot.core.plugin.model.PlannedReplyAction;
import com.nnm.bot.core.constant.CommandDeliveryConstants.Presentation;
import com.nnm.bot.core.plugin.model.ReplyAction;
import com.nnm.bot.core.plugin.model.ReplyBlock;
import com.nnm.bot.core.plugin.model.ReplyMessage;
import com.nnm.bot.core.plugin.model.ResponsePlan;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import com.nnm.bot.core.message.model.BotMarkdownMessage;

/** 候选评估只读元数据；所有渲染和上传都在选定实例后进行。 */
@Component
public class ReplyPlanner {
    @Resource
    private BotMessageSenderRouter senders;
    @Resource
    private ReplyProperties limits;

    public Optional<List<ResponsePlan>> plan(BotMessageContext context, CommandExecutionConfiguration configuration,
        CommandResult result) {
        ReplyTarget target = target(context, configuration.mentionInvoker());
        PresentationPolicy presentation = PresentationPolicy.defaults();
        // 在文字降级移除按钮元数据前校验总量，防止未获准账号绕过回复预算。
        int buttonCount = result.blocks().stream().filter(Markdown.class::isInstance).map(Markdown.class::cast)
            .mapToInt(block -> block.message().buttons().size()).sum();
        if (buttonCount > limits.getMaxActions())
            throw new IllegalArgumentException("Markdown action limit exceeded");
        List<ReplyBlock> blocks = adaptMarkdownButtons(target, result.blocks(), presentation.interaction());
        if (blocks.stream().anyMatch(ReplyBlock.HtmlData.class::isInstance))
            throw new SystemException(CommandErrorCode.DELIVERY_PLAN_INVALID,
                "HTML data must be rendered before reply planning");
        ReplyMessage reply = new ReplyMessage(blocks, List.of(), CompositionPolicy.REQUIRE_SINGLE);
        return plan(target, presentation, List.of(reply));
    }

    public Optional<List<ResponsePlan>> plan(ReplyTarget target, PresentationPolicy presentation,
        List<ReplyMessage> replies) {
        if (replies.size() > limits.getMaxLogicalMessages()
            || replies.stream().mapToInt(reply -> reply.blocks().size()).sum() > limits.getMaxBlocks()) {
            throw new IllegalArgumentException("Reply message/block limit exceeded");
        }
        if (replies.stream().mapToInt(reply -> reply.actions().size()).sum() > limits.getMaxActions()) {
            throw new IllegalArgumentException("Reply action limit exceeded");
        }
        List<List<Candidate>> choices = new ArrayList<>();
        for (int index = 0; index < replies.size(); index++) {
            List<Candidate> candidates = candidates(target, presentation, replies.get(index), index);
            if (candidates.isEmpty())
                return Optional.empty();
            choices.add(candidates);
        }
        // 后缀最小条数避免贪心选择误判整组不可发。
        int[] minimumRemaining = new int[choices.size() + 1];
        for (int index = choices.size() - 1; index >= 0; index--) {
            minimumRemaining[index] = minimumRemaining[index + 1]
                + choices.get(index).stream().mapToInt(Candidate::messages).min().orElseThrow();
        }
        List<ResponsePlan> plans = new ArrayList<>();
        int used = 0;
        for (int index = 0; index < choices.size(); index++) {
            Candidate chosen = null;
            for (Candidate candidate : choices.get(index)) {
                int required = used + candidate.messages() + minimumRemaining[index + 1];
                if (required <= limits.getMaxPlatformMessages() && target.canReserve(required)) {
                    chosen = candidate;
                    break;
                }
            }
            if (chosen == null)
                return Optional.empty();
            plans.add(chosen.plan());
            used += chosen.messages();
        }
        validateText(plans);
        return Optional.of(List.copyOf(plans));
    }

    private List<Candidate> candidates(ReplyTarget target, PresentationPolicy presentation, ReplyMessage reply,
        int index) {
        if (reply.actions().size() > limits.getMaxActions())
            throw new IllegalArgumentException("Reply action limit exceeded");
        int markdownButtons = reply.blocks().stream().filter(Markdown.class::isInstance).map(Markdown.class::cast)
            .mapToInt(block -> block.message().buttons().size()).sum();
        if (markdownButtons > limits.getMaxActions())
            throw new IllegalArgumentException("Markdown action limit exceeded");
        InteractionPolicy interaction = presentation.interaction();
        if (interaction == InteractionPolicy.DISABLED && reply.actions().stream().anyMatch(ReplyAction::required))
            return List.of();
        CompositionPolicy composition =
            reply.composition() == null ? CompositionPolicy.REQUIRE_SINGLE : reply.composition();
        List<Candidate> candidates = new ArrayList<>();
        boolean hasMarkdown = reply.blocks().stream().anyMatch(Markdown.class::isInstance);
        for (boolean plainMarkdown : hasMarkdown ? List.of(false, true) : List.of(false)) {
            for (List<PlannedReplyAction> actions : actionChoices(target, reply.actions(), interaction, reply.blocks(),
                plainMarkdown)) {
                List<ReplyBlock> blocks = blocks(reply, actions, plainMarkdown);
                List<PartKind> kinds = kinds(target, blocks, actions, plainMarkdown);
                if (!kinds.stream().allMatch(kind -> supported(target, kind))
                    || !plainMarkdown && !keyboardFits(blocks, actions))
                    continue;
                ResponsePlan plan =
                    new ResponsePlan(index, 0, blocks, actions, target.mentionUserId(), composition, plainMarkdown);
                if (actions.stream().anyMatch(action -> action.presentation() == Presentation.NATIVE)
                    && !single(target, plan))
                    continue;
                Candidate candidate = new Candidate(plan, partition(target, plan).size());
                if (composition == CompositionPolicy.REQUIRE_SINGLE && candidate.messages() != 1)
                    continue;
                if (!candidates.contains(candidate))
                    candidates.add(candidate);
            }
        }
        // 稳定排序保留同等组合下的展示顺序；PREFER_SINGLE 优先完整单条，其次才按格式拆分。
        if (composition == CompositionPolicy.PREFER_SINGLE) {
            candidates.sort(Comparator.comparing(candidate -> candidate.messages() != 1));
        }
        return List.copyOf(candidates);
    }

    private record Candidate(ResponsePlan plan, int messages) {
    }

    public List<ResponsePlan> compile(BotMessageContext context, List<ResponsePlan> plans) {
        return compile(target(context, false), plans);
    }

    public List<ResponsePlan> compile(ReplyTarget target, List<ResponsePlan> plans) {
        List<ResponsePlan> compiled = new ArrayList<>();
        for (ResponsePlan plan : plans) {
            if (plan.blocks().stream().anyMatch(ReplyBlock.HtmlData.class::isInstance)) {
                throw new SystemException(CommandErrorCode.DELIVERY_PLAN_INVALID,
                    "HTML data must be rendered before protocol compilation");
            }
            if (!kinds(target, plan.blocks(), plan.actions(), plan.plainMarkdown()).stream()
                .allMatch(kind -> supported(target, kind))) {
                throw new SystemException(CommandErrorCode.REPLY_CAPABILITY_CHANGED,
                    "Selected conversation no longer supports required content");
            }
            if (plan.actions().stream().anyMatch(action -> action.presentation() == Presentation.NATIVE)
                && !single(target, plan)) {
                throw new SystemException(CommandErrorCode.REPLY_CAPABILITY_CHANGED,
                    "Selected native keyboard composition is no longer supported");
            }
            if (single(target, plan)) {
                compiled.add(plan);
            } else {
                if (plan.composition() == CompositionPolicy.REQUIRE_SINGLE) {
                    throw new SystemException(CommandErrorCode.DELIVERY_PLAN_INVALID,
                        "Prepared content cannot be sent as one platform message");
                }
                List<List<ReplyBlock>> parts = partition(target, plan);
                for (int i = 0; i < parts.size(); i++) {
                    compiled.add(new ResponsePlan(plan.logicalMessage(), i, parts.get(i),
                        i == parts.size() - 1 ? plan.actions() : List.of(), i == 0 ? plan.mentionUserId() : null,
                        plan.composition(), plan.plainMarkdown()));
                }
            }
        }
        validateText(compiled);
        if (compiled.size() > limits.getMaxPlatformMessages())
            throw new IllegalArgumentException("Platform message limit exceeded");
        return List.copyOf(compiled);
    }

    public int platformMessageCount(BotMessageContext context, List<ResponsePlan> plans) {
        return platformMessageCount(target(context, false), plans);
    }

    public int platformMessageCount(ReplyTarget target, List<ResponsePlan> plans) {
        return plans.stream().mapToInt(plan -> partition(target, plan).size()).sum();
    }

    public Optional<ResponsePlan> explicitTextFallback(BotMessageContext context, ResponsePlan rejected,
        PresentationPolicy presentation) {
        if (rejected.plainMarkdown() || presentation.interaction() == InteractionPolicy.DISABLED) {
            return Optional.empty();
        }
        boolean hasMarkdown = false;
        List<ReplyBlock> blocks = new ArrayList<>();
        for (ReplyBlock block : rejected.blocks()) {
            if (block instanceof ReplyBlock.Text) {
                appendText(blocks, ((ReplyBlock.Text)block).content());
            } else if (block instanceof Markdown markdown) {
                hasMarkdown = true;
                appendText(blocks, markdown.plainTextContent());
                markdown.message().buttons()
                    .forEach(button -> appendText(blocks, button.label() + "：" + button.value()));
            } else {
                return Optional.empty();
            }
        }
        if (!hasMarkdown)
            return Optional.empty();

        List<PlannedReplyAction> actions = new ArrayList<>();
        for (PlannedReplyAction action : rejected.actions()) {
            if (action.presentation() == Presentation.NATIVE) {
                actions.add(new PlannedReplyAction(action.action(), Presentation.TEXT_FALLBACK));
                appendText(blocks, action.action().label() + "：" + action.action().value());
            } else {
                actions.add(action);
            }
        }
        ReplyTarget target = target(context, false);
        if (!target.supports(BotChannelCapability.TEXT_MESSAGE) || !target.canReserve(1)) {
            return Optional.empty();
        }
        ResponsePlan fallback = new ResponsePlan(rejected.logicalMessage(), rejected.part(), blocks, actions,
            rejected.mentionUserId(), rejected.composition(), true);
        try {
            validateText(List.of(fallback));
        } catch (IllegalArgumentException overLimit) {
            return Optional.empty();
        }
        return Optional.of(fallback);
    }

    private void appendText(List<ReplyBlock> blocks, String value) {
        if (!blocks.isEmpty() && blocks.getLast() instanceof Text previous) {
            blocks.set(blocks.size() - 1, new ReplyBlock.Text(previous.content() + "\n" + value));
        } else {
            blocks.add(new ReplyBlock.Text(value));
        }
    }

    private boolean single(ReplyTarget target, ResponsePlan plan) {
        if (!markdownCombinable(plan.blocks()))
            return false;
        if (plan.actions().stream().anyMatch(action -> action.presentation() == Presentation.NATIVE)) {
            List<PartKind> kinds = new ArrayList<>(kinds(target, plan.blocks(), plan.actions(), plan.plainMarkdown()));
            if (!kinds.contains(PartKind.KEYBOARD))
                kinds.add(PartKind.KEYBOARD);
            return target.supports(BotChannelCapability.NATIVE_KEYBOARD) && target.supportsComposition(kinds);
        }
        return single(target, plan.blocks(), plan.actions(), plan.plainMarkdown());
    }

    private boolean markdownCombinable(List<ReplyBlock> blocks) {
        return blocks.size() <= 1 || blocks.stream()
            .noneMatch(block -> block instanceof Markdown markdown && markdown.message().customTemplateId() != null);
    }

    private boolean single(ReplyTarget target, List<ReplyBlock> blocks, List<PlannedReplyAction> actions,
        boolean plainMarkdown) {
        if (!markdownCombinable(blocks))
            return false;
        List<PartKind> kinds = kinds(target, blocks, actions, plainMarkdown);
        if (kinds.size() == 1)
            return true;
        return target.supportsComposition(kinds);
    }

    private List<List<ReplyBlock>> partition(ReplyTarget target, ResponsePlan plan) {
        if (single(target, plan))
            return List.of(mergeText(plan.blocks()));
        List<List<ReplyBlock>> parts = new ArrayList<>();
        List<ReplyBlock> current = new ArrayList<>();
        for (ReplyBlock block : plan.blocks()) {
            List<ReplyBlock> combined = new ArrayList<>(current);
            combined.add(block);
            if (!current.isEmpty() && !single(target, combined, List.of(), plan.plainMarkdown())) {
                parts.add(mergeText(current));
                current.clear();
            }
            current.add(block);
        }
        if (!current.isEmpty())
            parts.add(mergeText(current));
        return List.copyOf(parts);
    }

    private List<ReplyBlock> blocks(ReplyMessage reply, List<PlannedReplyAction> actions, boolean plainMarkdown) {
        List<ReplyBlock> blocks = new ArrayList<>();
        for (ReplyBlock block : reply.blocks()) {
            if (block instanceof Markdown markdown && plainMarkdown) {
                String text = markdown.plainTextContent();
                block = new Markdown(BotMarkdownMessage.content(text).withButtons(markdown.message().buttons()), text);
            }
            blocks.add(block);
        }
        List<String> text = actions.stream().filter(action -> action.presentation().usesText())
            .map(PlannedReplyAction::action).map(action -> action.label() + "：" + action.value()).toList();
        if (!text.isEmpty())
            blocks.add(new ReplyBlock.Text(String.join("\n", text)));
        return mergeText(blocks);
    }

    private List<ReplyBlock> adaptMarkdownButtons(ReplyTarget target, List<ReplyBlock> source,
        InteractionPolicy interaction) {
        List<ReplyBlock> blocks = new ArrayList<>(source.size());
        for (ReplyBlock block : source) {
            if (!(block instanceof Markdown markdown) || markdown.message().buttons().isEmpty()) {
                blocks.add(block);
                continue;
            }
            if (interaction == InteractionPolicy.DISABLED) {
                blocks.add(new Markdown(markdown.message().withoutButtons(), markdown.plainText()));
                continue;
            }
            boolean nativeButtons =
                interaction == InteractionPolicy.PREFER_NATIVE && target.supports(BotChannelCapability.NATIVE_KEYBOARD)
                    && target.supportsComposition(withKeyboard(kinds(target, source, List.of(), false)))
                    && keyboardFits(source, List.of());
            if (nativeButtons) {
                blocks.add(block);
                continue;
            }
            String actions = markdown.message().buttons().stream().map(button -> button.label() + "：" + button.value())
                .reduce((left, right) -> left + "\n" + right).orElse("");
            String content =
                markdown.message().content() == null ? markdown.plainTextContent() : markdown.message().content();
            String plainText = markdown.plainTextContent();
            blocks.add(new Markdown(BotMarkdownMessage.content(content + "\n" + actions), plainText + "\n" + actions));
        }
        return List.copyOf(blocks);
    }

    /** QQ 按块顺延键盘行；规划时先检查整条消息，不能留到上传后才发现超过五行。 */
    private boolean keyboardFits(List<ReplyBlock> blocks, List<PlannedReplyAction> actions) {
        int rows = 0;
        int count = 0;
        for (ReplyBlock block : blocks) {
            if (block instanceof Markdown markdown && !markdown.message().buttons().isEmpty()) {
                rows += markdown.message().buttons().getLast().row() + 1;
                count += markdown.message().buttons().size();
            }
        }
        int nativeActions =
            (int)actions.stream().filter(action -> action.presentation() == Presentation.NATIVE).count();
        return rows + (nativeActions + 1) / 2 <= 5 && count + nativeActions <= 25;
    }

    private List<List<PlannedReplyAction>> actionChoices(ReplyTarget target, List<ReplyAction> actions,
        InteractionPolicy interaction, List<ReplyBlock> blocks, boolean plainMarkdown) {
        Presentation preferred = switch (interaction) {
            case DISABLED -> Presentation.OMITTED_DISABLED;
            case COMMAND_TEXT -> Presentation.COMMAND_TEXT;
            case PREFER_NATIVE ->
                target.supports(BotChannelCapability.NATIVE_KEYBOARD) && actions.size() <= limits.getMaxActions()
                    && target.supportsComposition(withKeyboard(kinds(target, blocks, List.of(), plainMarkdown)))
                        ? Presentation.NATIVE : Presentation.TEXT_FALLBACK;
        };
        List<PlannedReplyAction> complete =
            actions.stream().map(action -> new PlannedReplyAction(action, preferred)).toList();
        if (interaction == InteractionPolicy.DISABLED || actions.stream().allMatch(ReplyAction::required)) {
            return preferred == Presentation.NATIVE
                ? List.of(complete,
                    actions.stream().map(action -> new PlannedReplyAction(action, Presentation.TEXT_FALLBACK)).toList())
                : List.of(complete);
        }
        // 可选导航不能排除正文可发的实例，或耗尽正文需要的被动回复额度。
        List<PlannedReplyAction> requiredOnly = actions.stream().map(
            action -> new PlannedReplyAction(action, action.required() ? preferred : Presentation.OMITTED_INFEASIBLE))
            .toList();
        return preferred == Presentation.NATIVE ? List.of(complete,
            actions.stream().map(action -> new PlannedReplyAction(action, Presentation.TEXT_FALLBACK)).toList(),
            requiredOnly) : List.of(complete, requiredOnly);
    }

    private List<ReplyBlock> mergeText(List<ReplyBlock> source) {
        List<ReplyBlock> blocks = new ArrayList<>();
        for (ReplyBlock block : source) {
            if (block instanceof Text text && !blocks.isEmpty() && blocks.getLast() instanceof Text previous) {
                blocks.set(blocks.size() - 1, new ReplyBlock.Text(previous.content() + "\n" + text.content()));
            } else
                blocks.add(block);
        }
        return List.copyOf(blocks);
    }

    private List<PartKind> kinds(ReplyTarget target, List<ReplyBlock> blocks, List<PlannedReplyAction> actions,
        boolean plainMarkdown) {
        List<PartKind> kinds = new ArrayList<>();
        for (ReplyBlock block : blocks) {
            PartKind kind = kind(block, plainMarkdown);
            if (kind != PartKind.TEXT || kinds.isEmpty() || kinds.getLast() != PartKind.TEXT)
                kinds.add(kind);
            if (!plainMarkdown && target.supports(BotChannelCapability.NATIVE_KEYBOARD)
                && block instanceof Markdown markdown && !markdown.message().buttons().isEmpty()
                && (kinds.isEmpty() || kinds.getLast() != PartKind.KEYBOARD))
                kinds.add(PartKind.KEYBOARD);
        }
        if (actions.stream().anyMatch(action -> action.presentation() == Presentation.NATIVE)
            && !kinds.contains(PartKind.KEYBOARD))
            kinds.add(PartKind.KEYBOARD);
        return kinds;
    }

    private List<PartKind> withKeyboard(List<PartKind> kinds) {
        List<PartKind> result = new ArrayList<>(kinds);
        if (!result.contains(PartKind.KEYBOARD))
            result.add(PartKind.KEYBOARD);
        return result;
    }

    private PartKind kind(ReplyBlock block, boolean plainMarkdown) {
        return switch (block) {
            case Text ignored -> PartKind.TEXT;
            case ReplyBlock.HtmlData<?> ignored ->
                throw new SystemException(CommandErrorCode.DELIVERY_PLAN_INVALID, "Unrendered HTML data");
            case Image ignored -> PartKind.IMAGE;
            case File ignored -> PartKind.FILE;
            case Markdown ignored -> plainMarkdown ? PartKind.TEXT : PartKind.MARKDOWN;
        };
    }

    private boolean supported(ReplyTarget target, PartKind kind) {
        return target.supports(switch (kind) {
            case TEXT -> BotChannelCapability.TEXT_MESSAGE;
            case IMAGE -> BotChannelCapability.IMAGE_MESSAGE;
            case MARKDOWN -> BotChannelCapability.MARKDOWN_MESSAGE;
            case KEYBOARD -> BotChannelCapability.NATIVE_KEYBOARD;
            case FILE -> BotChannelCapability.FILE_MESSAGE;
        });
    }

    private ReplyTarget target(BotMessageContext context, boolean mentionInvoker) {
        return new ReplyTarget() {
            @Override
            public boolean supports(BotChannelCapability capability) {
                return senders.supports(context, capability);
            }

            @Override
            public boolean supportsComposition(List<PartKind> kinds) {
                return senders.supportsComposition(context, kinds);
            }

            @Override
            public boolean canReserve(int messages) {
                return senders.canReserve(context, messages);
            }

            @Override
            public String mentionUserId() {
                return mentionInvoker && context.isGroupMessage() ? context.senderId() : null;
            }
        };
    }

    private void validateText(List<ResponsePlan> plans) {
        long chars = 0;
        for (ResponsePlan plan : plans) {
            for (ReplyBlock block : plan.blocks()) {
                chars += switch (block) {
                    case Text text -> text.content().length();
                    case Markdown markdown -> markdown.plainTextContent().length() + markdown.message().buttons()
                        .stream().mapToInt(button -> button.label().length() + button.value().length()).sum();
                    default -> 0;
                };
            }
        }
        if (chars > limits.getMaxTextCharacters())
            throw new IllegalArgumentException("Reply text limit exceeded");
    }
}
