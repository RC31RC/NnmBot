package com.nnm.bot.core.plugin.response;

import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.group.service.BotGroupCommandContextResolver;
import com.nnm.bot.core.group.service.BotGroupMemberIdentityResolver;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.api.CommandAccessPolicy;
import com.nnm.bot.core.plugin.model.CommandResult;
import com.nnm.bot.core.plugin.model.RelatedCommand;
import com.nnm.bot.core.plugin.model.ReplyAction;
import com.nnm.bot.core.plugin.model.RuntimeCommand;
import com.nnm.bot.core.plugin.registry.CommandRegistry;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/** 从源插件的静态关联声明生成可选导航；目标命令仍按实际身份逐项校验。 */
@Component
public class PluginCommandRelatedActionProvider implements CommandRelatedActionProvider {
    @Resource
    private CommandRegistry commands;
    @Resource
    private CommandAccessPolicy accessPolicy;
    @Resource
    private BotGroupCommandContextResolver groupContexts;
    @Resource
    private BotGroupMemberIdentityResolver groupMembers;

    @Override
    public List<ReplyAction> actions(BotMessageContext context, RuntimeCommand current, CommandResult result) {
        if (context == null || !result.successful())
            return List.of();
        List<RelatedCommand> related = current.plugin().relatedCommands(result);
        if (related.isEmpty() || !current.configuration().enabled() || !groupAvailable(context))
            return List.of();
        List<ReplyAction> actions = new ArrayList<>();
        for (RelatedCommand link : related) {
            RuntimeCommand target = commands.command(link.targetCode()).orElse(null);
            if (target == null || target.descriptor().code().equalsIgnoreCase(current.descriptor().code())
                || !target.configuration().enabled()
                || !accessPolicy.allowed(context, target.descriptor(), target.configuration()))
                continue;
            String trigger = target.descriptor().lookupTriggers().getFirst().name();
            if (target.descriptor().matchType() == MatchType.EXACT && !link.arguments().isEmpty())
                continue;
            StringBuilder text = new StringBuilder(trigger);
            for (RelatedCommand.Argument argument : link.arguments())
                text.append(' ').append(argument.value());
            if (text.length() <= 512)
                actions.add(new ReplyAction.Command(link.title(), text.toString(), false));
        }
        return List.copyOf(actions);
    }

    private boolean groupAvailable(BotMessageContext context) {
        if (!context.isGroupMessage())
            return true;
        var scope = groupContexts.resolve(context);
        return scope.isPresent() && groupMembers.findByContext(scope.get().group().getId(), context)
            .filter(member -> !member.isEnabled()).isEmpty();
    }
}
