package com.nnm.bot.admin.service.bot.command.impl;

import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandGroupSaveDTO.BotCommandGroupMemberSaveDTO;
import com.nnm.bot.admin.pojo.entity.bot.command.BotCommandGroup;
import com.nnm.bot.admin.pojo.entity.bot.command.BotCommandGroupMember;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandGroupDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandGroupMemberDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandGroupSaveDTO;
import com.nnm.bot.admin.repository.bot.command.BotCommandGroupMemberRepository;
import com.nnm.bot.admin.repository.bot.command.BotCommandGroupRepository;
import com.nnm.bot.admin.service.bot.command.BotCommandGroupService;
import com.nnm.bot.common.exception.DataNotFoundException;
import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.admin.adapter.command.DatabaseCommandRelatedActionProvider;
import com.nnm.bot.admin.pojo.query.bot.command.BotCommandGroupPreviewQuery;
import com.nnm.bot.admin.pojo.dto.bot.command.BotCommandGroupPreviewDTO;
import com.nnm.bot.core.message.model.BotMessageContext;
import java.util.ArrayList;
import com.nnm.bot.admin.command.RelatedCommandBuilder;
import com.nnm.bot.core.plugin.registry.CommandRegistry;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BotCommandGroupServiceImpl implements BotCommandGroupService {
    @Resource
    private BotCommandGroupRepository groups;
    @Resource
    private BotCommandGroupMemberRepository members;
    @Resource
    private CommandRegistry commandRegistry;
    @Resource
    private RelatedCommandBuilder builder;
    @Resource
    private DatabaseCommandRelatedActionProvider related;

    @Override
    public List<BotCommandGroupDTO> list() {
        return groups.findAllByOrderByCodeAsc().stream()
                .map(this::item)
                .toList();
    }

    @Override
    public List<BotCommandGroupPreviewDTO> preview(long id, BotCommandGroupPreviewQuery request) {
        BotCommandGroup group = groups.findById(id).orElseThrow(() -> new DataNotFoundException("关联命令组不存在"));
        BusinessAssert.isTrue(group.getVersion() == request.version(), CommonErrorCode.DATA_CONFLICT, "关联组已更新，请重新加载");
        BotMessageContext context = BotMessageContext.of(request.platform(), request.scope(), request.botAccountId(), request.senderId(),
                request.conversationId(), "preview", "preview", "preview", null);
        ArrayList<BotCommandGroupPreviewDTO> rows = new ArrayList<BotCommandGroupPreviewDTO>();
        for (BotCommandGroupMember member : members.findAllByGroupIdOrderBySortOrderAscIdAsc(id)) {
            if (request.sourceCode() != null && !request.sourceCode().isBlank()
                    && !member.getCommandCode().equalsIgnoreCase(request.sourceCode())) continue;
            commandRegistry.command(member.getCommandCode()).ifPresent(source -> rows.addAll(
                    related.preview(context, group, source, request.actionContext())));
        }
        return List.copyOf(rows);
    }

    @Override
    @Transactional
    public long save(BotCommandGroupSaveDTO request) {
        String code = normalize(request.code());
        BotCommandGroup group = request.id() == null ? new BotCommandGroup()
                : groups.findById(request.id()).orElseThrow(() -> new DataNotFoundException("关联命令组不存在"));
        BusinessAssert.isTrue(request.id() == null || request.version() != null && group.getVersion() == request.version(),
                CommonErrorCode.DATA_CONFLICT, "关联命令组已被其他管理员修改，请重新加载");
        BusinessAssert.isTrue(groups.findByCodeIgnoreCase(code)
                        .filter(existing -> !Objects.equals(existing.getId(), group.getId())).isEmpty(),
                CommonErrorCode.DATA_CONFLICT, "关联命令组编码已存在");
        Set<String> commandCodes = new HashSet<>();
        for (BotCommandGroupMemberSaveDTO member : request.members()) {
            String commandCode = normalize(member.commandCode());
            BusinessAssert.isTrue(commandCodes.add(commandCode), CommonErrorCode.PARAM_INVALID, "关联命令不能重复");
            BusinessAssert.isTrue(commandRegistry.command(commandCode)
                            .filter(command -> command.configuration().enabled()
                                    && commandRegistry.metadata(command.plugin()).allowRelatedAction()).isPresent(),
                    CommonErrorCode.PARAM_INVALID, "关联命令不存在、已停用或未声明导航参数契约: " + commandCode);
            try {
                builder.validateBindings(commandRegistry.command(commandCode).orElseThrow(), member.parameterBindings());
            } catch (IllegalArgumentException invalid) {
                BusinessAssert.isTrue(false, CommonErrorCode.PARAM_INVALID, invalid.getMessage());
            }
            BusinessAssert.isTrue(members.findAllByCommandCodeIgnoreCase(commandCode).stream()
                            .allMatch(existing -> Objects.equals(existing.getGroupId(), group.getId())),
                    CommonErrorCode.DATA_CONFLICT, "关联命令已属于其他命令组: " + commandCode);
            BusinessAssert.isTrue(member.sortOrder() >= 0, CommonErrorCode.PARAM_INVALID, "成员排序不能为负数");
        }
        group.setCode(code);
        group.setName(request.name().trim());
        group.setEnabled(request.enabled());
        BotCommandGroup saved = groups.save(group);
        members.deleteAllByGroupId(saved.getId());
        members.flush();
        List<BotCommandGroupMember> replacements = request.members().stream().map(member -> {
            BotCommandGroupMember entity = new BotCommandGroupMember();
            entity.setGroupId(saved.getId());
            entity.setCommandCode(normalize(member.commandCode()));
            entity.setTitle(member.title().trim());
            entity.setSortOrder(member.sortOrder());
            entity.setParameterBindings(member.parameterBindings());
            return entity;
        }).toList();
        members.saveAll(replacements);
        return saved.getId();
    }

    @Override
    @Transactional
    public void delete(long id, int version) {
        BotCommandGroup group = groups.findById(id).orElseThrow(() -> new DataNotFoundException("关联命令组不存在"));
        BusinessAssert.isTrue(group.getVersion() == version, CommonErrorCode.DATA_CONFLICT,
                "关联命令组已被其他管理员修改，请重新加载");
        members.deleteAllByGroupId(id);
        members.flush();
        groups.delete(group);
    }

    private BotCommandGroupDTO item(BotCommandGroup group) {
        return new BotCommandGroupDTO(group.getId(), group.getCode(), group.getName(), group.isEnabled(),
                group.getVersion(), members.findAllByGroupIdOrderBySortOrderAscIdAsc(group.getId()).stream()
                        .map(member -> new BotCommandGroupMemberDTO(member.getCommandCode(), member.getTitle(), member.getSortOrder(), member.getParameterBindings()))
                        .toList());
    }

    private String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
