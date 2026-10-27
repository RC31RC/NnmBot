package com.nnm.bot.admin.service.support;

import com.nnm.bot.admin.validation.command.BotCommandPresentationValidator;
import com.nnm.bot.admin.pojo.entity.bot.command.BotCommandTemplate;
import com.nnm.bot.common.exception.BusinessException;
import com.nnm.bot.core.plugin.model.CommandView;
import com.nnm.bot.core.constant.CommandPresentationConstants.CompositionPolicy;
import com.nnm.bot.core.constant.CommandPresentationConstants.InteractionPolicy;
import com.nnm.bot.core.plugin.model.PresentationPolicy;
import com.nnm.bot.core.constant.CommandPresentationConstants.ViewFormat;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BotCommandPresentationValidatorTest {
    private final BotCommandPresentationValidator validator = new BotCommandPresentationValidator();
    private final List<CommandView> views = List.of(
            CommandView.textImageAndMarkdown("market.orders"), CommandView.textImageAndMarkdown("market.statistics"));

    @Test
    void imageOnlyRequiresCoverageOfEveryViewInsteadOfAnyEnabledTemplate() {
        assertThatThrownBy(() -> validator.validate(views, policy(ViewFormat.IMAGE),
                List.of(template("market.orders", true))))
                .isInstanceOf(BusinessException.class).hasMessageContaining("每个数据视图");
        assertThatCode(() -> validator.validate(views, policy(ViewFormat.IMAGE),
                List.of(template("market.orders", true), template("market.statistics", true))))
                .doesNotThrowAnyException();
    }

    @Test
    void disabledTemplateCannotCoverRequiredImageView() {
        assertThatThrownBy(() -> validator.validate(views, policy(ViewFormat.IMAGE),
                List.of(template("market.orders", true), template("market.statistics", false))))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void explicitTextFallbackAllowsAViewWithoutAnImageTemplate() {
        assertThatCode(() -> validator.validate(views, policy(ViewFormat.IMAGE, ViewFormat.TEXT),
                List.of(template("market.orders", true)))).doesNotThrowAnyException();
    }

    @Test
    void unrelatedTemplateCannotBeBoundEvenWhenTextIsAvailable() {
        assertThatThrownBy(() -> validator.validate(views, PresentationPolicy.defaults(),
                List.of(template("system.status", true))))
                .isInstanceOf(BusinessException.class).hasMessageContaining("插件声明的图片视图");
    }

    @Test
    void plainContentPluginCannotAdvertiseADataImageRenderer() {
        assertThatThrownBy(() -> validator.validate(List.of(), policy(ViewFormat.IMAGE, ViewFormat.TEXT), List.of()))
                .isInstanceOf(BusinessException.class).hasMessageContaining("未声明");
        assertThatCode(() -> validator.validate(List.of(), PresentationPolicy.defaults(), List.of()))
                .doesNotThrowAnyException();
    }

    private PresentationPolicy policy(ViewFormat... formats) {
        return new PresentationPolicy(List.of(formats), CompositionPolicy.PREFER_SINGLE, InteractionPolicy.COMMAND_TEXT, List.of());
    }

    private BotCommandTemplate template(String key, boolean enabled) {
        BotCommandTemplate template = new BotCommandTemplate();
        template.setViewKey(key);
        template.setEnabled(enabled);
        template.setHtmlContent("<html></html>");
        return template;
    }
}
