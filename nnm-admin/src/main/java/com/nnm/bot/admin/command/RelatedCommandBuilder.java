package com.nnm.bot.admin.command;

import com.nnm.bot.core.constant.CommandPresentationConstants.ParameterType;

import java.util.List;
import com.nnm.bot.core.constant.MatchType;
import com.nnm.bot.core.plugin.model.RuntimeCommand;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import com.nnm.bot.core.plugin.model.RelatedParameter;

@Component
public class RelatedCommandBuilder {
    public void validateBindings(RuntimeCommand target, Map<String, String> bindings) {
        List<RelatedParameter> parameters = target.plugin().relatedParameters();
        if (parameters.size() > 8 || parameters.stream().map(parameter -> parameter.name()).distinct().count() != parameters.size()
                || parameters.stream().limit(Math.max(0, parameters.size() - 1)).anyMatch(parameter -> parameter.type() == ParameterType.TEXT)) {
            throw new IllegalArgumentException("关联参数必须唯一，只有最后一个参数允许包含空格");
        }
        Set<String> declared = target.plugin().relatedParameters().stream().map(parameter -> parameter.name()).collect(Collectors.toSet());
        if (target.descriptor().matchType() == MatchType.EXACT && !bindings.isEmpty()
                || target.descriptor().matchType() != MatchType.EXACT && declared.isEmpty()
                || !declared.equals(bindings.keySet()) || bindings.values().stream()
                .anyMatch(key -> key == null || !key.matches("[a-z][a-zA-Z0-9_.]{0,63}"))) {
            throw new IllegalArgumentException("关联参数绑定必须完整对应目标声明；EXACT 不允许附加参数");
        }
    }

    public String command(RuntimeCommand target, Map<String, String> bindings, Map<String, String> context) {
        validateBindings(target, bindings);
        String trigger = target.descriptor().lookupTriggers().getFirst().name();
        StringBuilder text = new StringBuilder(trigger);
        for (RelatedParameter parameter : target.plugin().relatedParameters()) {
            text.append(' ').append(parameter.validate(context.get(bindings.get(parameter.name()))));
        }
        if (text.length() > 512) throw new IllegalArgumentException("生成命令过长");
        return text.toString();
    }
}
