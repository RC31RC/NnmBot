package com.nnm.bot.warframe.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nnm.bot.warframe.dto.WarframeCommunityFieldDiff;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 负责社区记录的字段级合并，不持有数据库状态。
 *
 * <p>人工字段默认保留当前值；只有管理员明确选择采用上游值时，
 * 该字段才会从人工修改集合中移除。</p>
 */
public class CommunityJsonMergeEngine {

    private final ObjectMapper objectMapper;

    public CommunityJsonMergeEngine(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public MergeResult merge(
            String dataset,
            String recordKey,
            JsonNode current,
            JsonNode upstream,
            JsonNode manualFields) {
        ObjectNode currentObject = object(current);
        ObjectNode upstreamObject = object(upstream);
        ObjectNode manualObject = object(manualFields);
        ObjectNode merged = currentObject.deepCopy();
        List<WarframeCommunityFieldDiff> diffs = new ArrayList<>();

        Set<String> fields = new LinkedHashSet<>();
        currentObject.fieldNames().forEachRemaining(fields::add);
        upstreamObject.fieldNames().forEachRemaining(fields::add);
        manualObject.fieldNames().forEachRemaining(fields::add);

        for (String field : fields) {
            JsonNode currentValue = value(currentObject, field);
            JsonNode upstreamValue = value(upstreamObject, field);
            boolean manuallyModified = manualObject.has(field);
            if (!manuallyModified) {
                putOrRemove(merged, field, upstreamValue);
                if (!same(currentValue, upstreamValue)) {
                    diffs.add(diff(dataset, recordKey, field, currentValue,
                            upstreamValue, null, false, "USE_UPSTREAM"));
                }
                continue;
            }
            if (!same(currentValue, upstreamValue)) {
                diffs.add(diff(dataset, recordKey, field, currentValue,
                        upstreamValue, manualObject.get(field), true, "KEEP_MANUAL"));
            }
        }
        return new MergeResult(merged, manualObject, diffs);
    }

    public DecisionResult applyDecision(
            JsonNode current,
            JsonNode upstream,
            JsonNode manualFields,
            List<WarframeCommunityFieldDiff> diffs,
            Set<String> useUpstreamFields) {
        ObjectNode result = object(current);
        ObjectNode manual = object(manualFields);
        for (WarframeCommunityFieldDiff diff : diffs) {
            if (!useUpstreamFields.contains(diff.field())) {
                continue;
            }
            putOrRemove(result, diff.field(), value(object(upstream), diff.field()));
            manual.remove(diff.field());
        }
        return new DecisionResult(result, manual);
    }

    private WarframeCommunityFieldDiff diff(
            String dataset,
            String recordKey,
            String field,
            JsonNode current,
            JsonNode upstream,
            JsonNode manual,
            boolean conflict,
            String decision) {
        return new WarframeCommunityFieldDiff(
                dataset, recordKey, field, jsonValue(current), jsonValue(upstream),
                jsonValue(manual), conflict, decision);
    }

    private Object jsonValue(JsonNode value) {
        if (value == null || value.isMissingNode() || value.isNull()) {
            return null;
        }
        return objectMapper.convertValue(value, Object.class);
    }

    private ObjectNode object(JsonNode value) {
        return value != null && value.isObject()
                ? (ObjectNode) value.deepCopy()
                : objectMapper.createObjectNode();
    }

    private static JsonNode value(ObjectNode object, String field) {
        JsonNode value = object.get(field);
        return value == null ? null : value;
    }

    private static void putOrRemove(ObjectNode object, String field, JsonNode value) {
        if (value == null || value.isMissingNode()) {
            object.remove(field);
        } else {
            object.set(field, value.deepCopy());
        }
    }

    private static boolean same(JsonNode first, JsonNode second) {
        if (first == null || first.isMissingNode() || first.isNull()) {
            return second == null || second.isMissingNode() || second.isNull();
        }
        return first.equals(second);
    }

    public record MergeResult(
            ObjectNode merged,
            ObjectNode manualFields,
            List<WarframeCommunityFieldDiff> diffs
    ) {
    }

    public record DecisionResult(
            ObjectNode merged,
            ObjectNode manualFields
    ) {
    }
}
