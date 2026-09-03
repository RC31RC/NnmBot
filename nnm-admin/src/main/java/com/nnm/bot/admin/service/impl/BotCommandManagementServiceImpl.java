package com.nnm.bot.admin.service.impl;

import cn.hutool.core.util.ObjectUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.admin.domain.BotCommand;
import com.nnm.bot.admin.domain.BotCommandAlias;
import com.nnm.bot.admin.domain.BotCommandChannel;
import com.nnm.bot.admin.domain.BotCommandCost;
import com.nnm.bot.admin.domain.BotCommandTemplate;
import com.nnm.bot.admin.domain.BotCommandRobotRole;
import com.nnm.bot.admin.domain.BotRobotRole;
import com.nnm.bot.admin.domain.BotCommandRole;
import com.nnm.bot.admin.domain.SysRole;
import com.nnm.bot.admin.dto.bot.BotCommandChannelOutputItem;
import com.nnm.bot.admin.dto.bot.BotCommandChannelOutputSaveRequest;
import com.nnm.bot.admin.dto.bot.BotCommandCostItem;
import com.nnm.bot.admin.dto.bot.BotCommandCostSaveRequest;
import com.nnm.bot.admin.dto.bot.BotCommandItem;
import com.nnm.bot.admin.dto.bot.BotCommandRoleItem;
import com.nnm.bot.admin.dto.bot.BotCommandRoleSaveRequest;
import com.nnm.bot.admin.dto.bot.BotCommandSaveRequest;
import com.nnm.bot.admin.dto.bot.BotCommandSystemRoleSaveRequest;
import com.nnm.bot.admin.dto.bot.BotCommandTriggerItem;
import com.nnm.bot.admin.dto.bot.BotCommandTriggerSaveRequest;
import com.nnm.bot.admin.repository.BotCommandAliasRepository;
import com.nnm.bot.admin.repository.BotCommandChannelRepository;
import com.nnm.bot.admin.repository.BotCommandCostRepository;
import com.nnm.bot.admin.repository.BotCommandRepository;
import com.nnm.bot.admin.repository.BotCommandRobotRoleRepository;
import com.nnm.bot.admin.repository.BotCommandTemplateRepository;
import com.nnm.bot.admin.repository.BotCommandRoleRepository;
import com.nnm.bot.admin.repository.SysRoleRepository;
import com.nnm.bot.admin.service.BotCommandManagementService;
import com.nnm.bot.core.constant.BotPlatform;
import com.nnm.bot.core.constant.CommandBoundaryPolicy;
import com.nnm.bot.core.plugin.model.CommandDescriptor;
import com.nnm.bot.core.plugin.model.CommandExecutionConfiguration;
import com.nnm.bot.core.constant.ChannelOutputMode;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.annotation.BotPluginCommand;
import com.nnm.bot.core.plugin.registry.CommandRegistry;
import com.nnm.bot.core.plugin.model.RuntimeCommand;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Comparator;
import java.util.ArrayList;

@Service
public class BotCommandManagementServiceImpl implements BotCommandManagementService {

    @Resource
    private CommandRegistry commandRegistry;
    @Resource
    private BotCommandRepository commandRepository;
    @Resource
    private BotCommandAliasRepository aliasRepository;
    @Resource
    private BotCommandRobotRoleRepository commandRoleRepository;
    @Resource
    private BotCommandChannelRepository commandChannelRepository;
    @Resource
    private BotCommandCostRepository costRepository;
    @Resource
    private BotCommandTemplateRepository templateRepository;
    @Resource
    private com.nnm.bot.admin.repository.BotRobotRoleRepository roleRepository;
    @Resource
    private BotCommandRoleRepository systemRoleBindingRepository;
    @Resource
    private SysRoleRepository systemRoleRepository;

    @Override
    public List<BotCommandItem> commands() {
        Map<String, BotCommand> configured = commandRepository.findAll().stream()
                .collect(java.util.stream.Collectors.toMap(
                        command -> normalize(command.getCode()),
                        command -> command,
                        (left, right) -> right,
                        LinkedHashMap::new));
        Map<String, BotPlugin> plugins = commandRegistry.allPlugins().stream()
                .collect(java.util.stream.Collectors.toMap(
                        plugin -> normalize(commandRegistry.metadata(plugin).code()),
                        plugin -> plugin,
                        (left, right) -> left,
                        LinkedHashMap::new));
        Set<String> codes = new LinkedHashSet<>(plugins.keySet());
        codes.addAll(configured.keySet());
        return codes.stream()
                .map(code -> toItem(plugins.get(code), configured.get(code)))
                .sorted(Comparator.comparing(BotCommandItem::gameCode)
                        .thenComparing(BotCommandItem::code,
                                String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Override
    @Transactional
    public void update(String code, BotCommandSaveRequest request) {
        BotPlugin plugin = commandRegistry.plugin(code)
                .orElseThrow(() -> new IllegalArgumentException("Unknown command: " + code));
        CommandDescriptor descriptor = commandRegistry.command(code)
                .map(RuntimeCommand::descriptor)
                .orElseGet(() -> {
                    BotPluginCommand metadata = commandRegistry.metadata(plugin);
                    return new CommandDescriptor(
                            metadata.code(),
                            metadata.name(),
                            List.of(),
                            "",
                            "",
                            metadata.matchType(),
                            metadata.order());
                });
        BotCommand command = commandRepository.findByCodeIgnoreCase(code).orElseGet(BotCommand::new);
        command.setCode(descriptor.code());
        command.setName(valueOr(request.name(), descriptor.name()));
        command.setGameCode(resolveGameCode(command, descriptor.code(), request.gameCode()));
        command.setDescription(valueOr(request.description(), descriptor.description()));
        command.setEnabled(Boolean.TRUE.equals(request.enabled()));
        command.setShowInHelp(Boolean.TRUE.equals(request.showInHelp()));
        command.setOutputMode(request.outputMode());
        command.setCooldownSeconds(Math.max(0, ObjectUtil.isNull(request.cooldownSeconds())
                ? 0 : request.cooldownSeconds()));
        command.setMentionInvoker(Boolean.TRUE.equals(request.mentionInvoker()));
        command.setPermissionOverride(request.permissionOverride());
        command.setGroupRoleRequired(normalizeGroupRole(request.groupRoleRequired()));
        BotCommand saved = commandRepository.save(command);
        updateTemplates(saved, request.templateIds());

        aliasRepository.deleteAllByCommandId(saved.getId());
        aliasRepository.flush();

        Map<String, BotCommandTriggerSaveRequest> triggers =
                normalizeTriggers(request.triggers());
        for (BotCommandTriggerSaveRequest trigger : triggers.values()) {
            BotCommandAlias item = new BotCommandAlias();
            item.setCommandId(saved.getId());
            item.setAliasName(trigger.name().trim());
            item.setBoundaryPolicy(trigger.boundaryPolicy());
            aliasRepository.save(item);
        }
        aliasRepository.flush();
        commandRegistry.refreshAfterCommit();
    }

    @Override
    public List<BotCommandChannelOutputItem> channelOutputs(String code) {
        BotCommand command = commandRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new IllegalArgumentException("Unknown command: " + code));
        Map<String, BotCommandChannel> configured = commandChannelRepository
                .findAllByCommandId(command.getId())
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                        item -> item.getPlatform().toUpperCase(Locale.ROOT),
                        item -> item,
                        (left, right) -> right));

        return java.util.Arrays.stream(BotPlatform.values())
                .map(platform -> {
                    BotCommandChannel channel = configured.get(platform.name());
                    return new BotCommandChannelOutputItem(
                            platform,
                            platformDisplayName(platform),
                            ObjectUtil.isNotNull(channel),
                            ObjectUtil.isNull(channel)
                                    ? ChannelOutputMode.from(command.getOutputMode())
                                    : channel.getOutputMode());
                })
                .toList();
    }

    @Override
    @Transactional
    public void updateChannelOutput(String code, BotPlatform platform,
                                    BotCommandChannelOutputSaveRequest request) {
        BotCommand command = commandRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new IllegalArgumentException("Unknown command: " + code));
        BotCommandChannel output = commandChannelRepository
                .findByCommandIdAndPlatformIgnoreCase(command.getId(), platform.name())
                .orElseGet(BotCommandChannel::new);
        output.setCommandId(command.getId());
        output.setPlatform(platform.name());
        output.setOutputMode(request.outputMode());
        commandChannelRepository.save(output);
        commandRegistry.refreshAfterCommit();
    }

    private String platformDisplayName(BotPlatform platform) {
        return switch (platform) {
            case ONEBOT -> "OneBot";
            case QQ_OFFICIAL -> "QQ 官方";
        };
    }

    @Override
    public List<BotCommandRoleItem> roles(String code) {
        BotCommand command = commandRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new IllegalArgumentException("Unknown command: " + code));
        Map<Long, BotCommandRobotRole> assigned = commandRoleRepository.findAllByCommandId(command.getId())
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                        BotCommandRobotRole::getRobotRoleId, item -> item));
        return roleRepository.findAll().stream()
                .sorted(java.util.Comparator.comparing(BotRobotRole::getCode))
                .map(role -> new BotCommandRoleItem(
                        role.getId(),
                        role.getCode(),
                        role.getName(),
                        role.isEnabled(),
                        assigned.containsKey(role.getId())))
                .toList();
    }

    @Override
    @Transactional
    public void updateRoles(String code, BotCommandRoleSaveRequest request) {
        BotCommand command = commandRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new IllegalArgumentException("Unknown command: " + code));
        Map<Long, BotRobotRole> roles = roleRepository.findAllById(request.roleIds()).stream()
                .collect(java.util.stream.Collectors.toMap(BotRobotRole::getId, item -> item));
        if (roles.size() != request.roleIds().size()) {
            throw new IllegalArgumentException("命令角色包含不存在的角色");
        }
        commandRoleRepository.deleteAllByCommandId(command.getId());
        commandRoleRepository.flush();
        for (Long roleId : request.roleIds()) {
            BotCommandRobotRole binding = new BotCommandRobotRole();
            binding.setCommandId(command.getId());
            binding.setRobotRoleId(roleId);
            commandRoleRepository.save(binding);
        }
    }

    @Override
    public List<Long> systemRoleIds(String code) {
        BotCommand command = commandRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new IllegalArgumentException("Unknown command: " + code));
        return systemRoleBindingRepository
                .findAllByCommandId(command.getId())
                .stream()
                .map(BotCommandRole::getRoleId)
                .distinct()
                .toList();
    }

    @Override
    @Transactional
    public void updateSystemRoles(String code, BotCommandSystemRoleSaveRequest request) {
        BotCommand command = commandRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new IllegalArgumentException("Unknown command: " + code));
        List<Long> roleIds = CollUtil.isEmpty(request.roleIds())
                ? List.of()
                : request.roleIds().stream().distinct().toList();
        Map<Long, SysRole> roles = systemRoleRepository.findAllById(roleIds).stream()
                .collect(java.util.stream.Collectors.toMap(SysRole::getId, item -> item));
        if (roles.size() != roleIds.size()) {
            throw new IllegalArgumentException("命令系统角色包含不存在的角色");
        }
        systemRoleBindingRepository.deleteAllByCommandId(command.getId());
        systemRoleBindingRepository.flush();
        for (Long roleId : roleIds) {
            BotCommandRole binding = new BotCommandRole();
            binding.setCommandId(command.getId());
            binding.setRoleId(roleId);
            systemRoleBindingRepository.save(binding);
        }
    }

    @Override
    public BotCommandCostItem cost(String code) {
        BotCommand command = commandRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new IllegalArgumentException("Unknown command: " + code));
        BotCommandCost cost = costRepository.findByCommandId(command.getId()).orElse(null);
        return new BotCommandCostItem(
                command.getCode(),
                ObjectUtil.isNull(cost) ? 0 : cost.getCostPoints(),
                ObjectUtil.isNotNull(cost) && cost.isEnabled(),
                ObjectUtil.isNull(cost) ? 0 : cost.getRewardPoints(),
                ObjectUtil.isNotNull(cost) && cost.isRewardEnabled());
    }

    @Override
    @Transactional
    public void updateCost(String code, BotCommandCostSaveRequest request) {
        BotCommand command = commandRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new IllegalArgumentException("Unknown command: " + code));
        BotCommandCost cost = costRepository.findByCommandId(command.getId())
                .orElseGet(BotCommandCost::new);
        cost.setCommandId(command.getId());
        cost.setCostPoints(request.costPoints());
        cost.setEnabled(Boolean.TRUE.equals(request.enabled()));
        cost.setRewardPoints(request.rewardPoints());
        cost.setRewardEnabled(Boolean.TRUE.equals(request.rewardEnabled()));
        costRepository.save(cost);
    }

    private void updateTemplates(BotCommand command, Collection<Long> requestedIds) {
        List<Long> templateIds = CollUtil.isEmpty(requestedIds)
                ? List.of()
                : requestedIds.stream()
                        .filter(ObjectUtil::isNotNull)
                        .distinct()
                        .toList();
        Map<Long, BotCommandTemplate> selected = templateRepository.findAllById(templateIds)
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                        BotCommandTemplate::getId, item -> item));
        if (selected.size() != templateIds.size()) {
            throw new IllegalArgumentException("命令模板包含不存在的模板");
        }
        if (selected.values().stream()
                .anyMatch(template -> ObjectUtil.isNotNull(template.getCommandId())
                        && !template.getCommandId().equals(command.getId()))) {
            throw new IllegalArgumentException("命令模板已归属于其他命令");
        }

        List<BotCommandTemplate> current = templateRepository
                .findAllByCommandIdOrderBySortOrderAscIdAsc(command.getId());
        for (BotCommandTemplate template : current) {
            if (!selected.containsKey(template.getId())) {
                template.setCommandId(null);
                template.setSortOrder(0);
            }
        }
        for (int index = 0; index < templateIds.size(); index++) {
            BotCommandTemplate template = selected.get(templateIds.get(index));
            template.setCommandId(command.getId());
            template.setSortOrder(index);
        }
        templateRepository.saveAll(new ArrayList<>(current));
        templateRepository.saveAll(selected.values());
        templateRepository.flush();
    }

    private Map<String, BotCommandTriggerSaveRequest> normalizeTriggers(
            Collection<BotCommandTriggerSaveRequest> triggers) {
        if (CollUtil.isEmpty(triggers)) {
            return Map.of();
        }
        Map<String, BotCommandTriggerSaveRequest> normalized = new LinkedHashMap<>();
        for (BotCommandTriggerSaveRequest trigger : triggers) {
            if (ObjectUtil.isNull(trigger) || StrUtil.isBlank(trigger.name())) {
                continue;
            }
            String value = trigger.name().trim();
            CommandBoundaryPolicy policy = ObjectUtil.defaultIfNull(
                    trigger.boundaryPolicy(), CommandBoundaryPolicy.defaultFor(value));
            normalized.putIfAbsent(
                    value.toLowerCase(Locale.ROOT),
                    new BotCommandTriggerSaveRequest(value, policy));
        }
        return Collections.unmodifiableMap(normalized);
    }

    private BotCommandItem toItem(BotPlugin plugin, BotCommand command) {
        BotPluginCommand metadata = ObjectUtil.isNull(plugin)
                ? null : commandRegistry.metadata(plugin);
        String code = ObjectUtil.isNull(command)
                ? metadata.code()
                : command.getCode();
        String gameCode = gameCode(command, plugin, code);
        List<BotCommandTriggerItem> triggers = ObjectUtil.isNull(command)
                ? List.of()
                : aliasRepository.findAllByCommandId(command.getId()).stream()
                        .sorted(Comparator.comparing(BotCommandAlias::getId))
                        .map(alias -> new BotCommandTriggerItem(
                                alias.getAliasName(), alias.getBoundaryPolicy()))
                        .toList();
        List<BotCommandTemplate> templates = ObjectUtil.isNull(command)
                ? List.of()
                : templateRepository.findAllByCommandIdOrderBySortOrderAscIdAsc(command.getId());
        return new BotCommandItem(
                ObjectUtil.isNull(command) ? null : command.getId(),
                ObjectUtil.isNull(command),
                code,
                ObjectUtil.isNull(command) ? metadata.name() : command.getName(),
                gameCode,
                ObjectUtil.isNull(command) ? "" : command.getDescription(),
                ObjectUtil.isNull(command) ? false : command.isEnabled(),
                ObjectUtil.isNull(command) ? false : command.isShowInHelp(),
                ObjectUtil.isNull(command) ? com.nnm.bot.core.constant.CommandOutputMode.TEXT : command.getOutputMode(),
                triggers,
                ObjectUtil.isNull(command) ? 0 : command.getCooldownSeconds(),
                ObjectUtil.isNotNull(command) && command.isMentionInvoker(),
                ObjectUtil.isNull(command) || ObjectUtil.isNull(command.getPermissionOverride())
                        ? "" : command.getPermissionOverride(),
                ObjectUtil.isNull(command) ? null : command.getGroupRoleRequired(),
                templates.stream().map(BotCommandTemplate::getId).toList(),
                templates.stream().map(BotCommandTemplate::getTemplateCode).toList(),
                ObjectUtil.isNull(command) ? null : command.getCreatedAt(),
                ObjectUtil.isNull(command) ? null : command.getUpdatedAt());
    }

    private String normalize(String code) {
        return ObjectUtil.isNull(code) ? "" : code.trim().toLowerCase(Locale.ROOT);
    }

    private String resolveGameCode(BotCommand command, String descriptorCode, String requestedGameCode) {
        String pluginGameCode = pluginGameCode(descriptorCode);
        if (ObjectUtil.isNotNull(pluginGameCode)) {
            return pluginGameCode;
        }
        if (StrUtil.isNotBlank(requestedGameCode)) {
            return requestedGameCode.trim();
        }
        if (ObjectUtil.isNotNull(command.getId()) && StrUtil.isNotBlank(command.getGameCode())) {
            return command.getGameCode();
        }
        return defaultGameCode(descriptorCode);
    }

    private String normalizeGroupRole(String role) {
        if (StrUtil.isBlank(role)) {
            return null;
        }
        String normalized = role.trim().toUpperCase(Locale.ROOT);
        if (!Set.of("OWNER", "ADMIN").contains(normalized)) {
            throw new IllegalArgumentException("群组角色要求只能是 OWNER 或 ADMIN");
        }
        return normalized;
    }

    private String gameCode(BotCommand command, BotPlugin plugin, String descriptorCode) {
        if (ObjectUtil.isNotNull(plugin)) {
            return commandRegistry.metadata(plugin).gameCode();
        }
        if (ObjectUtil.isNotNull(command) && StrUtil.isNotBlank(command.getGameCode())) {
            return command.getGameCode();
        }
        return defaultGameCode(descriptorCode);
    }

    /**
     * 内置插件的业务域由代码归属决定，避免历史数据库中的错误分类导致管理端漏显示。
     * 没有插件实现的纯数据库命令仍然允许由 game_code 管理。
     */
    private String pluginGameCode(String code) {
        return commandRegistry.plugin(code)
                .map(plugin -> commandRegistry.metadata(plugin).gameCode())
                .orElse(null);
    }

    private String defaultGameCode(String code) {
        return Set.of("status", "help", "sign-in", "bind")
                .contains(normalize(code))
                ? "system" : "warframe";
    }

    private static String valueOr(String value, String fallback) {
        return StrUtil.isBlank(value) ? fallback : value.trim();
    }
}
