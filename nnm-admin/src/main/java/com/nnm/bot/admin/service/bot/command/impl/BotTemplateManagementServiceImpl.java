package com.nnm.bot.admin.service.bot.command.impl;

import com.nnm.bot.admin.constant.bot.BotManagementErrorCode;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.nnm.bot.admin.pojo.entity.bot.command.BotCommandTemplate;
import com.nnm.bot.admin.pojo.dto.bot.command.BotTemplateDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotTemplateSaveDTO;
import com.nnm.bot.admin.repository.bot.command.BotCommandTemplateRepository;
import com.nnm.bot.admin.repository.bot.command.BotCommandRepository;
import com.nnm.bot.admin.service.bot.command.BotTemplateManagementService;
import com.nnm.bot.admin.pojo.entity.bot.command.BotCommand;
import com.nnm.bot.admin.validation.command.BotCommandPresentationValidator;
import com.nnm.bot.core.plugin.registry.CommandRegistry;
import com.nnm.bot.common.error.BusinessException;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/** 管理命令展示模板，并在事务提交后刷新运行时注册表。 */
@Service
public class BotTemplateManagementServiceImpl implements BotTemplateManagementService {

    @Resource
    private BotCommandTemplateRepository templateRepository;
    @Resource
    private BotCommandRepository commandRepository;
    @Resource
    private CommandRegistry commandRegistry;
    @Resource
    private BotCommandPresentationValidator presentationValidator;

    @Override
    public List<BotTemplateDTO> list() {
        Map<Long, Long> selected = commandRepository.findAll().stream().map(BotCommand::getTemplateId)
            .filter(Objects::nonNull).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        return templateRepository.findAll().stream().sorted(Comparator.comparing(BotCommandTemplate::getTemplateCode))
            .map(template -> new BotTemplateDTO(template.getId(), template.getTemplateCode(), template.getName(),
                template.getCommandId(), template.getSortOrder(), template.getHtmlContent(), template.isEnabled(),
                selected.getOrDefault(template.getId(), 0L), template.getCreatedAt(), template.getUpdatedAt()))
            .toList();
    }

    @Override
    @Transactional
    public long create(BotTemplateSaveDTO request) {
        String code = normalize(request.templateCode());
        if (templateRepository.findAll().stream().anyMatch(item -> item.getTemplateCode().equalsIgnoreCase(code))) {
            throw new BusinessException(BotManagementErrorCode.TEMPLATE_CODE_CONFLICT);
        }
        BotCommandTemplate template = new BotCommandTemplate();
        apply(template, request, code);
        presentationValidator.validate(List.of(template));
        long id = templateRepository.save(template).getId();
        commandRegistry.refreshAfterCommit();
        return id;
    }

    @Override
    @Transactional
    public void update(long id, BotTemplateSaveDTO request) {
        BotCommandTemplate template = templateRepository.findById(id)
            .orElseThrow(() -> new BusinessException(BotManagementErrorCode.TEMPLATE_NOT_FOUND));
        Long previousCommandId = template.getCommandId();
        String code = normalize(request.templateCode());
        if (templateRepository.findAll().stream()
            .anyMatch(item -> !item.getId().equals(id) && item.getTemplateCode().equalsIgnoreCase(code))) {
            throw new BusinessException(BotManagementErrorCode.TEMPLATE_CODE_CONFLICT);
        }
        apply(template, request, code);
        presentationValidator.validate(List.of(template));
        clearOldSelection(previousCommandId, template);
        templateRepository.save(template);
        commandRegistry.refreshAfterCommit();
    }

    @Override
    @Transactional
    public void delete(long id) {
        BotCommandTemplate template = templateRepository.findById(id)
            .orElseThrow(() -> new BusinessException(BotManagementErrorCode.TEMPLATE_NOT_FOUND));
        if (commandRepository.findAll().stream().anyMatch(command -> Objects.equals(command.getTemplateId(), id)))
            throw new BusinessException(BotManagementErrorCode.TEMPLATE_IN_USE);
        templateRepository.delete(template);
        commandRegistry.refreshAfterCommit();
    }

    private void apply(BotCommandTemplate template, BotTemplateSaveDTO request, String code) {
        if (request.commandId() != null && commandRepository.findById(request.commandId()).isEmpty()) {
            throw new BusinessException(BotManagementErrorCode.TEMPLATE_COMMAND_NOT_FOUND);
        }
        template.setTemplateCode(code);
        template.setName(request.name().trim());
        template.setCommandId(request.commandId());
        template.setSortOrder(
            request.commandId() == null || request.sortOrder() == null ? 0 : Math.max(0, request.sortOrder()));
        if (request.htmlContent() == null || request.htmlContent().isBlank())
            throw new BusinessException(BotManagementErrorCode.HTML_TEMPLATE_REQUIRED);
        template.setHtmlContent(request.htmlContent() == null ? "" : request.htmlContent());
        template.setEnabled(Boolean.TRUE.equals(request.enabled()));
    }

    private void clearOldSelection(Long previousCommandId, BotCommandTemplate template) {
        if (previousCommandId == null)
            return;
        if (Objects.equals(previousCommandId, template.getCommandId()) && template.isEnabled())
            return;
        BotCommand previous = commandRepository.findById(previousCommandId)
            .orElseThrow(() -> new BusinessException(BotManagementErrorCode.PREVIOUS_TEMPLATE_COMMAND_NOT_FOUND));
        if (Objects.equals(previous.getTemplateId(), template.getId())) {
            previous.setTemplateId(null);
            commandRepository.save(previous);
        }
    }

    private String normalize(String code) {
        return code.trim().toLowerCase(Locale.ROOT);
    }
}
