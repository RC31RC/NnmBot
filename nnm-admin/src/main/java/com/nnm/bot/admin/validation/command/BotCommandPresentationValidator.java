package com.nnm.bot.admin.validation.command;

import com.nnm.bot.admin.pojo.entity.bot.command.BotCommandTemplate;
import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.validation.BusinessAssert;
import org.springframework.stereotype.Component;

import java.util.List;

/** 验证命令选择的 HTML 图片模板。 */
@Component
public class BotCommandPresentationValidator {

    public void validate(List<BotCommandTemplate> templates) {
        BusinessAssert.isTrue(templates.size() <= 1, CommonErrorCode.PARAM_INVALID, "每个命令只能选择一个 HTML 模板");
        for (BotCommandTemplate template : templates) {
            BusinessAssert.isTrue(template.getHtmlContent() != null && !template.getHtmlContent().isBlank(),
                CommonErrorCode.PARAM_INVALID, "命令模板必须提供 HTML 内容");
        }
    }
}
