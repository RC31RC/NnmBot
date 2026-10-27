package com.nnm.bot.admin.validation.command;

import com.nnm.bot.core.plugin.model.PresentationPolicy.Override;
import com.nnm.bot.admin.pojo.entity.bot.command.BotCommandTemplate;
import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.core.plugin.model.CommandView;
import com.nnm.bot.core.plugin.model.PresentationPolicy;
import com.nnm.bot.core.constant.CommandPresentationConstants.ViewFormat;
import org.springframework.stereotype.Component;

import java.util.List;

/** 命令与模板编辑共用同一约束，防止修改模板后留下不可执行的展示配置。 */
@Component
public class BotCommandPresentationValidator {

    public void validate(List<CommandView> views, PresentationPolicy policy,
                         List<BotCommandTemplate> templates) {
        BusinessAssert.isTrue(views.stream().map(CommandView::key).distinct().count() == views.size(),
                CommonErrorCode.PARAM_INVALID, "插件声明了重复的数据视图");
        for (Override override : policy.overrides()) {
            validate(views, new PresentationPolicy(override.viewOrder(), override.composition(), override.interaction(), List.of()), templates);
        }
        BusinessAssert.isTrue(policy.viewOrder().stream().allMatch(format -> views.isEmpty()
                        ? format == ViewFormat.TEXT
                        : views.stream().allMatch(view -> view.formats().contains(format))),
                CommonErrorCode.PARAM_INVALID, "展示策略包含插件未声明的展示方式");
        for (BotCommandTemplate template : templates) {
            BusinessAssert.isTrue(views.stream().anyMatch(view -> view.key().equals(template.getViewKey())
                            && (template.getHtmlContent() == null || template.getHtmlContent().isBlank() || view.formats().contains(ViewFormat.IMAGE))
                            && (template.getMarkdownContent() == null || template.getMarkdownContent().isBlank() || view.formats().contains(ViewFormat.MARKDOWN))),
                    CommonErrorCode.PARAM_INVALID, "HTML 模板必须绑定插件声明的图片视图");
        }
        if (policy.viewOrder().contains(ViewFormat.IMAGE) && !policy.viewOrder().contains(ViewFormat.TEXT)) {
            BusinessAssert.isTrue(!views.isEmpty() && views.stream().allMatch(view -> templates.stream()
                            .anyMatch(template -> template.isEnabled() && view.key().equals(template.getViewKey())
                                    && template.getHtmlContent() != null && !template.getHtmlContent().isBlank())),
                    CommonErrorCode.PARAM_INVALID, "仅使用图片视图时，每个数据视图必须关联已启用的 HTML 模板");
        }
        if (policy.viewOrder().contains(ViewFormat.MARKDOWN) && !policy.viewOrder().contains(ViewFormat.TEXT)) {
            BusinessAssert.isTrue(views.stream().allMatch(view -> templates.stream().anyMatch(template -> template.isEnabled()
                            && view.key().equals(template.getViewKey()) && template.getMarkdownContent() != null
                            && !template.getMarkdownContent().isBlank() && template.getPlainTextContent() != null
                            && !template.getPlainTextContent().isBlank())),
                    CommonErrorCode.PARAM_INVALID, "Markdown 视图需要独立模板和文字降级模板");
        }
    }
}
