package com.nnm.bot.warframe.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.nnm.bot.warframe.pojo.WarframeCommunityStateTranslationDetails;

import java.util.List;
import java.util.Map;

public interface WarframeWorldStateRuleService {
    /** 读取一个规则集；中文覆盖英文回退，人工配置在同语言下优先。 */
    Map<String, JsonNode> sections(String datasetCode);

    /**
     * 读取 state_translation_local.json 这种扁平规则。
     *
     * <p>它与普通 STATE_TRANSLATION 是两个业务模型，返回结果只保留命令需要的
     * 稳定字段，不要求插件自行解析实体中的完整 JSON。</p>
     */
    List<WarframeCommunityStateTranslationDetails> stateTranslations();
}
