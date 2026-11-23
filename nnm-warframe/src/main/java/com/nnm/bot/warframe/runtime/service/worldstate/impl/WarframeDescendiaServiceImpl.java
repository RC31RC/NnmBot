package com.nnm.bot.warframe.runtime.service.worldstate.impl;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.common.validation.BusinessAssert;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.worldstate.DescendiaRewardGroupDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.worldstate.DescendiaRewardGroupDTO.RewardOptionDTO;
import com.nnm.bot.warframe.persistent.curated.service.worldstate.WarframeDescendiaRewardCatalogService;
import com.nnm.bot.warframe.runtime.constant.WarframeWorldStateErrorCode;
import com.nnm.bot.warframe.runtime.mapper.worldstate.WarframeWorldStateProjector;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.DescendiaRewardSelectionDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateCommandVO;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeDescendiaService;
import com.nnm.bot.warframe.runtime.service.worldstate.WarframeWorldStateService;
import jakarta.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;

import static com.nnm.bot.warframe.runtime.service.worldstate.impl.WarframeWorldStateProjectionFields.toStringKeys;

/** 单 JVM 内按官方周次保存人工确认，与刷新快照独立更新并共用同一周次身份。 */
@Service
public class WarframeDescendiaServiceImpl implements WarframeDescendiaService {
    /** 楼层到官方掉落池的静态资料。 */
    @Resource
    private WarframeDescendiaRewardCatalogService catalog;

    /** 保存前重新校验当前有效的周次。 */
    @Resource
    private WarframeWorldStateService worldState;

    private static final String NORMAL = "normal";
    private static final String STEEL_PATH = "steelPath";
    private final Map<Floor, String> selections = new LinkedHashMap<>();
    private String selectedWeek;

    @Override
    public WarframeWorldStateCommandVO project(WorldStateDTO state) {
        var projected = WarframeWorldStateProjector.descents(state);
        var weeks = rows(projected.data().get("items"));
        if (weeks.isEmpty())
            return projected;
        var week = toStringKeys(weeks.getFirst());
        String weekKey = key(week);
        Map<Floor, String> confirmed = confirmed(weekKey);
        Map<Floor, DescendiaRewardGroupDTO> groups = new LinkedHashMap<>();
        for (var group : catalog.findAll())
            groups.put(new Floor(group.floor(), group.difficulty()), group);
        week.put("weekKey", weekKey);
        week.put("rewardStatus", groups.isEmpty() ? "楼层奖池尚未入库" : "普通与钢铁分别确认，未勾选的候选不代表本周结果");
        groups.values().stream().map(DescendiaRewardGroupDTO::sourceUrl).filter(Objects::nonNull).findFirst()
            .ifPresent(source -> week.put("rewardSource", source));
        List<String> confirmedLines = new ArrayList<>();
        week.put("challenges", rows(week.get("challenges")).stream().map(challenge -> {
            var entry = toStringKeys(challenge);
            if (challenge.get("index") instanceof Number index) {
                attach(entry, "normalReward", new Floor(index.intValue(), NORMAL), groups, confirmed);
                attach(entry, "steelPathReward", new Floor(index.intValue(), STEEL_PATH), groups, confirmed);
                addConfirmed(confirmedLines, index, "普通", entry.get("normalReward"));
                addConfirmed(confirmedLines, index, "钢铁", entry.get("steelPathReward"));
            }
            return entry;
        }).toList());
        var output = toStringKeys(projected.data());
        output.put("items", List.of(week));
        String text =
            confirmedLines.isEmpty() ? projected.text() : projected.text() + "\n" + String.join("\n", confirmedLines);
        return new WarframeWorldStateCommandVO(text, output);
    }

    private static void addConfirmed(List<String> lines, Number floor, String difficulty, Object reward) {
        if (!(reward instanceof Map<?, ?> group))
            return;
        rows(group.get("options")).stream().filter(option -> Boolean.TRUE.equals(option.get("confirmed"))).findFirst()
            .ifPresent(option -> lines.add("第 %s 层 · %s · %s（已确认）".formatted(floor, difficulty, option.get("name"))));
    }

    @Override
    public void confirm(DescendiaRewardSelectionDTO selection) {
        BusinessAssert.isTrue(
            selection != null && selection.floor() != null
                && (NORMAL.equals(selection.difficulty()) || STEEL_PATH.equals(selection.difficulty())),
            WarframeWorldStateErrorCode.DESCENDIA_REWARD_INVALID);
        var current = activeWeek(worldState.getCurrentState());
        String week = current == null ? null : key(current);
        BusinessAssert.isTrue(week != null && week.equals(selection.week()),
            WarframeWorldStateErrorCode.DESCENDIA_WEEK_CHANGED);
        var group = catalog.findAll().stream()
            .filter(item -> item.floor() == selection.floor() && item.difficulty().equals(selection.difficulty()))
            .findFirst().orElse(null);
        BusinessAssert.isTrue(
            rows(current.get("challenges")).stream()
                .anyMatch(item -> item.get("index") instanceof Number index && index.intValue() == selection.floor())
                && group != null && !group.guaranteed()
                && (selection.optionKey() == null
                    || group.options().stream().anyMatch(option -> optionKey(option).equals(selection.optionKey()))),
            WarframeWorldStateErrorCode.DESCENDIA_REWARD_INVALID);
        save(week, new Floor(selection.floor(), selection.difficulty()), selection.optionKey());
    }

    private synchronized void save(String week, Floor floor, String option) {
        var current = activeWeek(worldState.getCurrentState());
        BusinessAssert.isTrue(current != null && week.equals(key(current)),
            WarframeWorldStateErrorCode.DESCENDIA_WEEK_CHANGED);
        if (!week.equals(selectedWeek)) {
            selections.clear();
            selectedWeek = week;
        }
        if (option == null)
            selections.remove(floor);
        else
            selections.put(floor, option);
    }

    private synchronized Map<Floor, String> confirmed(String week) {
        return week != null && week.equals(selectedWeek) ? Map.copyOf(selections) : Map.of();
    }

    private static Map<?, ?> activeWeek(WorldStateDTO state) {
        if (state == null)
            return null;
        var weeks = rows(WarframeWorldStateProjector.descents(state).data().get("items"));
        return weeks.isEmpty() ? null : weeks.getFirst();
    }

    private static String key(Map<?, ?> week) {
        return week.get("expiryInstant") instanceof String expiry && StrUtil.isNotBlank(expiry)
            && week.get("seed") instanceof Number seed ? expiry + ":" + seed : null;
    }

    private static String optionKey(RewardOptionDTO option) {
        String identity = Objects.toString(option.itemKey(), option.name()) + ":" + option.count();
        return UUID.nameUUIDFromBytes(identity.getBytes(StandardCharsets.UTF_8)).toString();
    }

    private static void attach(Map<String, Object> challenge, String field, Floor floor,
        Map<Floor, DescendiaRewardGroupDTO> groups, Map<Floor, String> confirmed) {
        var group = groups.get(floor);
        if (group == null)
            return;
        var reward = new LinkedHashMap<String, Object>();
        reward.put("label", group.label());
        reward.put("guaranteed", group.guaranteed());
        reward.put("note", group.note());
        reward.put("options", group.options().stream().map(option -> {
            String identity = optionKey(option);
            var data = new LinkedHashMap<String, Object>();
            data.put("optionKey", identity);
            data.put("itemKey", option.itemKey());
            data.put("name", option.name());
            data.put("displayName", option.name());
            data.put("imageUrl", option.imageUrl());
            data.put("count", option.count());
            data.put("chancePercent", option.chancePercent());
            data.put("confirmed", !group.guaranteed() && identity.equals(confirmed.get(floor)));
            return data;
        }).toList());
        challenge.put(field, reward);
    }

    private static List<Map<?, ?>> rows(Object data) {
        if (!(data instanceof List<?> values))
            return List.of();
        return values.stream().filter(Map.class::isInstance).<Map<?, ?>>map(value -> (Map<?, ?>)value).toList();
    }

    /** 单周内的奖励选择由楼层和难度唯一标识。 */
    private record Floor(int number, String difficulty) {
    }
}
