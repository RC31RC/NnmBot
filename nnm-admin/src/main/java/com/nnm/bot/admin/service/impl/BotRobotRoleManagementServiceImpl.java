package com.nnm.bot.admin.service.impl;

import java.util.Comparator;
import cn.hutool.core.util.ObjectUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.admin.domain.BotRobotRole;
import com.nnm.bot.admin.domain.BotRobotRoleInstance;
import com.nnm.bot.admin.domain.BotCommand;
import com.nnm.bot.admin.domain.BotCommandRobotRole;
import com.nnm.bot.admin.dto.bot.BotRobotRoleItem;
import com.nnm.bot.admin.dto.bot.BotRobotRoleSaveRequest;
import com.nnm.bot.admin.repository.BotChannelInstanceRepository;
import com.nnm.bot.admin.repository.BotCommandRepository;
import com.nnm.bot.admin.repository.BotCommandRobotRoleRepository;
import com.nnm.bot.admin.repository.BotRobotRoleInstanceRepository;
import com.nnm.bot.admin.repository.BotRobotRoleRepository;
import com.nnm.bot.admin.service.BotRobotRoleManagementService;
import com.nnm.bot.common.exception.DataNotFoundException;
import com.nnm.bot.core.plugin.registry.CommandRegistry;
import com.nnm.bot.core.plugin.model.CommandDescriptor;
import com.nnm.bot.core.constant.CommandOutputMode;
import com.nnm.bot.core.plugin.system.SystemCommandCodes;
import io.github.rdt.lock.LockOptions;
import io.github.rdt.lock.RdtLock;
import io.github.rdt.lock.DistributedLockManager;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BotRobotRoleManagementServiceImpl implements BotRobotRoleManagementService {

    @Resource
    private BotRobotRoleRepository roleRepository;
    @Resource
    private BotRobotRoleInstanceRepository instanceBindingRepository;
    @Resource
    private BotCommandRobotRoleRepository commandBindingRepository;
    @Resource
    private BotChannelInstanceRepository instanceRepository;
    @Resource
    private BotCommandRepository commandRepository;
    @Resource
    private CommandRegistry commandRegistry;
    @Resource
    private DistributedLockManager lockManager;

    @Override
    public List<BotRobotRoleItem> list() {
        return roleRepository.findAll().stream()
                .sorted(Comparator.comparing(BotRobotRole::getCode))
                .map(this::toItem)
                .toList();
    }

    @Override
    @Transactional
    public long create(BotRobotRoleSaveRequest request) {
        String code = normalize(request.code());
        if (roleRepository.findByCodeIgnoreCase(code).isPresent()) {
            throw new IllegalArgumentException("机器人角色编码已存在");
        }
        BotRobotRole role = new BotRobotRole();
        apply(role, request, code);
        role = roleRepository.save(role);
        saveInstanceBindings(role, request.botInstanceIds());
        saveCommandBindings(role, request.commandCodes());
        return role.getId();
    }

    @Override
    @Transactional
    public void update(long id, BotRobotRoleSaveRequest request) {
        BotRobotRole role = role(id);
        String code = normalize(request.code());
        roleRepository.findByCodeIgnoreCase(code)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("机器人角色编码已存在");
        });
        apply(role, request, code);
        if (ObjectUtil.isNotNull(request.botInstanceIds())) {
            saveInstanceBindings(role, request.botInstanceIds());
        }
        saveCommandBindings(role, request.commandCodes());
    }

    @Override
    @Transactional
    @RdtLock
    public void assignInstanceRoles(long instanceId, List<Long> roleIds) {
        // 使用 RDT 锁保护同一实例的角色全集，数据库只负责普通查询和唯一约束。
        lockManager.lock("bot-instance-role:" + instanceId, LockOptions.defaults());
        instanceRepository.findById(instanceId)
                .orElseThrow(() -> new DataNotFoundException("机器人实例不存在"));
        Set<Long> targetRoleIds = CollUtil.isEmpty(roleIds)
                ? Set.of()
                : roleIds.stream()
                        .filter(ObjectUtil::isNotNull)
                        .collect(Collectors.toSet());
        if (roleRepository.findAllById(targetRoleIds).size() != targetRoleIds.size()) {
            throw new IllegalArgumentException("包含不存在的机器人角色");
        }

        List<BotRobotRoleInstance> currentBindings =
                instanceBindingRepository.findAllByBotInstanceId(instanceId);
        Set<Long> currentRoleIds = currentBindings.stream()
                .map(BotRobotRoleInstance::getRobotRoleId)
                .collect(Collectors.toSet());

        List<BotRobotRoleInstance> obsoleteBindings = currentBindings.stream()
                .filter(binding -> !targetRoleIds.contains(binding.getRobotRoleId()))
                .toList();
        if (CollUtil.isNotEmpty(obsoleteBindings)) {
            instanceBindingRepository.deleteAll(obsoleteBindings);
            instanceBindingRepository.flush();
        }

        List<BotRobotRoleInstance> missingBindings = targetRoleIds.stream()
                .filter(roleId -> !currentRoleIds.contains(roleId))
                .map(roleId -> {
                    BotRobotRoleInstance binding = new BotRobotRoleInstance();
                    binding.setRobotRoleId(roleId);
                    binding.setBotInstanceId(instanceId);
                    return binding;
                })
                .toList();
        if (CollUtil.isNotEmpty(missingBindings)) {
            instanceBindingRepository.saveAll(missingBindings);
        }
    }

    @Override
    @Transactional
    public void delete(long id) {
        BotRobotRole role = role(id);
        if (instanceBindingRepository.existsByRobotRoleId(id)) {
            throw new IllegalArgumentException("机器人角色仍绑定机器人实例，不能删除");
        }
        // 命令关联是角色的能力定义，删除角色时一并清理，避免留下悬挂关联。
        commandBindingRepository.deleteAllByRobotRoleId(id);
        commandBindingRepository.flush();
        roleRepository.delete(role);
    }

    private void saveInstanceBindings(BotRobotRole role, List<Long> botInstanceIds) {
        List<Long> ids = CollUtil.isEmpty(botInstanceIds) ? List.of() : botInstanceIds;
        Set<Long> targetInstanceIds = new HashSet<>(ids);
        if (targetInstanceIds.size() != ids.size()
                || instanceRepository.findAllById(targetInstanceIds).size() != targetInstanceIds.size()) {
            throw new IllegalArgumentException("机器人角色包含重复或不存在的机器人实例");
        }

        List<BotRobotRoleInstance> currentBindings =
                instanceBindingRepository.findAllByRobotRoleId(role.getId());
        Set<Long> currentInstanceIds = currentBindings.stream()
                .map(BotRobotRoleInstance::getBotInstanceId)
                .collect(Collectors.toSet());
        List<BotRobotRoleInstance> obsoleteBindings = currentBindings.stream()
                .filter(binding -> !targetInstanceIds.contains(binding.getBotInstanceId()))
                .toList();
        if (CollUtil.isNotEmpty(obsoleteBindings)) {
            instanceBindingRepository.deleteAll(obsoleteBindings);
            instanceBindingRepository.flush();
        }

        List<BotRobotRoleInstance> missingBindings = targetInstanceIds.stream()
                .filter(instanceId -> !currentInstanceIds.contains(instanceId))
                .map(instanceId -> {
                    BotRobotRoleInstance binding = new BotRobotRoleInstance();
                    binding.setRobotRoleId(role.getId());
                    binding.setBotInstanceId(instanceId);
                    return binding;
                })
                .toList();
        if (CollUtil.isNotEmpty(missingBindings)) {
            instanceBindingRepository.saveAll(missingBindings);
        }
    }

    private BotRobotRoleItem toItem(BotRobotRole role) {
        List<Long> instanceIds = instanceBindingRepository.findAllByRobotRoleId(role.getId())
                .stream()
                .map(BotRobotRoleInstance::getBotInstanceId)
                .toList();
        Set<Long> assignedCommandIds = commandBindingRepository.findAllByRobotRoleId(role.getId())
                .stream()
                .map(BotCommandRobotRole::getCommandId)
                .collect(Collectors.toSet());
        Map<Long, BotCommand> commandsById = commandRepository.findAllById(assignedCommandIds).stream()
                .collect(Collectors.toMap(BotCommand::getId, Function.identity()));
        return new BotRobotRoleItem(
                role.getId(),
                role.getCode(),
                role.getName(),
                role.getDescription(),
                role.isEnabled(),
                instanceRepository.findAllById(instanceIds).stream()
                        .map(instance -> new BotRobotRoleItem.ListItemBinding(
                                instance.getId(), instance.getInstanceId(), instance.getDisplayName()))
                        .toList(),
                assignedCommandIds.stream()
                        .map(commandsById::get)
                        .filter(command -> ObjectUtil.isNotNull(command))
                        .sorted(Comparator.comparing(BotCommand::getGameCode)
                                .thenComparing(BotCommand::getCode))
                        .map(command -> new BotRobotRoleItem.CommandBinding(
                                command.getCode(), command.getName(), command.getGameCode()))
                        .toList(),
                role.getCreatedAt(),
                role.getUpdatedAt());
    }

    private BotRobotRole role(long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("机器人角色不存在"));
    }

    private void apply(BotRobotRole role, BotRobotRoleSaveRequest request, String code) {
        role.setCode(code);
        role.setName(request.name().trim());
        role.setDescription(trimToNull(request.description()));
        role.setEnabled(Boolean.TRUE.equals(request.enabled()));
    }

    private void saveCommandBindings(BotRobotRole role, List<String> commandCodes) {
        Set<String> codeSet = new HashSet<>(SystemCommandCodes.ALL);
        if (!CollUtil.isEmpty(commandCodes)) {
            commandCodes.stream()
                    .filter(StrUtil::isNotBlank)
                    .map(code -> code.trim().toLowerCase(Locale.ROOT))
                    .forEach(codeSet::add);
        }
        List<String> codes = codeSet.stream().sorted().toList();
        Map<String, BotCommand> commands = commandRegistry.descriptors().stream()
                .filter(descriptor -> codes.contains(descriptor.code().toLowerCase(Locale.ROOT)))
                .collect(Collectors.toMap(
                        descriptor -> descriptor.code().toLowerCase(Locale.ROOT),
                        descriptor -> commandRepository.findByCodeIgnoreCase(descriptor.code())
                                .orElseGet(() -> createDefaultCommand(descriptor)),
                        (left, right) -> left));
        if (commands.size() != codes.size()) {
            throw new IllegalArgumentException("机器人角色包含不存在的命令");
        }
        // 角色命令关系由角色编辑一次性重建，避免保留已取消的旧绑定。
        commandBindingRepository.deleteAllByRobotRoleId(role.getId());
        commandBindingRepository.flush();
        for (String code : codes) {
            BotCommandRobotRole binding = new BotCommandRobotRole();
            binding.setCommandId(commands.get(code).getId());
            binding.setRobotRoleId(role.getId());
            commandBindingRepository.save(binding);
        }
    }

    private BotCommand createDefaultCommand(
            CommandDescriptor descriptor) {
        BotCommand command = new BotCommand();
        command.setCode(descriptor.code());
        command.setName(descriptor.name());
        command.setGameCode("system");
        command.setDescription(descriptor.description());
        command.setEnabled(true);
        command.setShowInHelp(true);
        command.setOutputMode(CommandOutputMode.TEXT);
        command.setCooldownSeconds(0);
        return commandRepository.save(command);
    }

    private String normalize(String value) {
        return StrUtil.trim(value).toLowerCase(Locale.ROOT);
    }

    private String trimToNull(String value) {
        return StrUtil.isBlank(value) ? null : value.trim();
    }
}
