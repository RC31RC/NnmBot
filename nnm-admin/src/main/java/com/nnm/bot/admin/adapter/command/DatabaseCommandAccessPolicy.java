package com.nnm.bot.admin.adapter.command;

import java.util.OptionalLong;
import com.nnm.bot.admin.pojo.entity.bot.command.BotCommandRobotRole;
import com.nnm.bot.admin.pojo.entity.bot.command.BotCommandRole;
import com.nnm.bot.admin.pojo.entity.bot.role.BotRobotRole;
import java.util.stream.Collectors;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.nnm.bot.admin.pojo.entity.bot.command.BotCommand;
import com.nnm.bot.admin.pojo.entity.system.SysUser;
import com.nnm.bot.admin.repository.bot.command.BotCommandRepository;
import com.nnm.bot.admin.repository.bot.command.BotCommandRoleRepository;
import com.nnm.bot.admin.repository.bot.command.BotCommandRobotRoleRepository;
import com.nnm.bot.admin.repository.bot.role.BotRobotRoleInstanceRepository;
import com.nnm.bot.admin.repository.bot.role.BotRobotRoleRepository;
import com.nnm.bot.admin.repository.system.SysUserIdentityRepository;
import com.nnm.bot.admin.repository.system.SysUserRepository;
import com.nnm.bot.core.channel.port.BotChannelInstanceIdentityResolver;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.message.model.BotMessageContext;
import com.nnm.bot.core.plugin.api.CommandAccessPolicy;
import com.nnm.bot.core.plugin.model.CommandAccessDecision;
import com.nnm.bot.core.plugin.model.CommandDescriptor;
import com.nnm.bot.core.plugin.model.CommandExecutionConfiguration;
import com.nnm.bot.core.plugin.system.SystemCommandCodes;
import com.nnm.bot.core.group.domain.BotGroupMember;
import com.nnm.bot.core.group.service.BotGroupMemberIdentityResolver;
import com.nnm.bot.core.group.service.BotGroupCommandContextResolver;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import jakarta.annotation.Resource;
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

    @Resource
    private BotCommandRepository commandRepository;
    @Resource
    private BotCommandRobotRoleRepository commandRoleRepository;
    @Resource
    private BotRobotRoleInstanceRepository roleInstanceRepository;
    @Resource
    private BotRobotRoleRepository roleRepository;
    @Resource
    private BotCommandRoleRepository systemRoleBindingRepository;
    @Resource
    private SysUserIdentityRepository identityRepository;
    @Resource
    private SysUserRepository userRepository;
    @Resource
    private BotChannelInstanceIdentityResolver instanceIdentityResolver;
    @Resource
    private BotGroupCommandContextResolver groupContextResolver;
    @Resource
    private BotGroupMemberIdentityResolver groupMemberIdentityResolver;

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

        CommandAccessDecision instanceDecision = this.checkBotInstance(
                context.platform(), context.botAccountId(), command);
        if (!instanceDecision.allowed()) {
            return instanceDecision;
        }

        CommandAccessDecision systemDecision = this.checkSystemRole(context, command);
        if (!systemDecision.allowed()) {
            return systemDecision;
        }

        return this.checkGroupRole(context, command);
    }

    @Override
    public CommandAccessDecision checkInstance(BotPlatform platform, String botAccountId,
                                               CommandDescriptor descriptor) {
        return commandRepository.findByCodeIgnoreCase(descriptor.code())
                .map(command -> checkBotInstance(platform, botAccountId, command))
                .orElseGet(() -> CommandAccessPolicy.super.checkInstance(platform, botAccountId, descriptor));
    }

    private CommandAccessDecision checkBotInstance(BotPlatform platform, String botAccountId,
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
                    command.getCode(), command.getId(), platform, botAccountId);
            return CommandAccessDecision.denied("当前命令未配置机器人能力角色");
        }
        OptionalLong instanceId = this.instanceIdentityResolver.resolveId(platform, botAccountId);
        if (instanceId.isEmpty()) {
            log.debug("机器人能力角色校验拒绝：commandCode={}, commandId={}, platform={}, botAccountId={}, "
                            + "bindingCount={}, reason=机器人实例不存在",
                    command.getCode(), command.getId(), platform, botAccountId,
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
                        .anyMatch(item -> item.getBotInstanceId().equals(instanceId.getAsLong())))
                .collect(Collectors.toSet());
        boolean allowed = !boundRoleIds.isEmpty();
        log.debug("机器人能力角色校验：commandCode={}, commandId={}, platform={}, botAccountId={}, "
                        + "bindingCount={}, instanceId={}, enabledRoleIds={}, boundRoleIds={}, allowed={}",
                command.getCode(), command.getId(), platform, botAccountId,
                bindings.size(), instanceId.getAsLong(), enabledRoleIds, boundRoleIds, allowed);
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
        // 解析器会优先使用入口只读上下文，权限检查不重复查询群组、绑定和主成员。
        return groupContextResolver.resolve(context)
                .flatMap(group -> groupMemberIdentityResolver.findByContext(group.group().getId(), context))
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
