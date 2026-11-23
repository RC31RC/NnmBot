package com.nnm.bot.warframe.runtime.mapper.worldstate;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import cn.hutool.core.text.CharSequenceUtil;
import com.nnm.bot.warframe.runtime.constant.WarframeBountySupplementStatus;
import com.nnm.bot.warframe.runtime.constant.WarframeWorldStateResolutionStatus;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WarframeWorldStateUnresolvedReferenceDTO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateResolutionVO;

/** 在最终投影上确定仍未完成的字段；同一规则供命令和管理端使用。 */
public final class WarframeWorldStateResolutionMapper {
    private static final Pattern PATH_PART = Pattern.compile("([^.\\[\\]]+)|\\[(\\d+)]");
    private static final Pattern INTERNAL_PATH = Pattern.compile("/(?:Lotus|EE)/[^,，\\s]+");
    private static final Pattern INTERNAL_CODE =
        Pattern.compile("(?:MT_|SORTIE_|DT_|CT_|VoidT|OSTRONS_|SOLARIS_|ENTRATI_)[A-Za-z0-9_]+");
    private static final Set<String> REFERENCE_FIELDS = Set.of("item", "reward", "node", "mission", "missionType",
        "modifier", "boss", "faction", "syndicate", "tier", "type", "title", "challenge", "location", "locationTag",
        "attackerReward", "defenderReward", "upgradeType", "operationType", "description", "challengeDescription",
        "rewardNotice", "rewardItems", "risks", "personalModifiers", "nodes", "attackerMissions", "defenderMissions",
        "attackerRewards", "defenderRewards", "attackerFaction", "defenderFaction", "agentType", "jobType",
        "lastDiscoveredLocation", "mode", "deviation", "ally");

    private WarframeWorldStateResolutionMapper() {}

    /**
     * 合并静态引用诊断和动态业务的部分命中状态；完全解析的字段不携带状态。
     *
     * @param data 已完成所有增强的最终投影。
     * @param unresolved 上游引用解析保留的审核记录。
     * @return 尚需人工确认的字段、原值、已解析内容和候选。
     */
    public static List<WarframeWorldStateResolutionVO> pending(Map<String, Object> data,
        List<WarframeWorldStateUnresolvedReferenceDTO> unresolved) {
        Map<PendingReferenceKey, PendingReference> pending = new LinkedHashMap<>();
        if (unresolved != null) {
            for (WarframeWorldStateUnresolvedReferenceDTO reference : unresolved) {
                Object current = atPath(data, reference.fieldPath());
                String raw = reference.reference();
                if (isReadableTitle(reference.fieldPath(), raw))
                    continue;
                // 后续记录增强已把该字段解析出来时，旧诊断不再显示。
                if (!(current instanceof String text))
                    continue;
                WarframeWorldStateResolutionStatus status = reference.status();
                boolean needsChoice = status.requiresChoice();
                if (!needsChoice
                    && (!clean(text).contains(clean(raw)) || resolvedSiblingAtPath(data, reference.fieldPath(), text)))
                    continue;
                String resolved = resolvedPortion(text, clean(raw));
                if (needsChoice && resolved == null)
                    resolved = resolvedSiblingNameAtPath(data, reference.fieldPath(), text);
                if (needsChoice && resolved == null && !clean(text).contains(clean(raw)))
                    resolved = clean(text);
                add(pending, reference.fieldPath(), raw, status.code(), resolved, reference.candidates());
            }
        }
        visit(data.get("items"), "items", pending);
        return pending.values().stream().map(PendingReference::toVO).toList();
    }

    /**
     * 递归定位最终投影中待确认的引用。
     *
     * @param value 当前值。
     * @param path 当前字段路径。
     * @param pending 已收集的待确认字段。
     */
    private static void visit(Object value, String path, Map<PendingReferenceKey, PendingReference> pending) {
        if (value instanceof List<?> list) {
            for (int index = 0; index < list.size(); index++)
                visit(list.get(index), path + "[" + index + "]", pending);
            return;
        }
        if (value instanceof String text) {
            reviewRaw(path, text, pending);
            return;
        }
        if (!(value instanceof Map<?, ?> row))
            return;
        status(row, path, "challengeResolutionStatus", "challengeKey", "challenge", "challenge", "challengeMatchedKey",
            pending);
        status(row, path, "typeResolutionStatus", "typeKey", "type", "type", "typeMatchedKey", pending);
        String resolutionStatus = row.get("resolutionStatus") instanceof String text ? text : "";
        if (WarframeWorldStateResolutionStatus
            .fromCode(resolutionStatus) == WarframeWorldStateResolutionStatus.ALLY_UNRESOLVED)
            status(row, path, "resolutionStatus", "ally", "ally", "allyName", null, pending);
        else if (row.containsKey("challenge"))
            status(row, path, "resolutionStatus", "challenge", "challenge", "challengeName", null, pending);
        else
            status(row, path, "resolutionStatus", "node", "node", "nodeName", null, pending);
        row.forEach((key, child) -> {
            String field = String.valueOf(key);
            String childPath = path + "." + field;
            if (child instanceof String text && !resolvedSibling(row, field, text))
                reviewRaw(childPath, text, pending);
            else if (child instanceof Map<?, ?> || child instanceof List<?>)
                visit(child, childPath, pending);
        });
    }

    /**
     * 判断路径对应原始键是否已有确定的同级解析名称。
     *
     * @param data 最终投影。
     * @param path 原始键路径。
     * @param raw 原始键。
     * @return 已确认解析且无不完整状态时为 true。
     */
    private static boolean resolvedSiblingAtPath(Map<String, Object> data, String path, String raw) {
        int separator = path.lastIndexOf('.');
        if (separator < 0)
            return false;
        Object parent = atPath(data, path.substring(0, separator));
        return parent instanceof Map<?, ?> row && resolvedSibling(row, path.substring(separator + 1), raw);
    }

    /**
     * 排除保留原值但已由同级名称确认解析的字段。
     *
     * @param row 当前条目。
     * @param field 引用字段。
     * @param raw 原始键。
     * @return 当前字段已完成解析时为 true。
     */
    private static boolean resolvedSibling(Map<?, ?> row, String field, String raw) {
        if (resolvedSiblingName(row, field, raw) == null && !resolvedByCodeKey(row, field, raw))
            return false;
        if (incomplete(row.get(field + "ResolutionStatus")))
            return false;
        if ("challenge".equals(field) && incomplete(row.get("challengeResolutionStatus")))
            return false;
        if ("type".equals(field) && incomplete(row.get("typeResolutionStatus")))
            return false;
        Object status = row.get("resolutionStatus");
        if (!incomplete(status))
            return true;
        String affected = status instanceof String code
            && WarframeWorldStateResolutionStatus.fromCode(code) == WarframeWorldStateResolutionStatus.ALLY_UNRESOLVED
                ? "ally" : row.containsKey("challenge") ? "challenge" : "node";
        return !field.equals(affected);
    }

    /**
     * 读取候选仍未确认时可供核对的同级解析名称。
     *
     * @param data 最终投影。
     * @param path 原始键路径。
     * @param raw 原始键。
     * @return 可用名称，未附加名称时为 null。
     */
    private static String resolvedSiblingNameAtPath(Map<String, Object> data, String path, String raw) {
        int separator = path.lastIndexOf('.');
        if (separator < 0)
            return null;
        Object parent = atPath(data, path.substring(0, separator));
        return parent instanceof Map<?, ?> row ? resolvedSiblingName(row, path.substring(separator + 1), raw) : null;
    }

    /**
     * 读取当前字段已附加的非内部键名称。
     *
     * @param row 当前条目。
     * @param field 引用字段。
     * @param raw 原始键。
     * @return 已附加名称，未找到时为 null。
     */
    private static String resolvedSiblingName(Map<?, ?> row, String field, String raw) {
        Object name = row.get(field + "Name");
        if (!(name instanceof String resolved) || CharSequenceUtil.isBlank(resolved)
            || clean(raw).equals(clean(resolved)) || INTERNAL_PATH.matcher(resolved).find()
            || INTERNAL_CODE.matcher(resolved).find())
            return null;
        return clean(resolved);
    }

    private static boolean resolvedByCodeKey(Map<?, ?> row, String field, String raw) {
        return row.get(field) instanceof String display && clean(display).equals(clean(raw))
            && row.get(field + "Key") instanceof String key && INTERNAL_CODE.matcher(key).matches()
            && !INTERNAL_PATH.matcher(display).find() && !INTERNAL_CODE.matcher(display).matches();
    }

    /**
     * 判断状态是否仍需确认。
     *
     * @param value 动态状态值。
     * @return 非完整状态时为 true。
     */
    private static boolean incomplete(Object value) {
        return value instanceof String code && !CharSequenceUtil.isBlank(code) && !complete(code);
    }

    /**
     * 补充没有显式诊断的内部引用。
     *
     * @param path 字段路径。
     * @param text 字段原值。
     * @param pending 待确认字段集合。
     */
    private static void reviewRaw(String path, String text, Map<PendingReferenceKey, PendingReference> pending) {
        String field = path.substring(path.lastIndexOf('.') + 1).replaceFirst("\\[\\d+]$", "");
        if (!REFERENCE_FIELDS.contains(field))
            return;
        Matcher paths = INTERNAL_PATH.matcher(text);
        boolean found = false;
        while (paths.find()) {
            found = true;
            add(pending, path, paths.group(), WarframeWorldStateResolutionStatus.UNRESOLVED.code(),
                resolvedPortion(text, paths.group()), List.of());
        }
        if (!found && INTERNAL_CODE.matcher(text).matches())
            add(pending, path, text, WarframeWorldStateResolutionStatus.UNRESOLVED.code(), null, List.of());
    }

    /** 标题通常已经是可读文本，不要求它必须命中物品或语言静态表。 */
    private static boolean isReadableTitle(String path, String value) {
        if (path == null || !path.matches("(?:.*\\.)?title"))
            return false;
        return !INTERNAL_PATH.matcher(value).find() && !INTERNAL_CODE.matcher(value).matches();
    }

    /**
     * 按业务状态记录原值、已解析内容与候选。
     *
     * @param row 当前条目。
     * @param path 当前路径。
     * @param statusKey 状态字段名。
     * @param rawKey 原始键字段名。
     * @param fallbackKey 原始键不存在时的字段名。
     * @param resolvedKey 已解析名称字段名。
     * @param matchedKey 候选匹配字段名。
     * @param pending 待确认字段集合。
     */
    private static void status(Map<?, ?> row, String path, String statusKey, String rawKey, String fallbackKey,
        String resolvedKey, String matchedKey, Map<PendingReferenceKey, PendingReference> pending) {
        Object status = row.get(statusKey);
        if (!(status instanceof String code) || CharSequenceUtil.isBlank(code) || complete(code))
            return;
        String field = row.get(rawKey) instanceof String raw && !CharSequenceUtil.isBlank(raw) ? rawKey : fallbackKey;
        Object rawValue = row.get(field);
        if (!(rawValue instanceof String raw) || CharSequenceUtil.isBlank(raw))
            return;
        Object resolvedValue = resolvedKey == null ? null : row.get(resolvedKey);
        String resolved = resolvedValue instanceof String text && !text.equals(raw) ? clean(text) : null;
        Object matched = matchedKey == null ? null : row.get(matchedKey);
        Object alternatives = row.get(fallbackKey + "Candidates");
        List<String> candidates = alternatives instanceof List<?> values
            ? values.stream().filter(String.class::isInstance).map(String.class::cast).toList()
            : matched instanceof String text && CharSequenceUtil.isNotBlank(text) ? List.of(text) : List.of();
        add(pending, path + "." + fallbackKey, raw, code, resolved, candidates);
    }

    /** @param code 状态代码；@return 静态引用或补充来源已经完成处理。 */
    private static boolean complete(String code) {
        return WarframeWorldStateResolutionStatus.fromCode(code).isComplete()
            || WarframeBountySupplementStatus.fromCode(code).isComplete();
    }

    /**
     * 合并同一字段诊断，保留解析结果和可用候选。
     *
     * @param pending 待确认字段集合。
     * @param path 字段路径。
     * @param raw 原始键。
     * @param status 待确认状态。
     * @param resolved 当前解析结果。
     * @param candidates 候选名称。
     */
    private static void add(Map<PendingReferenceKey, PendingReference> pending, String path, String raw, String status,
        String resolved, List<String> candidates) {
        if (raw == null || CharSequenceUtil.isBlank(raw))
            return;
        WarframeWorldStateResolutionStatus statusType = WarframeWorldStateResolutionStatus.fromCode(status);
        PendingReference note = new PendingReference(path, raw, statusType, resolved, candidates);
        // 已有候选/部分匹配信息优先于简单的原始内部键提示。
        pending.merge(new PendingReferenceKey(path, raw), note, (existing, incoming) -> {
            boolean replaceStatus = existing.status().isUnresolved();
            return new PendingReference(existing.path(), existing.reference(),
                replaceStatus ? incoming.status() : existing.status(),
                existing.resolvedValue() != null ? existing.resolvedValue() : incoming.resolvedValue(),
                existing.candidates().isEmpty() ? incoming.candidates() : existing.candidates());
        });
    }

    /**
     * 从混合摘要中提取已解析的完整名称片段。
     *
     * @param text 当前展示摘要。
     * @param raw 未解析的原始键。
     * @return 已解析部分，只有原始键时为 null。
     */
    private static String resolvedPortion(String text, String raw) {
        if (raw == null || CharSequenceUtil.isBlank(raw) || !text.contains(raw))
            return null;
        String resolved = Arrays.stream(clean(text).split("[,，]")).map(CharSequenceUtil::trim)
            .filter(part -> CharSequenceUtil.isNotEmpty(part) && !part.contains(raw)
                && !INTERNAL_PATH.matcher(part).find() && !INTERNAL_CODE.matcher(part).find())
            .reduce((first, next) -> first + "、" + next).orElse(null);
        return resolved;
    }

    /**
     * 清除展示层的游戏文本标记。
     *
     * @param value 待展示文本。
     * @return 清理后的文本。
     */
    private static String clean(String value) {
        return WarframeWorldStateMarkupCleaner.clean(value);
    }

    /**
     * 按嵌套字段路径读取最终投影。
     *
     * @param data 最终投影。
     * @param path 带列表下标的字段路径。
     * @return 当前字段值，路径不存在时为 null。
     */
    private static Object atPath(Map<String, Object> data, String path) {
        Object current = data;
        Matcher matcher = PATH_PART.matcher(path);
        while (matcher.find()) {
            if (matcher.group(1) != null && current instanceof Map<?, ?> map)
                current = map.get(matcher.group(1));
            else if (matcher.group(2) != null && current instanceof List<?> list) {
                int index = Integer.parseInt(matcher.group(2));
                current = index < list.size() ? list.get(index) : null;
            } else
                return null;
        }
        return current;
    }

    /** 聚合同一字段的原值、解析状态与候选，供管理端和命令展示。 */
    private record PendingReference(String path, String reference, WarframeWorldStateResolutionStatus status,
        String resolvedValue, List<String> candidates) {
        private WarframeWorldStateResolutionVO toVO() {
            return new WarframeWorldStateResolutionVO(path, reference, status, resolvedValue, candidates);
        }
    }

    /** 字段路径和原值组成的结构化去重键，不依赖分隔符编码。 */
    private record PendingReferenceKey(String path, String reference) {
    }
}
