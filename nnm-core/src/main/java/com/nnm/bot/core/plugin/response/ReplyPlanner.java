package com.nnm.bot.core.plugin.response;

import com.nnm.bot.core.plugin.model.CommandExecutionConfiguration.CommandTemplate;
import com.nnm.bot.core.plugin.model.ReplyBlock.DataView;
import com.nnm.bot.core.plugin.model.ReplyBlock.File;
import com.nnm.bot.core.plugin.model.ReplyBlock.Image;
import com.nnm.bot.core.plugin.model.ReplyBlock.Markdown;
import com.nnm.bot.core.plugin.model.ReplyBlock.Text;
import com.nnm.bot.core.config.ReplyProperties;
import com.nnm.bot.core.constant.BotChannelCapability;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.constant.BotMessageConstants.PartKind;
import com.nnm.bot.core.message.render.BotImageRenderer;
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
import com.nnm.bot.core.constant.CommandPresentationConstants.ViewFormat;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** 候选评估只读元数据；所有渲染和上传都在选定实例后进行。 */
@Component
public class ReplyPlanner {
    @Resource
    private BotMessageSenderRouter senders;
    @Resource
    private ReplyProperties limits;
    @Resource
    private ObjectProvider<BotImageRenderer> imageRendererProvider;

    public Optional<List<ResponsePlan>> plan(BotMessageContext context,
                                             CommandExecutionConfiguration configuration,
                                             CommandResult result) {
        return plan(target(context, configuration.mentionInvoker()), configuration.presentation().resolve(context.platform(), context.scope()),
                configuration.templates(), result.replies());
    }

    public Optional<List<ResponsePlan>> plan(ReplyTarget target,
                                             PresentationPolicy presentation,
                                             List<CommandTemplate> templates,
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
            List<Candidate> candidates = candidates(target, presentation, templates, replies.get(index), index);
            if (candidates.isEmpty()) return Optional.empty();
            choices.add(candidates);
        }
        // 每条消息最多只有格式与 Markdown 表示的有限组合；后缀最小条数避免贪心选择误判整组不可发。
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
            if (chosen == null) return Optional.empty();
            plans.add(chosen.plan());
            used += chosen.messages();
        }
        validateText(plans);
        return Optional.of(List.copyOf(plans));
    }

    private List<Candidate> candidates(ReplyTarget target, PresentationPolicy presentation,
                                       List<CommandTemplate> templates,
                                       ReplyMessage reply, int index) {
        if (reply.actions().size() > limits.getMaxActions()) throw new IllegalArgumentException("Reply action limit exceeded");
        InteractionPolicy interaction = presentation.interaction();
        if (interaction == InteractionPolicy.DISABLED
                && reply.actions().stream().anyMatch(ReplyAction::required)) return List.of();
        CompositionPolicy composition = reply.composition() == null
                ? presentation.composition() : reply.composition();
        List<ViewFormat> formats = reply.blocks().stream().anyMatch(ReplyBlock.DataView.class::isInstance)
                ? presentation.viewOrder().stream()
                .filter(format -> format != ViewFormat.SUMMARY_ONLY || reply.blocks().stream()
                        .filter(ReplyBlock.DataView.class::isInstance)
                        .map(ReplyBlock.DataView.class::cast)
                        .allMatch(view -> view.summary() != null))
                .toList() : List.of(ViewFormat.TEXT);
        List<Candidate> candidates = new ArrayList<>();
        for (ViewFormat format : formats) {
            if (format == ViewFormat.IMAGE && (reply.blocks().stream()
                    .filter(ReplyBlock.DataView.class::isInstance).map(ReplyBlock.DataView.class::cast)
                    .anyMatch(view -> templates.stream().noneMatch(template -> template.viewKey().equals(view.viewKey())
                            && template.html() != null && !template.html().isBlank()))
                    || imageRendererProvider.getIfAvailable() == null)) continue;
            if (format == ViewFormat.MARKDOWN && reply.blocks().stream()
                    .filter(ReplyBlock.DataView.class::isInstance).map(ReplyBlock.DataView.class::cast)
                    .anyMatch(view -> templates.stream().noneMatch(template -> template.viewKey().equals(view.viewKey())
                            && template.markdown() != null && !template.markdown().isBlank()))) continue;
            // 原生 Markdown 单独可发，不代表它与相邻正文可以同条；只尝试明确的文字表示。
            for (boolean plainMarkdown : List.of(false, true)) {
                for (List<PlannedReplyAction> actions : actionChoices(target, reply.actions(), interaction, reply.blocks(), format)) {
                    List<ReplyBlock> blocks = blocks(target, reply, actions, plainMarkdown);
                    if (blocks == null || !blocks.stream().allMatch(block -> supported(target, kind(block, format, plainMarkdown)))) continue;
                    ResponsePlan plan = new ResponsePlan(index, 0, blocks, actions,
                            target.mentionUserId(), composition, format, plainMarkdown);
                    if (actions.stream().anyMatch(action -> action.presentation() == Presentation.NATIVE)
                            && (plainMarkdown || !single(target, plan))) continue;
                    Candidate candidate = new Candidate(plan, partition(target, plan).size());
                    if (composition == CompositionPolicy.REQUIRE_SINGLE && candidate.messages() != 1) continue;
                    if (!candidates.contains(candidate)) candidates.add(candidate);
                }
            }
        }
        // 稳定排序保留同等组合下的展示顺序；PREFER_SINGLE 优先完整单条，其次才按格式拆分。
        if (composition == CompositionPolicy.PREFER_SINGLE) {
            candidates.sort(Comparator.comparing(candidate -> candidate.messages() != 1));
        }
        return List.copyOf(candidates);
    }

    private record Candidate(ResponsePlan plan, int messages) { }

    public List<ResponsePlan> compile(BotMessageContext context, List<ResponsePlan> plans) {
        return compile(target(context, false), plans);
    }

    public List<ResponsePlan> compile(ReplyTarget target, List<ResponsePlan> plans) {
        List<ResponsePlan> compiled = new ArrayList<>();
        for (ResponsePlan plan : plans) {
            if (plan.blocks().stream().anyMatch(ReplyBlock.DataView.class::isInstance)) {
                throw new IllegalStateException("Data views must be prepared before protocol compilation");
            }
            if (!plan.blocks().stream().allMatch(block -> supported(target, kind(block, plan.viewFormat(), plan.plainMarkdown())))) {
                throw new IllegalStateException("Selected conversation no longer supports required content");
            }
            if (plan.actions().stream().anyMatch(action -> action.presentation() == Presentation.NATIVE) && !single(target, plan)) {
                throw new IllegalStateException("Selected native keyboard composition is no longer supported");
            }
            if (single(target, plan)) {
                compiled.add(plan);
            } else {
                if (plan.composition() == CompositionPolicy.REQUIRE_SINGLE) {
                    throw new IllegalStateException("Prepared content cannot be sent as one platform message");
                }
                List<List<ReplyBlock>> parts = partition(target, plan);
                for (int i = 0; i < parts.size(); i++) {
                    compiled.add(new ResponsePlan(plan.logicalMessage(), i, parts.get(i),
                            i == parts.size() - 1 ? plan.actions() : List.of(),
                            i == 0 ? plan.mentionUserId() : null, plan.composition(), plan.viewFormat(), plan.plainMarkdown()));
                }
            }
        }
        validateText(compiled);
        if (compiled.size() > limits.getMaxPlatformMessages()) throw new IllegalArgumentException("Platform message limit exceeded");
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
        if (rejected.viewFormat() != ViewFormat.TEXT
                && !presentation.allowsFallback(rejected.viewFormat(), ViewFormat.TEXT)) {
            return Optional.empty();
        }

        boolean hasMarkdown = false;
        List<ReplyBlock> blocks = new ArrayList<>();
        for (ReplyBlock block : rejected.blocks()) {
            if (block instanceof ReplyBlock.Text) {
                appendText(blocks, ((ReplyBlock.Text) block).content());
            } else if (block instanceof Markdown markdown && markdown.plainText() != null) {
                hasMarkdown = true;
                appendText(blocks, markdown.plainText());
            } else {
                return Optional.empty();
            }
        }
        if (!hasMarkdown) return Optional.empty();

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
                rejected.mentionUserId(), rejected.composition(), ViewFormat.TEXT, true);
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
        if (!markdownCombinable(plan.blocks())) return false;
        if (plan.actions().stream().anyMatch(action -> action.presentation() == Presentation.NATIVE)) {
            List<PartKind> kinds = new ArrayList<>(kinds(plan.blocks(), plan.viewFormat(), plan.plainMarkdown()));
            kinds.add(PartKind.KEYBOARD);
            return target.supports(BotChannelCapability.NATIVE_KEYBOARD) && target.supportsComposition(kinds);
        }
        return single(target, plan.blocks(), plan.viewFormat(), plan.plainMarkdown());
    }

    private boolean markdownCombinable(List<ReplyBlock> blocks) {
        return blocks.size() <= 1 || blocks.stream().noneMatch(block -> block instanceof Markdown markdown
                && markdown.message().customTemplateId() != null);
    }

    private boolean single(ReplyTarget target, List<ReplyBlock> blocks, ViewFormat format, boolean plainMarkdown) {
        if (!markdownCombinable(blocks)) return false;
        List<PartKind> kinds = kinds(blocks, format, plainMarkdown);
        if (kinds.size() == 1) return true;
        return target.supportsComposition(kinds);
    }

    private List<List<ReplyBlock>> partition(ReplyTarget target, ResponsePlan plan) {
        if (single(target, plan)) return List.of(mergeText(plan.blocks()));
        List<List<ReplyBlock>> parts = new ArrayList<>();
        List<ReplyBlock> current = new ArrayList<>();
        for (ReplyBlock block : plan.blocks()) {
            List<ReplyBlock> combined = new ArrayList<>(current);
            combined.add(block);
            if (!current.isEmpty() && !single(target, combined, plan.viewFormat(), plan.plainMarkdown())) {
                parts.add(mergeText(current));
                current.clear();
            }
            current.add(block);
        }
        if (!current.isEmpty()) parts.add(mergeText(current));
        return List.copyOf(parts);
    }

    private List<ReplyBlock> blocks(ReplyTarget target, ReplyMessage reply,
                                   List<PlannedReplyAction> actions, boolean plainMarkdown) {
        List<ReplyBlock> blocks = new ArrayList<>();
        for (ReplyBlock block : reply.blocks()) {
            if (block instanceof Markdown markdown
                    && (plainMarkdown || !target.supports(BotChannelCapability.MARKDOWN_MESSAGE))) {
                if (markdown.plainText() == null) return null;
                block = new ReplyBlock.Text(markdown.plainText());
            }
            blocks.add(block);
        }
        List<String> text = actions.stream().filter(action -> action.presentation().usesText())
                .map(PlannedReplyAction::action).map(action -> action.label() + "：" + action.value()).toList();
        if (!text.isEmpty()) blocks.add(new ReplyBlock.Text(String.join("\n", text)));
        return mergeText(blocks);
    }

    private List<List<PlannedReplyAction>> actionChoices(ReplyTarget target, List<ReplyAction> actions,
            InteractionPolicy interaction, List<ReplyBlock> blocks, ViewFormat format) {
        Presentation preferred = switch (interaction) {
            case DISABLED -> Presentation.OMITTED_DISABLED;
            case COMMAND_TEXT -> Presentation.COMMAND_TEXT;
            case PREFER_NATIVE -> target.supports(BotChannelCapability.NATIVE_KEYBOARD)
                    && actions.size() <= 10 && blocks.stream().anyMatch(block -> kind(block, format, false) == PartKind.MARKDOWN)
                    && blocks.stream().allMatch(block -> kind(block, format, false) == PartKind.MARKDOWN || kind(block, format, false) == PartKind.TEXT)
                    ? Presentation.NATIVE : Presentation.TEXT_FALLBACK;
        };
        List<PlannedReplyAction> complete = actions.stream()
                .map(action -> new PlannedReplyAction(action, preferred)).toList();
        if (interaction == InteractionPolicy.DISABLED || actions.stream().allMatch(ReplyAction::required)) {
            return preferred == Presentation.NATIVE ? List.of(complete, actions.stream()
                    .map(action -> new PlannedReplyAction(action, Presentation.TEXT_FALLBACK)).toList()) : List.of(complete);
        }
        // 可选导航不能排除正文可发的实例，或耗尽正文需要的被动回复额度。
        List<PlannedReplyAction> requiredOnly = actions.stream().map(action -> new PlannedReplyAction(action,
                action.required() ? preferred : Presentation.OMITTED_INFEASIBLE)).toList();
        return preferred == Presentation.NATIVE ? List.of(complete, actions.stream()
                .map(action -> new PlannedReplyAction(action, Presentation.TEXT_FALLBACK)).toList(), requiredOnly)
                : List.of(complete, requiredOnly);
    }

    private List<ReplyBlock> mergeText(List<ReplyBlock> source) {
        List<ReplyBlock> blocks = new ArrayList<>();
        for (ReplyBlock block : source) {
            if (block instanceof Text text && !blocks.isEmpty()
                    && blocks.getLast() instanceof Text previous) {
                blocks.set(blocks.size() - 1, new ReplyBlock.Text(previous.content() + "\n" + text.content()));
            } else blocks.add(block);
        }
        return List.copyOf(blocks);
    }

    private List<PartKind> kinds(List<ReplyBlock> blocks, ViewFormat format, boolean plainMarkdown) {
        List<PartKind> kinds = new ArrayList<>();
        for (ReplyBlock block : blocks) {
            PartKind kind = kind(block, format, plainMarkdown);
            if (kind != PartKind.TEXT || kinds.isEmpty() || kinds.getLast() != PartKind.TEXT) kinds.add(kind);
        }
        return kinds;
    }

    private PartKind kind(ReplyBlock block, ViewFormat format, boolean plainMarkdown) {
        return switch (block) {
            case Text ignored -> PartKind.TEXT;
            case DataView<?> ignored -> switch (format) {
                case IMAGE -> PartKind.IMAGE;
                case MARKDOWN -> plainMarkdown ? PartKind.TEXT : PartKind.MARKDOWN;
                default -> PartKind.TEXT;
            };
            case Image ignored -> PartKind.IMAGE;
            case File ignored -> PartKind.FILE;
            case Markdown ignored -> PartKind.MARKDOWN;
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
                    case Markdown markdown -> markdown.message().content() == null
                            ? 0 : markdown.message().content().length();
                    case DataView<?> view -> plan.viewFormat() == ViewFormat.SUMMARY_ONLY
                            && view.summary() != null ? view.summary().length() : 0;
                    default -> 0;
                };
            }
        }
        if (chars > limits.getMaxTextCharacters()) throw new IllegalArgumentException("Reply text limit exceeded");
    }
}
