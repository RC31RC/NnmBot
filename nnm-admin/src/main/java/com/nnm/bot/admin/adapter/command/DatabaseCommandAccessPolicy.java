package com.nnm.bot.admin.adapter.command;

import com.nnm.bot.admin.domain.BotCommandRobotRole;
import com.nnm.bot.admin.domain.BotCommandRole;
import com.nnm.bot.admin.domain.BotRobotRole;
import java.util.stream.Collectors;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.nnm.bot.admin.domain.BotCommand;
import com.nnm.bot.admin.domain.SysUser;
import com.nnm.bot.admin.repository.BotChannelInstanceRepository;
import com.nnm.bot.admin.repository.BotCommandRepository;
import com.nnm.bot.admin.repository.BotCommandRoleRepository;
import com.nnm.bot.admin.repository.BotCommandRobotRoleRepository;
import com.nnm.bot.admin.repository.BotRobotRoleInstanceRepository;
import com.nnm.bot.admin.repository.BotRobotRoleRepository;
import com.nnm.bot.admin.repository.SysUserIdentityRepository;
import com.nnm.bot.admin.repository.SysUserRepository;
import com.nnm.bot.core.channel.port.BotChannelInstanceIdentityResolver;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.api.CommandAccessPolicy;
import com.nnm.bot.core.plugin.model.CommandAccessDecision;
import com.nnm.bot.core.plugin.model.CommandDescriptor;
import com.nnm.bot.core.plugin.model.CommandExecutionConfiguration;
import com.nnm.bot.core.plugin.system.SystemCommandCodes;
import com.nnm.bot.core.group.repository.BotGroupBotRepository;
import com.nnm.bot.core.group.repository.BotGroupRepository;
import com.nnm.bot.core.group.domain.BotGroup;
import com.nnm.bot.core.group.domain.BotGroupBot;
import com.nnm.bot.core.group.domain.BotGroupMember;
import com.nnm.bot.core.group.service.BotGroupMemberIdentityResolver;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * 命令执行权限策略。
 *
 * <p>机器人能力角色、系统角色和逻辑群组角色是三套独立关系：
 * 能力角色决定机器人能不能提供命令，系统角色和群组角色决定当前用户能不能使用命令。</p>
 */
@Component
@Slf4j
public class DatabaseCommandAccessPolicy implements CommandAccessPolicy {

    private final BotCommandRepository commandRepository;
    private final BotCommandRobotRoleRepository commandRoleRepository;
    private final BotRobotRoleInstanceRepository roleInstanceRepository;
    private final BotRobotRoleRepository roleRepository;
    private final BotChannelInstanceRepository instanceRepository;
    private final BotCommandRoleRepository systemRoleBindingRepository;
    private final SysUserIdentityRepository identityRepository;
    private final SysUserRepository userRepository;
    private final BotChannelInstanceIdentityResolver instanceIdentityResolver;
    private final BotGroupBotRepository groupBotRepository;
    private final BotGroupRepository groupRepository;
    private final BotGroupMemberIdentityResolver groupMemberIdentityResolver;

    public DatabaseCommandAccessPolicy(
            BotCommandRepository commandRepository,
            BotCommandRobotRoleRepository commandRoleRepository,
            BotRobotRoleInstanceRepository roleInstanceRepository,
            BotRobotRoleRepository roleRepository,
            BotChannelInstanceRepository instanceRepository,
            BotCommandRoleRepository systemRoleBindingRepository,
            SysUserIdentityRepository identityRepository,
            SysUserRepository userRepository,
            BotChannelInstanceIdentityResolver instanceIdentityResolver,
            BotGroupBotRepository groupBotRepository,
            BotGroupRepository groupRepository,
            BotGroupMemberIdentityResolver groupMemberIdentityResolver) {
        this.commandRepository = commandRepository;
        this.commandRoleRepository = commandRoleRepository;
        this.roleInstanceRepository = roleInstanceRepository;
        this.roleRepository = roleRepository;
        this.instanceRepository = instanceRepository;
        this.systemRoleBindingRepository = systemRoleBindingRepository;
        this.identityRepository = identityRepository;
        this.userRepository = userRepository;
        this.instanceIdentityResolver = instanceIdentityResolver;
        this.groupBotRepository = groupBotRepository;
        this.groupRepository = groupRepository;
        this.groupMemberIdentityResolver = groupMemberIdentityResolver;
    }

    @Override
    public CommandAccessDecision check(BotMessageContext context,
                                        CommandDescriptor descriptor,
                                        CommandExecutionConfiguration configuration) {
        BotCommand command = this.commandRepository.findByCodeIgnoreCase(descriptor.code())
                .orElse(null);
        if (ObjectUtil.isNull(command)) {
            String commandCode = descriptor.code().strip().toLowerCase(Locale.ROOT);
            return SystemCommandCodes.ALL.contains(commandCode)
                    ? CommandAccessDecision.permit()
                    : CommandAccessDecision.denied("当前业务命令未完成数据库配置");
        }

        CommandAccessDecision instanceDecision = this.checkBotInstance(context, command);
        if (!instanceDecision.allowed()) {
            return instanceDecision;
        }

        CommandAccessDecision systemDecision = this.checkSystemRole(context, command);
        if (!systemDecision.allowed()) {
            return systemDecision;
        }

        return this.checkGroupRole(context, command);
    }

    private CommandAccessDecision checkBotInstance(BotMessageContext context,
                                                    BotCommand command) {
        // 系统命令是每个实例的内置能力，不受机器人能力角色绑定影响。
        if (SystemCommandCodes.ALL.contains(command.getCode().toLowerCase(Locale.ROOT))) {
            return CommandAccessDecision.permit();
        }
        List<BotCommandRobotRole> bindings =
                this.commandRoleRepository.findAllByCommandId(command.getId());
        if (bindings.isEmpty()) {
            log.debug("机器人能力角色校验拒绝：commandCode={}, commandId={}, platform={}, botAccountId={}, "
                            + "reason=未配置机器人能力角色",
                    command.getCode(), command.getId(), context.platform(), context.botAccountId());
            return CommandAccessDecision.denied("当前命令未配置机器人能力角色");
        }
        var instance = this.instanceRepository.findByPlatformAndBotAccountId(
                context.platform(), context.botAccountId()).orElse(null);
        if (ObjectUtil.isNull(instance)) {
            log.debug("机器人能力角色校验拒绝：commandCode={}, commandId={}, platform={}, botAccountId={}, "
                            + "bindingCount={}, reason=机器人实例不存在",
                    command.getCode(), command.getId(), context.platform(), context.botAccountId(),
                    bindings.size());
            return CommandAccessDecision.denied("当前机器人实例不存在");
        }
        Set<Long> enabledRoleIds = bindings.stream()
                .map(BotCommandRobotRole::getRobotRoleId)
                .filter(roleId -> this.roleRepository.findById(roleId)
                        .map(BotRobotRole::isEnabled)
                        .orElse(false))
                .collect(Collectors.toSet());
        Set<Long> boundRoleIds = enabledRoleIds.stream()
                .filter(roleId -> this.roleInstanceRepository.findAllByRobotRoleId(roleId).stream()
                        .anyMatch(item -> item.getBotInstanceId().equals(instance.getId())))
                .collect(Collectors.toSet());
        boolean allowed = !boundRoleIds.isEmpty();
        log.debug("机器人能力角色校验：commandCode={}, commandId={}, platform={}, botAccountId={}, "
                        + "bindingCount={}, instanceId={}, enabledRoleIds={}, boundRoleIds={}, allowed={}",
                command.getCode(), command.getId(), context.platform(), context.botAccountId(),
                bindings.size(), instance.getId(), enabledRoleIds, boundRoleIds, allowed);
        return allowed
                ? CommandAccessDecision.permit()
                : CommandAccessDecision.denied("当前机器人未绑定该命令能力");
    }

    private CommandAccessDecision checkSystemRole(BotMessageContext context,
                                                   BotCommand command) {
        List<BotCommandRole> bindings =
                this.systemRoleBindingRepository.findAllByCommandId(command.getId());
        if (bindings.isEmpty()) {
            return CommandAccessDecision.permit();
        }

        Optional<SysUser> user = this.resolveSystemUser(context);
        if (user.isEmpty() || !user.get().isEnabled()) {
            // QQ 官方通常只能提供 OpenID；没有绑定该 OpenID 时不能冒充系统管理员。
            return CommandAccessDecision.denied("当前平台身份未绑定启用的系统用户");
        }
        Set<Long> requiredRoleIds = bindings.stream()
                .map(BotCommandRole::getRoleId)
                .collect(Collectors.toSet());
        boolean allowed = user.get().getRoles().stream()
                .anyMatch(role -> role.isEnabled() && requiredRoleIds.contains(role.getId()));
        return allowed
                ? CommandAccessDecision.permit()
                : CommandAccessDecision.denied("当前系统用户没有该命令所需角色");
    }

    private CommandAccessDecision checkGroupRole(BotMessageContext context,
                                                  BotCommand command) {
        String requiredRole = normalizeRole(command.getGroupRoleRequired());
        if (StrUtil.isBlank(requiredRole)) {
            return CommandAccessDecision.permit();
        }
        if (!context.isGroupMessage()) {
            return CommandAccessDecision.denied("该命令只能在群组作用域中使用");
        }

        BotGroupMember member = this.resolveGroupMember(context);
        if (ObjectUtil.isNull(member) || !member.isEnabled()) {
            return CommandAccessDecision.denied("当前用户不是已启用的逻辑群组成员");
        }
        boolean allowed = "OWNER".equals(member.getMemberRole())
                || "ADMIN".equals(requiredRole) && "ADMIN".equals(member.getMemberRole());
        return allowed
                ? CommandAccessDecision.permit()
                : CommandAccessDecision.denied("当前群组成员没有该命令所需角色");
    }

    private Optional<SysUser> resolveSystemUser(BotMessageContext context) {
        return this.identityRepository
                .findFirstByPlatformInAndExternalUserIdOrderByIdAsc(
                        this.identityPlatforms(context), context.senderId())
                .flatMap(identity -> this.userRepository.findWithRolesById(identity.getUserId()));
    }

    private BotGroupMember resolveGroupMember(BotMessageContext context) {
        var instanceId = this.instanceIdentityResolver.resolveId(
                context.platform(), context.botAccountId());
        if (instanceId.isEmpty()) {
            return null;
        }
        BotGroupBot binding = this.groupBotRepository
                .findByPlatformAndScopeTypeAndScopeKeyAndBotInstanceId(
                        context.platform().name(), "GROUP", context.conversationId(),
                        instanceId.getAsLong())
                .filter(BotGroupBot::isEnabled)
                .orElse(null);
        if (ObjectUtil.isNull(binding)) {
            return null;
        }
        BotGroup group = this.groupRepository.findById(binding.getGroupId())
                .filter(BotGroup::isEnabled)
                .orElse(null);
        if (ObjectUtil.isNull(group)) {
            return null;
        }
        return this.groupMemberIdentityResolver
                .findOrCreateForCommand(group.getId(), context)
                .orElse(null);
    }

    private List<String> identityPlatforms(BotMessageContext context) {
        if (context.platform() == BotPlatform.ONEBOT
                || context.platform() == BotPlatform.QQ_OFFICIAL) {
            return List.of(context.platform().name(), "QQ");
        }
        return List.of(context.platform().name());
    }

    private String normalizeRole(String role) {
        return StrUtil.isBlank(role) ? null : role.trim().toUpperCase(Locale.ROOT);
    }
}
