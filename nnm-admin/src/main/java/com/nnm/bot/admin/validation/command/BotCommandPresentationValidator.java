package com.nnm.bot.admin.validation.command;

import cn.hutool.core.text.CharSequenceUtil;

import com.nnm.bot.admin.constant.bot.BotManagementErrorCode;
import com.nnm.bot.admin.pojo.entity.bot.command.BotCommandTemplate;
import com.nnm.bot.common.validation.BusinessAssert;
import org.springframework.stereotype.Component;

import java.util.List;

/** 验证命令选择的 HTML 图片模板。 */
@Component
public class BotCommandPresentationValidator {

    public void validate(List<BotCommandTemplate> templates) {
        BusinessAssert.isTrue(templates.size() <= 1, BotManagementErrorCode.MULTIPLE_HTML_TEMPLATES);
        for (BotCommandTemplate template : templates) {
            BusinessAssert.isTrue(
                template.getHtmlContent() != null && !CharSequenceUtil.isBlank(template.getHtmlContent()),
                BotManagementErrorCode.TEMPLATE_HTML_REQUIRED);
        }
    }
}
