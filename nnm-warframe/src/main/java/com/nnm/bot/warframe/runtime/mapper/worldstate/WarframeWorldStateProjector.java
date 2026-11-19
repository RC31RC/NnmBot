package com.nnm.bot.warframe.runtime.mapper.worldstate;

import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO.ActiveMissionDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO.AlertDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO.ArbitrationDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO.ArchimedeaDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO.ConstructionProgressDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO.SentientOutpostDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO.SimarisDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO.SortieDTO;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO;
import com.nnm.bot.warframe.runtime.constant.WarframeBountyCycleConstants.Syndicate;
import com.nnm.bot.warframe.runtime.constant.WarframeConquestType;
import com.nnm.bot.warframe.runtime.constant.WarframeWorldStateDisplayConstants.CalendarEvent;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateCommandVO;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * WorldStateDTO 内部业务投影，供引用解析、管理端和订阅匹配使用。 命令展示字段由 WarframeWorldStateDisplayMapper 在引用解析之后选择。
 */
public final class WarframeWorldStateProjector {

    private static final long SECONDS_PER_MINUTE = 60;
    private static final long SECONDS_PER_HOUR = 60 * SECONDS_PER_MINUTE;
    private static final long SECONDS_PER_DAY = 24 * SECONDS_PER_HOUR;
    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("MM-dd HH:mm").withZone(ZONE_ID);

    /**
     * 禁止实例化，仅通过本类的静态方法使用此能力。
     */
    private WarframeWorldStateProjector() {}

    /**
     * 构造警报的命令展示字段。
     *
     * @param state 已经解析的当前世界状态。
     * @return 当前命令的标题、状态及展示条目。
     */
    public static WarframeWorldStateCommandVO alerts(WorldStateDTO state) {
        List<Map<String, Object>> items = new ArrayList<>();
        for (AlertDTO alert : state.alerts()) {
            // 官方 WorldStateDTO 可能短时间保留已结束警报，当前命令只投影仍有效的任务。
            if (alert.expiry() != null && !alert.expiry().isAfter(Instant.now())) {
                continue;
            }
            Map<String,
                Object> item = item("id", alert.id(), "mission", alert.mission(), "reward", alert.reward(), "node",
                    alert.node(), "faction", alert.faction(), "minEnemyLevel", alert.minEnemyLevel(), "maxEnemyLevel",
                    alert.maxEnemyLevel(), "activation", format(alert.activation()), "activationInstant",
                    alert.activation() == null ? "" : alert.activation().toString(), "expiry", format(alert.expiry()),
                    "expiryInstant", alert.expiry() == null ? "" : alert.expiry().toString(), "remaining",
                    remaining(alert.expiry()));
            item.put("rewardItems", alert.rewardItems().stream()
                .map(reward -> item("item", reward.item(), "count", reward.count())).toList());
            if (alert.rewardCredits() != null)
                item.put("rewardCredits", alert.rewardCredits());
            items.add(item);
        }
        return output(state, "警报", "当前警报：" + items.size() + " 条", items, "items", "alertCount");
    }

    /**
     * 构造突击的命令展示字段。
     *
     * @param state 已经解析的当前世界状态。
     * @param archon 执刑官。
     * @return 当前命令的标题、状态及展示条目。
     */
    public static WarframeWorldStateCommandVO sorties(WorldStateDTO state, boolean archon) {
        List<SortieDTO> source = archon ? state.archonHunts() : state.allSorties();
        List<Map<String, Object>> items = source.stream().map(WarframeWorldStateProjector::sortie).toList();
        String title = archon ? "执刑官猎杀" : "突击";
        return output(state, title, title + "：" + items.size() + " 条", items, "items", "itemCount");
    }

    /**
     * 构造虚空商人的命令展示字段。
     *
     * @param state 已经解析的当前世界状态。
     * @return 当前命令的标题、状态及展示条目。
     */
    public static WarframeWorldStateCommandVO voidTrader(WorldStateDTO state) {
        List<Map<String, Object>> items = state.allVoidTraders().stream().map(trader -> {
            Map<String,
                Object> item = item("location", trader.location(), "activation", format(trader.activation()),
                    "activationInstant", trader.activation() == null ? "" : trader.activation().toString(), "expiry",
                    format(trader.expiry()), "expiryInstant", trader.expiry() == null ? "" : trader.expiry().toString(),
                    "remaining", remaining(trader.expiry()), "active", trader.active());
            item.put("inventory",
                trader.inventory().stream().map(
                    product -> item("item", product.item(), "ducats", product.ducats(), "credits", product.credits()))
                    .toList());
            return item;
        }).toList();
        return output(state, "奸商", "奸商数据：" + items.size() + " 条", items, "items", "itemCount");
    }

    /**
     * 构造仲裁的命令展示字段。
     *
     * @param state 已经解析的当前世界状态。
     * @param table 表。
     * @return 当前命令的标题、状态及展示条目。
     */
    public static WarframeWorldStateCommandVO arbitration(WorldStateDTO state, boolean table) {
        ArbitrationDTO arbitration = state.arbitration();
        if (ObjectUtil.isNull(arbitration)) {
            return new WarframeWorldStateCommandVO(table ? "仲裁轮换表暂时没有可用数据" : "当前没有进行中的仲裁",
                base(state, table ? "仲裁表" : "仲裁"));
        }
        Map<String, Object> current =
            item("node", arbitration.node(), "type", arbitration.type(), "expiry", format(arbitration.expiry()),
                "expiryInstant", arbitration.expiry() == null ? "" : arbitration.expiry().toString(), "remaining",
                remaining(arbitration.expiry()));
        Map<String, Object> data = base(state, table ? "仲裁表" : "仲裁");
        data.put("current", current);
        data.put("items", List.of(current));
        data.put("itemCount", 1);
        String text = "仲裁：" + value(arbitration.node(), "未知节点") + "（" + value(arbitration.type(), "未知任务") + "）" + "，剩余 "
            + value(remaining(arbitration.expiry()), "未知");
        return new WarframeWorldStateCommandVO(text, data);
    }

    /**
     * 构造每日特惠的命令展示字段。
     *
     * @param state 已经解析的当前世界状态。
     * @return 当前命令的标题、状态及展示条目。
     */
    public static WarframeWorldStateCommandVO dailyDeals(WorldStateDTO state) {
        List<Map<String, Object>> items = state.dailyDeals().stream()
            .map(deal -> item("item", deal.item(), "stock", deal.stock(), "sold", deal.sold(), "credits",
                deal.credits(), "platinum", deal.platinum(), "activation", format(deal.activation()),
                "activationInstant", deal.activation() == null ? "" : deal.activation().toString(), "expiry",
                format(deal.expiry()), "expiryInstant", deal.expiry() == null ? "" : deal.expiry().toString(),
                "remaining", remaining(deal.expiry())))
            .toList();
        return output(state, "每日特惠", "每日特惠：" + items.size() + " 条", items, "items", "itemCount");
    }

    /**
     * 构造入侵的命令展示字段。
     *
     * @param state 已经解析的当前世界状态。
     * @return 当前命令的标题、状态及展示条目。
     */
    public static WarframeWorldStateCommandVO invasions(WorldStateDTO state) {
        List<
            Map<String, Object>> items =
                state.invasions().stream().filter(invasion -> !invasion.completed())
                    .map(invasion -> item("node", invasion.node(), "completion", invasion.completion(), "completed",
                        invasion.completed(), "attackerFaction", invasion.attackerFaction(), "defenderFaction",
                        invasion.defenderFaction(), "attackerMissions", invasion.attackerMissions(), "defenderMissions",
                        invasion.defenderMissions(), "attackerReward", invasion.attackerReward(), "defenderReward",
                        invasion.defenderReward(), "activation", format(invasion.activation()), "activationInstant",
                        invasion.activation() == null ? ""
                            : invasion.activation().toString(),
                        "attackerRewards",
                        invasion.attackerRewards().stream()
                            .map(reward -> item("item", reward.item(), "count", reward.count())).toList(),
                        "defenderRewards",
                        (invasion.defenderRewards().isEmpty() && !invasion.attackerRewards().isEmpty()
                            && invasion.attackerReward() != null
                            && invasion.attackerReward().equals(invasion.defenderReward()) ? invasion.attackerRewards()
                                : invasion.defenderRewards())
                            .stream().map(reward -> item("item", reward.item(), "count", reward.count())).toList()))
                    .toList();
        return output(state, "入侵", "当前入侵：" + items.size() + " 条", items, "items", "itemCount");
    }

    /**
     * 构造裂隙的命令展示字段。
     *
     * @param state 已经解析的当前世界状态。
     * @param filter 筛选。
     * @param title 标题。
     * @return 当前命令的标题、状态及展示条目。
     */
    public static WarframeWorldStateCommandVO fissures(WorldStateDTO state, Predicate<ActiveMissionDTO> filter,
        String title) {
        List<Map<String, Object>> items = state.allActiveMissions().stream().filter(filter)
            .map(mission -> item("id", mission.id(), "node", mission.node(), "missionType", mission.missionType(),
                "tier", mission.tier(), "hard", mission.hard(), "voidStorm", mission.voidStorm(), "activation",
                format(mission.activation()), "activationInstant",
                mission.activation() == null ? "" : mission.activation().toString(), "expiry", format(mission.expiry()),
                "expiryInstant", mission.expiry() == null ? "" : mission.expiry().toString(), "remaining",
                remaining(mission.expiry())))
            .toList();
        return output(state, title, title + "：" + items.size() + " 条", items, "items", "itemCount");
    }

    /**
     * 构造钢铁路径的命令展示字段。
     *
     * @param state 已经解析的当前世界状态。
     * @return 当前命令的标题、状态及展示条目。
     */
    public static WarframeWorldStateCommandVO steelPath(WorldStateDTO state) {
        List<Map<String, Object>> items = state.steelPathOfferings().stream().map(offering -> item("node",
            offering.node(), "reward", offering.reward(), "activation", format(offering.activation()),
            "activationInstant", offering.activation() == null ? "" : offering.activation().toString(), "expiry",
            format(offering.expiry()), "expiryInstant", offering.expiry() == null ? "" : offering.expiry().toString(),
            "remaining", remaining(offering.expiry()))).toList();
        return output(state, "钢铁奖励", "钢铁路径奖励：" + items.size() + " 条", items, "items", "itemCount");
    }

    /**
     * 构造周期的命令展示字段。
     *
     * @param state 已经解析的当前世界状态。
     * @return 当前命令的标题、状态及展示条目。
     */
    public static WarframeWorldStateCommandVO cycles(WorldStateDTO state) {
        return cycles(state, "平原");
    }

    /**
     * 构造周期的命令展示字段。
     *
     * @param state 已经解析的当前世界状态。
     * @param title 标题。
     * @return 当前命令的标题、状态及展示条目。
     */
    public static WarframeWorldStateCommandVO cycles(WorldStateDTO state, String title) {
        Map<String, Object> cycles = new LinkedHashMap<>();
        if (ObjectUtil.isNotNull(state.earthCycle())) {
            cycles.put("地球", cycle(state.earthCycle().isDay() ? "白天" : "夜晚", state.earthCycle().expiry()));
        }
        if (ObjectUtil.isNotNull(state.cetusCycle())) {
            cycles.put("夜灵平原", cycle(state.cetusCycle().isDay() ? "白天" : "夜晚", state.cetusCycle().expiry()));
        }
        if (ObjectUtil.isNotNull(state.vallisCycle())) {
            cycles.put("福尔图娜", cycle(state.vallisCycle().isWarm() ? "温暖" : "寒冷", state.vallisCycle().expiry()));
        }
        if (ObjectUtil.isNotNull(state.cambionCycle())) {
            cycles.put("魔胎之境", cycle(state.cambionCycle().active(), state.cambionCycle().expiry()));
        }
        if (ObjectUtil.isNotNull(state.zarimanCycle())) {
            cycles.put("扎里曼", cycle(state.zarimanCycle().state(), state.zarimanCycle().expiry()));
        }
        if (ObjectUtil.isNotNull(state.duviriCycle())) {
            cycles.put("双衍王境", cycle(state.duviriCycle().state(), state.duviriCycle().expiry()));
        }
        if ("双衍王境".equals(title)) {
            cycles.keySet().removeIf(name -> !"双衍王境".equals(name));
        }
        Map<String, Object> data = base(state, title);
        data.put("cycles", cycles);
        data.put("items",
            cycles.entrySet().stream().map(entry -> item("name", entry.getKey(), "value", entry.getValue())).toList());
        data.put("itemCount", cycles.size());
        return new WarframeWorldStateCommandVO(title + "：" + cycles.size() + " 个", data);
    }

    /**
     * 构造集团的命令展示字段。
     *
     * @param state 已经解析的当前世界状态。
     * @param title 标题。
     * @param matcher 匹配器。
     * @return 当前命令的标题、状态及展示条目。
     */
    public static WarframeWorldStateCommandVO syndicate(WorldStateDTO state, String title, Predicate<String> matcher) {
        List<
            Map<String, Object>> items =
                state.syndicateMissions().stream().filter(mission -> matcher.test(value(mission.syndicate(), "")))
                    .map(mission -> item("syndicate", mission.syndicate(), "node", mission.node(), "nodes",
                        mission.nodes(), "activation", format(mission.activation()), "activationInstant",
                        mission.activation() == null ? "" : mission.activation().toString(), "expiry",
                        format(mission.expiry()), "expiryInstant",
                        mission.expiry() == null ? "" : mission.expiry().toString(), "remaining",
                        remaining(mission.expiry()), "supplementStatus", mission.supplementStatus(), "supplementalJobs",
                        mission.supplementalJobs().stream().map(job -> item(
                            "node", job.node(), "nodeName", job.nodeName(), "systemName", job.systemName(),
                            "missionType", job.missionType(), "challenge", job.challenge(), "challengeName",
                            job.challengeName(), "challengeDescription", job.challengeDescription(), "ally", job.ally(),
                            "allyName", job.allyName(), "resolutionStatus", job.resolutionStatus())).toList(),
                        "staticRewardPools",
                        mission.staticRewardPools().stream().map(
                            pool -> item("poolId", pool.id(), "name", pool.name(), "rewards", pool.rewards())).toList(),
                        "jobs",
                        mission.jobs().stream()
                            .map(job -> item("jobType", value(job.jobType(), job.type()), "type",
                                value(job.type(), job.isVault() ? "隔离库" : job.jobType()), "minEnemyLevel",
                                job.minEnemyLevel(), "maxEnemyLevel", job.maxEnemyLevel(), "standingStages",
                                job.standingStages(), "minMR", job.minMR(), "isVault", job.isVault(), "locationTag",
                                job.locationTag(), "expiry", format(job.expiry()), "expiryInstant",
                                job.expiry() == null ? "" : job.expiry().toString(), "remaining",
                                remaining(job.expiry()), "timeBound", job.timeBound(), "rewardPoolReference",
                                job.rewardPoolReference(), "rewards",
                                job.rewardPool().stream().map(reward -> item("item", reward.item(), "count",
                                    reward.count(), "chance", reward.chance(), "rarity", reward.rarity())).toList()))
                            .toList()))
                    .toList();
        return output(state, title, title + "：" + items.size() + " 条", items, "items", "itemCount");
    }

    /**
     * 区分官方任务和经毫秒轮次核对的 oracle 补充任务；始终带解析状态，不合并历史排期。
     *
     * @param state 已解析的当前世界状态。
     * @param syndicate 当前分区所属集团，按官方代码精确匹配。
     * @return 当前地区的补充赏金任务与静态候选池。
     */
    public static WarframeWorldStateCommandVO bountyCycle(WorldStateDTO state, Syndicate syndicate) {
        List<Map<String, Object>> items = state.syndicateMissions().stream()
            .filter(mission -> syndicate.matches(mission.syndicate()))
            .map(mission -> item("syndicate", mission.syndicate(), "supplementStatus", mission.supplementStatus(),
                "expiry", format(mission.expiry()), "expiryInstant",
                mission.expiry() == null ? "" : mission.expiry().toString(), "supplementalJobs",
                mission.supplementalJobs().stream()
                    .map(job -> item("tier", job.tier(), "node", job.node(), "nodeName", job.nodeName(), "systemName",
                        job.systemName(), "missionType", job.missionType(), "challenge", job.challenge(),
                        "challengeName", job.challengeName(), "challengeDescription", job.challengeDescription(),
                        "ally", job.ally(), "allyName", job.allyName(), "resolutionStatus", job.resolutionStatus()))
                    .toList(),
                "staticRewardPools",
                mission.staticRewardPools().stream()
                    .map(pool -> item("poolId", pool.id(), "name", pool.name(), "rewards", pool.rewards())).toList()))
            .toList();
        return output(state, syndicate.title(), syndicate.title() + "：" + items.size() + " 组", items, "items",
            "itemCount");
    }

    /**
     * 构造夜波的命令展示字段。
     *
     * @param state 已经解析的当前世界状态。
     * @return 当前命令的标题、状态及展示条目。
     */
    public static WarframeWorldStateCommandVO nightwave(WorldStateDTO state) {
        List<Map<String, Object>> items = state.nightwave().stream()
            .map(mission -> item("id", mission.id(), "challenge", mission.challenge(), "title", mission.title(),
                "description", mission.desc(), "reputation", mission.reputation(), "daily", mission.isDaily(),
                "activation", format(mission.activation()), "expiry", format(mission.expiry()), "expiryInstant",
                mission.expiry() == null ? "" : mission.expiry().toString(), "remaining", remaining(mission.expiry())))
            .toList();
        return output(state, "电波", "电波任务：" + items.size() + " 条", items, "items", "itemCount");
    }

    /**
     * 构造科研的命令展示字段。
     *
     * @param state 已经解析的当前世界状态。
     * @param title 标题。
     * @param filter 筛选。
     * @return 当前命令的标题、状态及展示条目。
     */
    public static WarframeWorldStateCommandVO archimedeas(WorldStateDTO state, String title,
        Predicate<ArchimedeaDTO> filter) {
        List<Map<String, Object>> items = state.archimedeas().stream().filter(filter)
            .map(conquest -> item("id", conquest.id(), "type", WarframeConquestType.displayName(conquest.typeKey()),
                "typeKey", conquest.typeKey(), "activation", format(conquest.activation()), "activationInstant",
                conquest.activation() == null ? "" : conquest.activation().toString(), "expiry",
                format(conquest.expiry()), "expiryInstant",
                conquest.expiry() == null ? "" : conquest.expiry().toString(), "remaining",
                remaining(conquest.expiry()), "personalModifiers", conquest.personalModifiers(), "missions",
                conquest.missions().stream()
                    .map(mission -> item("faction", mission.faction(), "factionKey", mission.factionKey(),
                        "missionType", mission.missionType(), "missionTypeKey", mission.missionTypeKey(), "deviation",
                        mission.deviation(), "risks", mission.risks()))
                    .toList()))
            .toList();
        return output(state, title, title + "：" + items.size() + " 条", items, "items", "itemCount");
    }

    /**
     * 构造恶堕的命令展示字段。
     *
     * @param state 已经解析的当前世界状态。
     * @return 当前命令的标题、状态及展示条目。
     */
    public static WarframeWorldStateCommandVO descents(WorldStateDTO state) {
        Instant now = Instant.now();
        List<Map<String, Object>> items = state.descents().stream()
            .filter(descent -> (descent.activation() == null || !descent.activation().isAfter(now))
                && (descent.expiry() == null || descent.expiry().isAfter(now)))
            .max(Comparator
                .comparing(WorldStateDTO.DescentDTO::activation, Comparator.nullsFirst(Comparator.naturalOrder())))
            .stream()
            .map(descent -> item("seed", descent.seed(), "activation", format(descent.activation()), "expiry",
                format(descent.expiry()), "expiryInstant", descent.expiry() == null ? "" : descent.expiry().toString(),
                "remaining", remaining(descent.expiry()), "rewardStatus", "官方周轮换未提供奖励池；暂无法核实本周奖励", "challenges",
                descent.challenges().stream()
                    .map(challenge -> item("index", challenge.index(), "type", challenge.type(), "challenge",
                        challenge.challenge(), "level", challenge.level(), "specs", challenge.specs(), "auras",
                        challenge.auras()))
                    .toList()))
            .toList();
        return output(state, "深层下降", "深层下降：" + items.size() + " 个轮换", items, "items", "itemCount");
    }

    /** @param state 当前官方快照；@return Tmp.pgr 消息和按商品分开的 QTCC 活动目标。 */
    public static WarframeWorldStateCommandVO temporaryActivities(WorldStateDTO state) {
        List<Map<String, Object>> items = new ArrayList<>();
        var temporary = state.temporaryActivity();
        if (temporary != null) {
            if (temporary.pager() != null) {
                var pager = temporary.pager();
                items.add(item("title", "寻呼机消息", "description", pager.message(), "language", pager.language(),
                    "publishedAt", pager.timestamp() == null ? null : pager.timestamp().toString()));
            }
            for (var counter : temporary.counters()) {
                items.add(item("title", "QTCC 社区活动", "item", counter.item(), "current", counter.current(), "goalCount",
                    counter.target()));
            }
        }
        return output(state, "社区活动与寻呼机", "社区活动与寻呼机：" + items.size() + " 条", items, "items", "itemCount");
    }

    /** @param state 官方快照及外部结构化任务流增量；@return 本小时正在进行的赤毒任务。 */
    public static WarframeWorldStateCommandVO kuvaMissions(WorldStateDTO state) {
        List<Map<String, Object>> items = state.kuvaMissions().stream()
            .map(mission -> item("node", mission.node(), "missionType", mission.missionType(), "activationInstant",
                mission.activation().toString(), "expiryInstant", mission.expiry().toString(), "remaining",
                remaining(mission.expiry())))
            .toList();
        return output(state, "赤毒任务", "赤毒任务：" + items.size() + " 条", items, "items", "itemCount");
    }

    /**
     * 构造全局升级的命令展示字段。
     *
     * @param state 已经解析的当前世界状态。
     * @return 当前命令的标题、状态及展示条目。
     */
    public static WarframeWorldStateCommandVO globalUpgrades(WorldStateDTO state) {
        List<Map<String, Object>> items = state.globalUpgrades().stream()
            .map(upgrade -> item("id", upgrade.id(), "upgradeType", upgrade.upgradeType(), "operationType",
                upgrade.operationType(), "value", upgrade.value(), "activation", format(upgrade.activation()),
                "activationInstant", upgrade.activation() == null ? "" : upgrade.activation().toString(), "expiry",
                format(upgrade.expiry()), "expiryInstant", upgrade.expiry() == null ? "" : upgrade.expiry().toString(),
                "remaining", remaining(upgrade.expiry())))
            .toList();
        return output(state, "全局增益", "全局增益：" + items.size() + " 条", items, "items", "itemCount");
    }

    /**
     * 构造限时促销的命令展示字段。
     *
     * @param state 已经解析的当前世界状态。
     * @return 当前命令的标题、状态及展示条目。
     */
    public static WarframeWorldStateCommandVO flashSales(WorldStateDTO state) {
        List<Map<String, Object>> items = state.flashSales().stream()
            .map(sale -> item("id", sale.id(), "item", sale.uniqueName(), "discount", sale.discount(),
                "regularOverride", sale.regularOverride(), "premiumOverride", sale.premiumOverride(), "shownInMarket",
                sale.shownInMarket(), "featured", sale.featured(), "popular", sale.popular(), "activation",
                format(sale.activation()), "activationInstant",
                sale.activation() == null ? "" : sale.activation().toString(), "expiry", format(sale.expiry()),
                "expiryInstant", sale.expiry() == null ? "" : sale.expiry().toString(), "remaining",
                remaining(sale.expiry())))
            .toList();
        return output(state, "限时促销", "限时促销：" + items.size() + " 条", items, "items", "itemCount");
    }

    /**
     * 构造常驻敌人的命令展示字段。
     *
     * @param state 已经解析的当前世界状态。
     * @return 当前命令的标题、状态及展示条目。
     */
    public static WarframeWorldStateCommandVO persistentEnemies(WorldStateDTO state) {
        List<Map<String, Object>> items = state.persistentEnemies().stream()
            .map(enemy -> item("id", enemy.id(), "agentType", enemy.agentType(), "locationTag", enemy.locationTag(),
                "rank", enemy.rank(), "healthPercent", enemy.healthPercent(), "fleeDamage", enemy.fleeDamage(),
                "region", enemy.region(), "lastDiscoveredTime", format(enemy.lastDiscoveredTime()),
                "lastDiscoveredLocation", enemy.lastDiscoveredLocation(), "discovered", enemy.discovered(),
                "usingTicketing", enemy.usingTicketing()))
            .toList();
        return output(state, "小小黑", "小小黑：" + items.size() + " 条", items, "items", "itemCount");
    }

    /**
     * 构造事件的命令展示字段。
     *
     * @param state 已经解析的当前世界状态。
     * @return 当前命令的标题、状态及展示条目。
     */
    public static WarframeWorldStateCommandVO events(WorldStateDTO state) {
        List<Map<String, Object>> items =
            state.events().stream()
                .map(event -> item("id", event.id(), "title", event.title(), "description", event.description(),
                    "category", event.category(), "node", event.node(), "progress", event.progress(), "goal",
                    event.goal(), "activation", format(event.activation()), "activationInstant",
                    event.activation() == null ? "" : event.activation().toString(), "expiry", format(event.expiry()),
                    "expiryInstant", event.expiry() == null ? "" : event.expiry().toString(), "remaining",
                    remaining(event.expiry()), "rewards", event.rewards().stream()
                        .map(reward -> item("item", reward.item(), "count", reward.count())).toList(),
                    "rewardCredits", event.rewardCredits()))
                .toList();
        return output(state, "活动目标", "活动目标：" + items.size() + " 条", items, "items", "itemCount");
    }

    /**
     * 展示当前商城分类和商品，不把分类推荐解释为折扣。
     *
     * @param state 已解析的当前世界状态。
     * @return 有序分类和待静态查询补全的商品。
     */
    public static WarframeWorldStateCommandVO market(WorldStateDTO state) {
        List<Map<String, Object>> items = state.marketCategories().stream()
            .map(category -> item("title", category.title(), "categoryCode", category.categoryCode(), "categoryIcon",
                category.categoryIcon(), "addToMenu", category.addToMenu(), "inventory",
                category.inventory().stream().map(product -> item("item", product.item())).toList()))
            .toList();
        return output(state, "商城分类", "商城分类：" + items.size() + " 类", items, "items", "itemCount");
    }

    /**
     * 构造新闻的命令展示字段。
     *
     * @param state 已经解析的当前世界状态。
     * @return 当前命令的标题、状态及展示条目。
     */
    public static WarframeWorldStateCommandVO news(WorldStateDTO state) {
        List<Map<String, Object>> items = state.news().stream()
            .map(news -> item("id", news.id(), "title", news.title(), "link", news.link(), "imageUrl", news.imageUrl(),
                "publishedAt", format(news.publishedAt()), "expiry", format(news.expiry()), "expiryInstant",
                news.expiry() == null ? "" : news.expiry().toString(), "priority", news.priority()))
            .toList();
        return output(state, "新闻", "新闻：" + items.size() + " 条", items, "items", "itemCount");
    }

    /**
     * 构造建设进度的命令展示字段。
     *
     * @param state 已经解析的当前世界状态。
     * @return 当前命令的标题、状态及展示条目。
     */
    public static WarframeWorldStateCommandVO constructionProgress(WorldStateDTO state) {
        ConstructionProgressDTO progress = state.constructionProgress();
        List<Map<String, Object>> items = progress == null ? List.of()
            : List.of(item("name", "巨人战舰", "progress", progress.fomorian()),
                item("name", "利刃豺狼", "progress", progress.razorback()),
                item("name", "未标明项目", "progress", progress.unknown()));
        return output(state, "舰队建造", "舰队建造进度", items, "items", "itemCount");
    }

    /**
     * 构造Simaris的命令展示字段。
     *
     * @param state 已经解析的当前世界状态。
     * @return 当前命令的标题、状态及展示条目。
     */
    public static WarframeWorldStateCommandVO simaris(WorldStateDTO state) {
        SimarisDTO target = state.simaris();
        return output(state, "西玛里斯", "西玛里斯公共研究目标",
            target == null ? List.of() : List.of(item("title", target.target(), "active", target.active())), "items",
            "itemCount");
    }

    /**
     * 构造Sentient前哨站的命令展示字段。
     *
     * @param state 已经解析的当前世界状态。
     * @return 当前命令的标题、状态及展示条目。
     */
    public static WarframeWorldStateCommandVO sentientOutposts(WorldStateDTO state) {
        SentientOutpostDTO outpost = state.sentientOutpost();
        return output(state, "Sentient 前哨站", "Sentient 前哨站当前节点", outpost == null ? List.of()
            : List.of(item("node", outpost.node(), "activation", format(outpost.activation()), "activationInstant",
                outpost.activation() == null ? "" : outpost.activation().toString(), "expiry", format(outpost.expiry()),
                "expiryInstant", outpost.expiry() == null ? "" : outpost.expiry().toString(), "remaining",
                remaining(outpost.expiry()))),
            "items", "itemCount");
    }

    /**
     * 构造氏族倡议的命令展示字段。
     *
     * @param state 已经解析的当前世界状态。
     * @return 当前命令的标题、状态及展示条目。
     */
    public static WarframeWorldStateCommandVO clanInitiatives(WorldStateDTO state) {
        // 官方列表包含本周和下周；按周序号选择较早一条，不把下周候选当本周奖励。
        return output(state, "氏族周任务", "氏族周任务",
            state.clanInitiatives().stream()
                .sorted(Comparator
                    .comparing(WorldStateDTO.ClanInitiativeDTO::week, Comparator.nullsLast(Comparator.naturalOrder())))
                .limit(1)
                .map(
                    clan -> item("week", clan.week(), "location", clan.region(), "rewards",
                        clan.rewards().stream()
                            .map(reward -> item("item", reward.item(), "count", reward.count(), "points",
                                reward.points(), "claimed", reward.claimed()))
                            .toList()))
                .toList(),
            "items", "itemCount");
    }

    /**
     * 构造回环的命令展示字段。
     *
     * @param state 已经解析的当前世界状态。
     * @param hard 钢铁。
     * @return 当前命令的标题、状态及展示条目。
     */
    public static WarframeWorldStateCommandVO circuit(WorldStateDTO state, boolean hard) {
        return output(state, hard ? "灵化轮换" : "普通回廊", "回廊当前奖励",
            state.circuitChoices().stream()
                .filter(choice -> (hard ? "EXC_HARD" : "EXC_NORMAL").equalsIgnoreCase(choice.category())
                    || (hard ? "hard" : "normal").equalsIgnoreCase(choice.category()))
                .filter(choice -> choice.expiry() == null || choice.expiry().isAfter(Instant.now()))
                .filter(choice -> choice.activation() == null || !choice.activation().isAfter(Instant.now()))
                .flatMap(choice -> choice.choices().stream()
                    .map(name -> item("item", name.replaceAll("([a-z0-9])([A-Z])", "$1 $2").replace(" And ", " & "),
                        "expiry", format(choice.expiry()), "expiryInstant",
                        choice.expiry() == null ? "" : choice.expiry().toString(), "remaining",
                        remaining(choice.expiry()))))
                .toList(),
            "items", "itemCount");
    }

    /**
     * 构造日历的命令展示字段。
     *
     * @param state 已经解析的当前世界状态。
     * @return 当前命令的标题、状态及展示条目。
     */
    public static WarframeWorldStateCommandVO calendar(WorldStateDTO state) {
        List<Map<String, Object>> days = new ArrayList<>();
        for (var season : state.calendarSeasons()) {
            for (var day : season.days()) {
                List<Map<String, Object>> challenges = new ArrayList<>();
                List<Map<String, Object>> rewards = new ArrayList<>();
                for (var event : day.events()) {
                    if (CalendarEvent.CHALLENGE.matches(event.type())) {
                        challenges.add(item("title", event.title(), "challenge", event.title(), "description",
                            event.description()));
                    } else if (CalendarEvent.REWARD.matches(event.type())) {
                        rewards.add(item("item", event.reward(), "description", event.description()));
                    }
                }
                if (challenges.isEmpty() && rewards.isEmpty())
                    continue;
                Map<String, Object> entry = item("date", day.date(), "season", season.season());
                entry.put("challenges", challenges);
                entry.put("rewards", rewards);
                days.add(entry);
            }
        }
        return output(state, "实时日历", "1999 实时日历：" + days.size() + " 个有任务或奖励的日期", days, "items", "itemCount");
    }

    /**
     * 构造宝库商人的命令展示字段。
     *
     * @param state 已经解析的当前世界状态。
     * @return 当前命令的标题、状态及展示条目。
     */
    public static WarframeWorldStateCommandVO vaultTrader(WorldStateDTO state) {
        return output(state, "宝库商人", "宝库商人",
            state.vaultTraders().stream()
                .map(trader -> item("location", trader.location(), "activation", format(trader.activation()),
                    "activationInstant", trader.activation() == null ? "" : trader.activation().toString(), "expiry",
                    format(trader.expiry()), "expiryInstant", trader.expiry() == null ? "" : trader.expiry().toString(),
                    "inventory",
                    trader.inventory().stream().map(product -> item("item", product.item(), "primePrice",
                        product.primePrice(), "regularPrice", product.regularPrice())).toList()))
                .toList(),
            "items", "itemCount");
    }

    /**
     * 构造时间戳的命令展示字段。
     *
     * @param state 已经解析的当前世界状态。
     * @return 当前命令的标题、状态及展示条目。
     */
    public static WarframeWorldStateCommandVO timestamp(WorldStateDTO state) {
        return output(state, "世界时间", "世界状态时间戳",
            state.timestamp() == null ? List.of() : List.of(item("timestamp", format(state.timestamp()))), "items",
            "itemCount");
    }

    /**
     * 构造当前数据不可用时的明确展示结果。
     *
     * @param state 已经解析的当前世界状态。
     * @param name 当前业务对象的名称。
     * @return 当前命令的标题、状态及展示条目。
     */
    public static WarframeWorldStateCommandVO unavailable(WorldStateDTO state, String name) {
        Map<String, Object> data = base(state, name);
        data.put("available", false);
        return new WarframeWorldStateCommandVO(name + "功能已注册，但真实数据源尚未接入，当前不返回演示数据", data);
    }

    /**
     * 创建所有世界状态命令共用的标题、时间及快照状态字段。
     *
     * @param state 已经解析的当前世界状态。
     * @param title 标题。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    public static Map<String, Object> base(WorldStateDTO state, String title) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("title", title);
        data.put("source", state.metadata().source());
        data.put("fetchedAt", format(state.metadata().fetchedAt()));
        data.put("providerTimestamp", format(state.metadata().providerTimestamp()));
        data.put("stale", state.stale());
        data.put("updatedAt", format(state.timestamp()));
        // 统一模板上下文，避免空数据或未接入命令缺少通用变量。
        data.put("items", List.of());
        data.put("itemCount", 0);
        data.put("available", true);
        return data;
    }

    /**
     * 将世界状态条目及计数组织为当前命令使用的展示载荷。
     *
     * @param state 已经解析的当前世界状态。
     * @param title 标题。
     * @param text 待处理的文本。
     * @param items 当前物品或成员列表。
     * @param itemsKey 物品键。
     * @param countKey 数量键。
     * @return 当前命令的标题、状态及展示条目。
     */
    private static WarframeWorldStateCommandVO output(WorldStateDTO state, String title, String text,
        List<Map<String, Object>> items, String itemsKey, String countKey) {
        Map<String, Object> data = base(state, title);
        data.put(itemsKey, items);
        data.put(countKey, items.size());
        if (!"itemCount".equals(countKey)) {
            data.put("itemCount", items.size());
        }
        data.put("available", CollUtil.isNotEmpty(items));
        if (CollUtil.isEmpty(items)) {
            text = "当前没有可用的 " + title + " 数据";
        }
        return new WarframeWorldStateCommandVO(text, data);
    }

    /**
     * 构造突击的命令展示字段。
     *
     * @param sortie 突击。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    private static Map<String, Object> sortie(SortieDTO sortie) {
        return item("id", sortie.id(), "boss", sortie.boss(), "expiry", format(sortie.expiry()), "expiryInstant",
            sortie.expiry() == null ? "" : sortie.expiry().toString(), "remaining", remaining(sortie.expiry()),
            "variants", sortie.variants().stream().map(variant -> item("missionType", variant.missionType(), "modifier",
                variant.modifier(), "node", variant.node())).toList());
    }

    /**
     * 构造周期的命令展示字段。
     *
     * @param value 待校验或规范化的文本。
     * @param expiry 到期。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    private static Map<String, Object> cycle(String value, Instant expiry) {
        return item("value", value, "expiry", format(expiry), "expiryInstant", expiry == null ? "" : expiry.toString(),
            "remaining", remaining(expiry));
    }

    /**
     * 构造物品的命令展示字段。
     *
     * @param values 待处理的值集合。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    private static Map<String, Object> item(Object... values) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (int index = 0; index + 1 < values.length; index += 2) {
            if (ObjectUtil.isNotNull(values[index])) {
                result.put(String.valueOf(values[index]),
                    ObjectUtil.isNull(values[index + 1]) ? "" : values[index + 1]);
            }
        }
        return result;
    }

    /**
     * 将业务数据格式化为展示文本。
     *
     * @param instant 时间点。
     * @return 将业务数据格式化为展示文本对应的文本。
     */
    private static String format(Instant instant) {
        return ObjectUtil.isNull(instant) ? "" : TIME_FORMAT.format(instant);
    }

    /**
     * 将过期时间转换为面向用户的剩余时长文本。
     *
     * @param expiry 到期。
     * @return 截止时间相对于当前时刻的剩余时长文本。
     */
    private static String remaining(Instant expiry) {
        if (ObjectUtil.isNull(expiry)) {
            return "";
        }
        Duration duration = Duration.between(Instant.now(), expiry);
        if (duration.isNegative() || duration.isZero()) {
            return "已结束";
        }
        long seconds = Math.max(1, duration.toSeconds());
        if (seconds < SECONDS_PER_MINUTE) {
            return "%d秒".formatted(seconds);
        }
        if (seconds < SECONDS_PER_HOUR) {
            return "%d分%02d秒".formatted(seconds / SECONDS_PER_MINUTE, seconds % SECONDS_PER_MINUTE);
        }
        if (seconds >= SECONDS_PER_DAY) {
            return "%d天%02d小时%02d分".formatted(duration.toDays(), duration.toHoursPart(), duration.toMinutesPart());
        }
        return "%d小时%02d分%02d秒".formatted(duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart());
    }

    /**
     * 优先返回有效文本，缺省时使用指定显示值。
     *
     * @param value 待校验或规范化的文本。
     * @param fallback 缺省。
     * @return 当前值的业务表示对应的文本。
     */
    private static String value(String value, String fallback) {
        return StrUtil.isBlank(value) ? fallback : value;
    }
}
