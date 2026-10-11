package com.nnm.bot.admin.service.impl;

import java.util.Comparator;
import java.util.Locale;
import com.nnm.bot.admin.domain.BotCommandTemplate;
import com.nnm.bot.admin.dto.bot.BotTemplateItem;
import com.nnm.bot.admin.dto.bot.BotTemplateSaveRequest;
import com.nnm.bot.admin.repository.BotCommandTemplateRepository;
import com.nnm.bot.admin.repository.BotCommandRepository;
import com.nnm.bot.admin.service.BotTemplateManagementService;
import com.nnm.bot.common.exception.DataNotFoundException;
import com.nnm.bot.core.plugin.registry.CommandRegistry;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BotTemplateManagementServiceImpl implements BotTemplateManagementService {

    @Resource
    private BotCommandTemplateRepository templateRepository;
    @Resource
    private BotCommandRepository commandRepository;
    @Resource
    private CommandRegistry commandRegistry;

    @Override
    public List<BotTemplateItem> list() {
        return templateRepository.findAll().stream()
                .sorted(Comparator.comparing(BotCommandTemplate::getTemplateCode))
                .map(template -> new BotTemplateItem(
                        template.getId(),
                        template.getTemplateCode(),
                        template.getName(),
                        template.getCommandId(),
                        template.getSortOrder(),
                        template.getHtmlContent(),
                        template.isEnabled(),
                        template.getCommandId() == null ? 0 : 1,
                        template.getCreatedAt(),
                        template.getUpdatedAt()))
                .toList();
    }

    @Override
    @Transactional
    public long create(BotTemplateSaveRequest request) {
        String code = normalize(request.templateCode());
        if (templateRepository.findAll().stream()
                .anyMatch(item -> item.getTemplateCode().equalsIgnoreCase(code))) {
            throw new IllegalArgumentException("模板编码已存在");
        }
        BotCommandTemplate template = new BotCommandTemplate();
        apply(template, request, code);
        long id = templateRepository.save(template).getId();
        commandRegistry.refreshAfterCommit();
        return id;
    }

    @Override
    @Transactional
    public void update(long id, BotTemplateSaveRequest request) {
        BotCommandTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("模板不存在"));
        String code = normalize(request.templateCode());
        if (templateRepository.findAll().stream()
                .anyMatch(item -> !item.getId().equals(id)
                        && item.getTemplateCode().equalsIgnoreCase(code))) {
            throw new IllegalArgumentException("模板编码已存在");
        }
        apply(template, request, code);
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

    private void apply(BotCommandTemplate template, BotTemplateSaveRequest request, String code) {
        if (request.commandId() != null
                && commandRepository.findById(request.commandId()).isEmpty()) {
            throw new DataNotFoundException("所属命令不存在");
        }
        template.setTemplateCode(code);
        template.setName(request.name().trim());
        template.setCommandId(request.commandId());
        template.setSortOrder(request.commandId() == null || request.sortOrder() == null
                ? 0
                : Math.max(0, request.sortOrder()));
        template.setHtmlContent(request.htmlContent());
        template.setEnabled(Boolean.TRUE.equals(request.enabled()));
    }

    private String normalize(String code) {
        return code.trim().toLowerCase(Locale.ROOT);
    }
}
