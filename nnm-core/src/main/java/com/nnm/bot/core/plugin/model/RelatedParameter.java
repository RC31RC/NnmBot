package com.nnm.bot.core.plugin.model;

import com.nnm.bot.core.constant.CommandPresentationConstants.ParameterType;

/** 目标插件承诺接收的参数；只有显式 actionContext 能参与命令生成。 */
public record RelatedParameter(String name, ParameterType type, int maxLength) {

    public RelatedParameter {
        if (name == null || !name.matches("[a-z][a-zA-Z0-9_.]{0,63}") || type == null || maxLength < 1 || maxLength > 400) {
            throw new IllegalArgumentException("Invalid related parameter contract");
        }
    }
    public String validate(String value) {
        if (value == null || value.isBlank() || !value.equals(value.strip()) || value.length() > maxLength
                || value.codePoints().anyMatch(code -> Character.isISOControl(code) || Character.getType(code) == Character.FORMAT)
                || value.contains("[CQ:") || value.contains("<@") || value.indexOf('\\') >= 0
                || value.indexOf('"') >= 0 || value.indexOf('\'') >= 0
                || type == ParameterType.TOKEN && !value.matches("[a-zA-Z0-9_./:-]+")
                || type == ParameterType.POSITIVE_INTEGER && !value.matches("[1-9][0-9]{0,8}")) {
            throw new IllegalArgumentException("参数缺失、超长、歧义或不符合目标格式: " + name);
        }
        return value;
    }
}
