package com.nnm.bot.admin.service.bot.command.impl;

import cn.hutool.core.text.CharSequenceUtil;

import com.nnm.bot.admin.constant.bot.BotManagementErrorCode;
import java.util.stream.Collectors;
import cn.hutool.core.util.ObjUtil;

import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.admin.pojo.entity.bot.command.BotCommand;
import com.nnm.bot.admin.pojo.entity.bot.command.BotCommandAlias;
import com.nnm.bot.admin.pojo.entity.bot.command.BotCommandCost;
import com.nnm.bot.admin.pojo.entity.bot.command.BotCommandTemplate;
import com.nnm.bot.admin.pojo.entity.bot.command.BotCommandRobotRole;
import com.nnm.bot.admin.pojo.entity.bot.role.BotRobotRole;
import com.nnm.bot.admin.pojo.entity.bot.command.BotCommandRole;
import com.nnm.bot.admin.pojo.entity.system.SysRole;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandCostDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandCostSaveDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandRoleDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandRoleSaveDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandSaveDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandSystemRoleSaveDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandTriggerDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandTriggerSaveDTO;
import com.nnm.bot.admin.repository.bot.command.BotCommandAliasRepository;
import com.nnm.bot.admin.repository.bot.command.BotCommandCostRepository;
import com.nnm.bot.admin.repository.bot.command.BotCommandRepository;
import com.nnm.bot.admin.repository.bot.command.BotCommandRobotRoleRepository;
import com.nnm.bot.admin.repository.bot.command.BotCommandTemplateRepository;
import com.nnm.bot.admin.repository.bot.command.BotCommandRoleRepository;
import com.nnm.bot.admin.repository.system.SysRoleRepository;
import com.nnm.bot.admin.service.bot.command.BotCommandManagementService;
import com.nnm.bot.admin.validation.command.BotCommandPresentationValidator;
import com.nnm.bot.core.plugin.constant.CommandBoundaryPolicy;
import com.nnm.bot.core.plugin.api.CommandDescriptor;
import com.nnm.bot.core.plugin.api.BotPlugin;
import com.nnm.bot.core.plugin.api.CommandMetadataDTO;
import com.nnm.bot.core.plugin.registry.CommandRegistry;
import com.nnm.bot.core.plugin.registry.RuntimeCommand;
import com.nnm.bot.admin.repository.bot.role.BotRobotRoleRepository;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.common.error.BusinessException;
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

/** 读取命令定义并维护管理端命令配置、授权与成本。 */
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
    private BotCommandCostRepository costRepository;
    @Resource
    private BotCommandTemplateRepository templateRepository;
    @Resource
    private BotRobotRoleRepository roleRepository;
    @Resource
    private BotCommandRoleRepository systemRoleBindingRepository;
    @Resource
    private SysRoleRepository systemRoleRepository;
    @Resource
    private BotCommandPresentationValidator presentationValidator;

    @Override
    public List<BotCommandDTO> commands() {
        Map<String, BotCommand> configured =
            commandRepository.findAll().stream().collect(Collectors.toMap(command -> normalize(command.getCode()),
                command -> command, (left, right) -> right, LinkedHashMap::new));
        Map<String,
            BotPlugin<?, ?>> plugins = commandRegistry.allPlugins().stream()
                .collect(Collectors.toMap(plugin -> normalize(commandRegistry.metadata(plugin).code()),
                    plugin -> plugin, (left, right) -> left, LinkedHashMap::new));
        Set<String> codes = new LinkedHashSet<>(plugins.keySet());
        codes.addAll(configured.keySet());
        return codes
            .stream().map(code -> toItem(plugins.get(code), configured.get(code))).sorted(Comparator
                .comparing(BotCommandDTO::gameCode).thenComparing(BotCommandDTO::code, String.CASE_INSENSITIVE_ORDER))
            .toList();
    }

    @Override
    @Transactional
    public void update(String code, BotCommandSaveDTO request) {
        BotPlugin<?, ?> plugin = commandRegistry.plugin(code)
            .orElseThrow(() -> new BusinessException(BotManagementErrorCode.COMMAND_NOT_FOUND));
        CommandDescriptor descriptor = commandRegistry.command(code).map(RuntimeCommand::descriptor).orElseGet(() -> {
            CommandMetadataDTO metadata = commandRegistry.metadata(plugin);
            return new CommandDescriptor(metadata.code(), metadata.name(), List.of(), "", "", metadata.matchType(),
                metadata.order());
        });
        BotCommand command = commandRepository.findByCodeIgnoreCase(code).orElseGet(BotCommand::new);
        command.setCode(descriptor.code());
        command.setName(valueOr(request.name(), descriptor.name()));
        command.setGameCode(resolveGameCode(command, descriptor.code(), request.gameCode()));
        command.setDescription(valueOr(request.description(), descriptor.description()));
        command.setEnabled(Boolean.TRUE.equals(request.enabled()));
        command.setShowInHelp(Boolean.TRUE.equals(request.showInHelp()));
        command
            .setCooldownSeconds(Math.max(0, ObjUtil.isNull(request.cooldownSeconds()) ? 0 : request.cooldownSeconds()));
        command.setMentionInvoker(Boolean.TRUE.equals(request.mentionInvoker()));
        command.setPermissionOverride(request.permissionOverride());
        command.setGroupRoleRequired(normalizeGroupRole(request.groupRoleRequired()));
        BotCommand saved = commandRepository.save(command);
        updateTemplateSelection(saved, request.templateId());
        commandRepository.save(saved);

        Map<String, BotCommandTriggerSaveDTO> triggers = normalizeTriggers(request.triggers());
        synchronizeTriggers(saved.getId(), triggers);
        commandRegistry.refreshAfterCommit();
    }

    @Override
    public List<BotCommandRoleDTO> roles(String code) {
        BotCommand command = commandRepository.findByCodeIgnoreCase(code)
            .orElseThrow(() -> new BusinessException(BotManagementErrorCode.COMMAND_NOT_FOUND));
        Map<Long, BotCommandRobotRole> assigned = commandRoleRepository.findAllByCommandId(command.getId()).stream()
            .collect(Collectors.toMap(BotCommandRobotRole::getRobotRoleId, item -> item));
        return roleRepository.findAll().stream().sorted(Comparator.comparing(BotRobotRole::getCode))
            .map(role -> new BotCommandRoleDTO(role.getId(), role.getCode(), role.getName(), role.isEnabled(),
                assigned.containsKey(role.getId())))
            .toList();
    }

    @Override
    @Transactional
    public void updateRoles(String code, BotCommandRoleSaveDTO request) {
        BotCommand command = commandRepository.findByCodeIgnoreCase(code)
            .orElseThrow(() -> new BusinessException(BotManagementErrorCode.COMMAND_NOT_FOUND));
        Map<Long, BotRobotRole> roles = roleRepository.findAllById(request.roleIds()).stream()
            .collect(Collectors.toMap(BotRobotRole::getId, item -> item));
        if (roles.size() != request.roleIds().size()) {
            throw new BusinessException(BotManagementErrorCode.COMMAND_ROLE_UNKNOWN);
        }
        List<BotCommandRobotRole> current = commandRoleRepository.findAllByCommandId(command.getId());
        Set<Long> currentIds = current.stream().map(BotCommandRobotRole::getRobotRoleId).collect(Collectors.toSet());
        List<BotCommandRobotRole> removed =
            current.stream().filter(binding -> !roles.containsKey(binding.getRobotRoleId())).toList();
        if (!removed.isEmpty())
            commandRoleRepository.deleteAll(removed);
        List<BotCommandRobotRole> added = new ArrayList<>();
        for (Long roleId : request.roleIds()) {
            if (currentIds.contains(roleId))
                continue;
            BotCommandRobotRole binding = new BotCommandRobotRole();
            binding.setCommandId(command.getId());
            binding.setRobotRoleId(roleId);
            added.add(binding);
        }
        if (!added.isEmpty())
            commandRoleRepository.saveAll(added);
    }

    @Override
    public List<Long> systemRoleIds(String code) {
        BotCommand command = commandRepository.findByCodeIgnoreCase(code)
            .orElseThrow(() -> new BusinessException(BotManagementErrorCode.COMMAND_NOT_FOUND));
        return systemRoleBindingRepository.findAllByCommandId(command.getId()).stream().map(BotCommandRole::getRoleId)
            .distinct().toList();
    }

    @Override
    @Transactional
    public void updateSystemRoles(String code, BotCommandSystemRoleSaveDTO request) {
        BotCommand command = commandRepository.findByCodeIgnoreCase(code)
            .orElseThrow(() -> new BusinessException(BotManagementErrorCode.COMMAND_NOT_FOUND));
        List<Long> roleIds =
            CollUtil.isEmpty(request.roleIds()) ? List.of() : request.roleIds().stream().distinct().toList();
        Map<Long, SysRole> roles =
            systemRoleRepository.findAllById(roleIds).stream().collect(Collectors.toMap(SysRole::getId, item -> item));
        if (roles.size() != roleIds.size()) {
            throw new BusinessException(BotManagementErrorCode.COMMAND_SYSTEM_ROLE_UNKNOWN);
        }
        List<BotCommandRole> current = systemRoleBindingRepository.findAllByCommandId(command.getId());
        Set<Long> currentIds = current.stream().map(BotCommandRole::getRoleId).collect(Collectors.toSet());
        List<BotCommandRole> removed =
            current.stream().filter(binding -> !roles.containsKey(binding.getRoleId())).toList();
        if (!removed.isEmpty())
            systemRoleBindingRepository.deleteAll(removed);
        List<BotCommandRole> added = new ArrayList<>();
        for (Long roleId : roleIds) {
            if (currentIds.contains(roleId))
                continue;
            BotCommandRole binding = new BotCommandRole();
            binding.setCommandId(command.getId());
            binding.setRoleId(roleId);
            added.add(binding);
        }
        if (!added.isEmpty())
            systemRoleBindingRepository.saveAll(added);
    }

    private void synchronizeTriggers(long commandId, Map<String, BotCommandTriggerSaveDTO> target) {
        List<BotCommandAlias> current = aliasRepository.findAllByCommandId(commandId);
        Map<String, BotCommandAlias> retained = new LinkedHashMap<>();
        List<BotCommandAlias> removed = new ArrayList<>();
        for (BotCommandAlias alias : current) {
            String key = normalize(alias.getAliasName());
            if (!target.containsKey(key) || retained.putIfAbsent(key, alias) != null)
                removed.add(alias);
        }
        if (!removed.isEmpty()) {
            aliasRepository.deleteAll(removed);
            aliasRepository.flush();
        }
        List<BotCommandAlias> writing = new ArrayList<>();
        target.forEach((key, trigger) -> {
            BotCommandAlias alias = retained.get(key);
            if (alias == null) {
                alias = new BotCommandAlias();
                alias.setCommandId(commandId);
            } else if (alias.getAliasName().equals(trigger.name())
                && alias.getBoundaryPolicy() == trigger.boundaryPolicy()) {
                return;
            }
            alias.setAliasName(trigger.name());
            alias.setBoundaryPolicy(trigger.boundaryPolicy());
            writing.add(alias);
        });
        if (!writing.isEmpty())
            aliasRepository.saveAllAndFlush(writing);
    }

    @Override
    public BotCommandCostDTO cost(String code) {
        BotCommand command = commandRepository.findByCodeIgnoreCase(code)
            .orElseThrow(() -> new BusinessException(BotManagementErrorCode.COMMAND_NOT_FOUND));
        BotCommandCost cost = costRepository.findByCommandId(command.getId()).orElse(null);
        return new BotCommandCostDTO(command.getCode(), ObjUtil.isNull(cost) ? 0 : cost.getCostPoints(),
            ObjUtil.isNotNull(cost) && cost.isEnabled(), ObjUtil.isNull(cost) ? 0 : cost.getRewardPoints(),
            ObjUtil.isNotNull(cost) && cost.isRewardEnabled());
    }

    @Override
    @Transactional
    public void updateCost(String code, BotCommandCostSaveDTO request) {
        BotCommand command = commandRepository.findByCodeIgnoreCase(code)
            .orElseThrow(() -> new BusinessException(BotManagementErrorCode.COMMAND_NOT_FOUND));
        BotCommandCost cost = costRepository.findByCommandId(command.getId()).orElseGet(BotCommandCost::new);
        cost.setCommandId(command.getId());
        cost.setCostPoints(request.costPoints());
        cost.setEnabled(Boolean.TRUE.equals(request.enabled()));
        cost.setRewardPoints(request.rewardPoints());
        cost.setRewardEnabled(Boolean.TRUE.equals(request.rewardEnabled()));
        costRepository.save(cost);
    }

    private void updateTemplateSelection(BotCommand command, Long templateId) {
        if (templateId == null) {
            command.setTemplateId(null);
            return;
        }
        BotCommandTemplate template = templateRepository.findById(templateId)
            .orElseThrow(() -> new BusinessException(BotManagementErrorCode.COMMAND_TEMPLATE_NOT_FOUND));
        BusinessAssert.isTrue(command.getId() != null && command.getId().equals(template.getCommandId()),
            BotManagementErrorCode.TEMPLATE_COMMAND_MISMATCH);
        BusinessAssert.isTrue(template.isEnabled(), BotManagementErrorCode.TEMPLATE_DISABLED);
        presentationValidator.validate(List.of(template));
        command.setTemplateId(template.getId());
    }

    private Map<String, BotCommandTriggerSaveDTO> normalizeTriggers(Collection<BotCommandTriggerSaveDTO> triggers) {
        if (CollUtil.isEmpty(triggers)) {
            return Map.of();
        }
        Map<String, BotCommandTriggerSaveDTO> normalized = new LinkedHashMap<>();
        for (BotCommandTriggerSaveDTO trigger : triggers) {
            if (ObjUtil.isNull(trigger) || CharSequenceUtil.isBlank(trigger.name())) {
                continue;
            }
            String value = CharSequenceUtil.trim(trigger.name());
            CommandBoundaryPolicy policy =
                ObjUtil.defaultIfNull(trigger.boundaryPolicy(), CommandBoundaryPolicy.defaultFor(value));
            normalized.putIfAbsent(value.toLowerCase(Locale.ROOT), new BotCommandTriggerSaveDTO(value, policy));
        }
        return Collections.unmodifiableMap(normalized);
    }

    private BotCommandDTO toItem(BotPlugin<?, ?> plugin, BotCommand command) {
        CommandMetadataDTO metadata = ObjUtil.isNull(plugin) ? null : commandRegistry.metadata(plugin);
        String code = ObjUtil.isNull(command) ? metadata.code() : command.getCode();
        String gameCode = gameCode(command, plugin, code);
        List<BotCommandTriggerDTO> triggers = ObjUtil.isNull(command) ? List.of()
            : aliasRepository.findAllByCommandId(command.getId()).stream()
                .sorted(Comparator.comparing(BotCommandAlias::getId))
                .map(alias -> new BotCommandTriggerDTO(alias.getAliasName(), alias.getBoundaryPolicy())).toList();
        return new BotCommandDTO(ObjUtil.isNull(command) ? null : command.getId(), ObjUtil.isNull(command), code,
            ObjUtil.isNull(command) ? metadata.name() : command.getName(), gameCode,
            ObjUtil.isNull(command) ? "" : command.getDescription(), metadata == null ? null : metadata.matchType(),
            ObjUtil.isNull(command) ? false : command.isEnabled(),
            ObjUtil.isNull(command) ? false : command.isShowInHelp(), triggers,
            ObjUtil.isNull(command) ? 0 : command.getCooldownSeconds(),
            ObjUtil.isNotNull(command) && command.isMentionInvoker(),
            ObjUtil.isNull(command) || ObjUtil.isNull(command.getPermissionOverride()) ? ""
                : command.getPermissionOverride(),
            ObjUtil.isNull(command) ? null : command.getGroupRoleRequired(),
            ObjUtil.isNull(command) ? null : command.getTemplateId(),
            ObjUtil.isNull(command) ? null : command.getCreatedAt(),
            ObjUtil.isNull(command) ? null : command.getUpdatedAt());
    }

    private String normalize(String code) {
        return ObjUtil.isNull(code) ? "" : CharSequenceUtil.trim(code).toLowerCase(Locale.ROOT);
    }

    private String resolveGameCode(BotCommand command, String descriptorCode, String requestedGameCode) {
        String pluginGameCode = pluginGameCode(descriptorCode);
        if (ObjUtil.isNotNull(pluginGameCode)) {
            return pluginGameCode;
        }
        if (CharSequenceUtil.isNotBlank(requestedGameCode)) {
            return CharSequenceUtil.trim(requestedGameCode);
        }
        if (ObjUtil.isNotNull(command.getId()) && CharSequenceUtil.isNotBlank(command.getGameCode())) {
            return command.getGameCode();
        }
        return defaultGameCode(descriptorCode);
    }

    private String normalizeGroupRole(String role) {
        if (CharSequenceUtil.isBlank(role)) {
            return null;
        }
        String normalized = CharSequenceUtil.trim(role).toUpperCase(Locale.ROOT);
        if (!Set.of("OWNER", "ADMIN").contains(normalized)) {
            throw new BusinessException(BotManagementErrorCode.GROUP_ROLE_INVALID);
        }
        return normalized;
    }

    private String gameCode(BotCommand command, BotPlugin<?, ?> plugin, String descriptorCode) {
        if (ObjUtil.isNotNull(plugin)) {
            return commandRegistry.metadata(plugin).gameCode();
        }
        if (ObjUtil.isNotNull(command) && CharSequenceUtil.isNotBlank(command.getGameCode())) {
            return command.getGameCode();
        }
        return defaultGameCode(descriptorCode);
    }

    /**
     * 内置插件的业务域由代码归属决定，避免历史数据库中的错误分类导致管理端漏显示。 没有插件实现的纯数据库命令仍然允许由 game_code 管理。
     */
    private String pluginGameCode(String code) {
        return commandRegistry.plugin(code).map(plugin -> commandRegistry.metadata(plugin).gameCode()).orElse(null);
    }

    private String defaultGameCode(String code) {
        return Set.of("status", "help", "sign-in", "bind").contains(normalize(code)) ? "system" : "warframe";
    }

    private static String valueOr(String value, String fallback) {
        return CharSequenceUtil.isBlank(value) ? fallback : CharSequenceUtil.trim(value);
    }
}
