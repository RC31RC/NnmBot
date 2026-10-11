package com.nnm.bot.warframe.service.impl;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nnm.bot.warframe.model.entity.WarframeCommunityWorldStateRule;
import com.nnm.bot.warframe.constant.WarframeCommunityRecordSource;
import com.nnm.bot.warframe.constant.WarframeCommunitySource;
import com.nnm.bot.warframe.pojo.WarframeCommunityStateTranslationDetails;
import com.nnm.bot.warframe.repository.WarframeCommunityWorldStateRuleRepository;
import com.nnm.bot.warframe.service.WarframeWorldStateRuleService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class WarframeWorldStateRuleServiceImpl implements WarframeWorldStateRuleService {
    /*
     * state_translation_local 目前只有 info-api 和自维护候选。优先级只属于这个
     * WorldState 规则 Service，不能推广成普通 STATE_TRANSLATION 的全局规则。
     */
    private static final List<String> FLAT_RULE_SOURCE_PRIORITY = List.of(
            "SELF_MAINTAINED",
            WarframeCommunitySource.WF_INFO_API_RULE.code());

    @Resource
    private WarframeCommunityWorldStateRuleRepository repository;
    @Resource
    private ObjectMapper objectMapper;

    @Override
    public Map<String, JsonNode> sections(String datasetCode) {
        Map<String, JsonNode> sections = new LinkedHashMap<>();
        // 每个规则集仅少量分区；按需读库使管理端修改立即生效，不再维护一份独立缓存。
        repository.findByDatasetCodeAndEnabledTrueOrderByRecordKeyAsc(datasetCode).stream()
                .filter(record -> "en".equals(record.getLocale()) || "zh".equals(record.getLocale()))
                .sorted(Comparator.comparing((WarframeCommunityWorldStateRule record) ->
                                "zh".equals(record.getLocale()))
                        .thenComparing(record -> record.getRecordSource()
                                == WarframeCommunityRecordSource.SELF_MAINTAINED))
                .forEach(record -> merge(sections, record));
        return Map.copyOf(sections);
    }

    @Override
    public List<WarframeCommunityStateTranslationDetails> stateTranslations() {
        Map<String, WarframeCommunityStateTranslationDetails> selected = new LinkedHashMap<>();
        repository.findByDatasetCodeAndEnabledTrueOrderByRecordKeyAsc(
                        WarframeCommunitySource.WF_INFO_API_RULE.datasetCode()).stream()
                .filter(record -> "en".equals(record.getLocale())
                        || "zh".equals(record.getLocale()))
                // 选择整条中文候选；英文仅补缺失的业务键，不逐字段拼接来源。
                .sorted(Comparator.comparing((WarframeCommunityWorldStateRule record) ->
                                !"zh".equals(record.getLocale()))
                        .thenComparingInt(this::sourceRank)
                        .thenComparing(WarframeCommunityWorldStateRule::getRecordKey))
                .forEach(record -> {
                    var rule = flatRule(record);
                    if (rule != null) {
                        selected.putIfAbsent(rule.uniqueName(), rule);
                    }
                });
        return selected.values().stream()
                .sorted(Comparator.comparing(WarframeCommunityStateTranslationDetails::uniqueName))
                .toList();
    }

    private WarframeCommunityStateTranslationDetails flatRule(
            WarframeCommunityWorldStateRule record) {
        try {
            JsonNode ruleJson = objectMapper.readTree(record.getCurrentJson());
            String uniqueName = text(ruleJson, "uniqueName", "recordKey");
            if (StrUtil.isBlank(uniqueName)) {
                log.warn("Invalid flat WorldState rule without uniqueName: {}",
                        record.getRecordKey());
                return null;
            }
            return new WarframeCommunityStateTranslationDetails(
                    uniqueName, text(ruleJson, "name"), text(ruleJson, "description"));
        } catch (JsonProcessingException exception) {
            log.warn("Invalid flat WorldState rule JSON: {}", record.getRecordKey(), exception);
            return null;
        }
    }

    private int sourceRank(WarframeCommunityWorldStateRule record) {
        String source = record.getRecordSource() == WarframeCommunityRecordSource.SELF_MAINTAINED
                ? "SELF_MAINTAINED" : record.getSourceCode();
        int index = source == null ? -1 : FLAT_RULE_SOURCE_PRIORITY.indexOf(source);
        if (index < 0) {
            throw new IllegalStateException("WorldState 扁平规则存在未登记来源：" + source);
        }
        return index;
    }

    private static String text(JsonNode json, String... fields) {
        if (json == null) {
            return null;
        }
        for (String field : fields) {
            JsonNode value = json.get(field);
            if (value != null && value.isTextual() && StrUtil.isNotBlank(value.asText())) {
                return StrUtil.trim(value.asText());
            }
        }
        return null;
    }

    private void merge(Map<String, JsonNode> sections, WarframeCommunityWorldStateRule record) {
        try {
            JsonNode ruleJson = objectMapper.readTree(record.getCurrentJson());
            if (ruleJson == null || !ruleJson.path("key").isTextual()
                    || !ruleJson.path("value").isObject()) {
                log.warn("Invalid WorldState rule section: {}", record.getRecordKey());
                return;
            }
            String key = ruleJson.path("key").asText();
            ObjectNode target = (ObjectNode) sections.computeIfAbsent(key,
                    ignored -> objectMapper.createObjectNode());
            mergeFields(target, (ObjectNode) ruleJson.get("value"));
        } catch (JsonProcessingException exception) {
            log.warn("Invalid WorldState rule JSON: {}", record.getRecordKey(), exception);
        }
    }

    private static void mergeFields(ObjectNode target, ObjectNode source) {
        source.fields().forEachRemaining(entry -> {
            JsonNode value = entry.getValue();
            if (value.isObject() && target.path(entry.getKey()).isObject()) {
                mergeFields((ObjectNode) target.get(entry.getKey()), (ObjectNode) value);
            } else if (!value.isNull() && !(value.isTextual() && value.asText().isBlank())) {
                target.set(entry.getKey(), value.deepCopy());
            }
        });
    }
}
