package com.nnm.bot.admin.service.bot.command.impl;

import com.nnm.bot.core.plugin.api.BotPlugin;
import java.util.Comparator;
import java.util.Locale;
import com.nnm.bot.admin.pojo.entity.bot.command.BotCommandTemplate;
import com.nnm.bot.admin.pojo.dto.bot.command.BotTemplateDTO;
import com.nnm.bot.admin.pojo.dto.bot.command.BotTemplateSaveDTO;
import com.nnm.bot.admin.repository.bot.command.BotCommandTemplateRepository;
import com.nnm.bot.admin.repository.bot.command.BotCommandRepository;
import com.nnm.bot.admin.service.bot.command.BotTemplateManagementService;
import com.nnm.bot.common.exception.DataNotFoundException;
import com.nnm.bot.core.plugin.registry.CommandRegistry;
import com.nnm.bot.admin.pojo.entity.bot.command.BotCommand;
import com.nnm.bot.admin.validation.command.BotCommandPresentationValidator;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

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
        return templateRepository.findAll().stream()
                .sorted(Comparator.comparing(BotCommandTemplate::getTemplateCode))
                .map(template -> new BotTemplateDTO(
                        template.getId(),
                        template.getTemplateCode(),
                        template.getViewKey(),
                        template.getName(),
                        template.getCommandId(),
                        template.getSortOrder(),
                        template.getHtmlContent(),
                        template.getMarkdownContent(),
                        template.getPlainTextContent(),
                        template.isEnabled(),
                        template.getCommandId() == null ? 0 : 1,
                        template.getCreatedAt(),
                        template.getUpdatedAt()))
                .toList();
    }

    @Override
    @Transactional
    public long create(BotTemplateSaveDTO request) {
        String code = normalize(request.templateCode());
        if (templateRepository.findAll().stream()
                .anyMatch(item -> item.getTemplateCode().equalsIgnoreCase(code))) {
            throw new IllegalArgumentException("模板编码已存在");
        }
        BotCommandTemplate template = new BotCommandTemplate();
        apply(template, request, code);
        validateBinding(request.commandId(), template);
        long id = templateRepository.save(template).getId();
        commandRegistry.refreshAfterCommit();
        return id;
    }

    @Override
    @Transactional
    public void update(long id, BotTemplateSaveDTO request) {
        BotCommandTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("模板不存在"));
        Long previousCommandId = template.getCommandId();
        String code = normalize(request.templateCode());
        if (templateRepository.findAll().stream()
                .anyMatch(item -> !item.getId().equals(id)
                        && item.getTemplateCode().equalsIgnoreCase(code))) {
            throw new IllegalArgumentException("模板编码已存在");
        }
        apply(template, request, code);
        validateBinding(previousCommandId, template);
        if (!Objects.equals(previousCommandId, template.getCommandId())) {
            validateBinding(template.getCommandId(), template);
        }
        templateRepository.save(template);
        commandRegistry.refreshAfterCommit();
    }

    @Override
    @Transactional
    public void delete(long id) {
        BotCommandTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("模板不存在"));
        if (template.getCommandId() != null) {
            throw new IllegalArgumentException("模板仍被命令使用，不能删除");
        }
        templateRepository.delete(template);
        commandRegistry.refreshAfterCommit();
    }

    private void apply(BotCommandTemplate template, BotTemplateSaveDTO request, String code) {
        if (request.commandId() != null
                && commandRepository.findById(request.commandId()).isEmpty()) {
            throw new DataNotFoundException("所属命令不存在");
        }
        template.setTemplateCode(code);
        template.setViewKey(request.viewKey());
        template.setName(request.name().trim());
        template.setCommandId(request.commandId());
        template.setSortOrder(request.commandId() == null || request.sortOrder() == null
                ? 0
                : Math.max(0, request.sortOrder()));
        if ((request.htmlContent() == null || request.htmlContent().isBlank())
                && (request.markdownContent() == null || request.markdownContent().isBlank())) {
            throw new IllegalArgumentException("请提供 HTML 或 Markdown 模板");
        }
        if (request.markdownContent() != null && !request.markdownContent().isBlank()
                && (request.plainTextContent() == null || request.plainTextContent().isBlank())) {
            throw new IllegalArgumentException("Markdown 模板需要显式的文字降级模板");
        }
        template.setHtmlContent(request.htmlContent() == null ? "" : request.htmlContent());
        template.setMarkdownContent(request.markdownContent());
        template.setPlainTextContent(request.plainTextContent());
        template.setEnabled(Boolean.TRUE.equals(request.enabled()));
    }

    private void validateBinding(Long commandId, BotCommandTemplate changed) {
        if (commandId == null) return;
        BotCommand command = commandRepository.findById(commandId)
                .orElseThrow(() -> new DataNotFoundException("所属命令不存在"));
        BotPlugin plugin = commandRegistry.plugin(command.getCode())
                .orElseThrow(() -> new DataNotFoundException("所属命令插件不存在"));
        ArrayList<BotCommandTemplate> templates = new ArrayList<>(templateRepository
                .findAllByCommandIdOrderBySortOrderAscIdAsc(commandId).stream()
                .filter(template -> !Objects.equals(template.getId(), changed.getId())).toList());
        if (commandId.equals(changed.getCommandId())) templates.add(changed);
        presentationValidator.validate(plugin.views(), command.getPresentation(), templates);
    }

    private String normalize(String code) {
        return code.trim().toLowerCase(Locale.ROOT);
    }
}
