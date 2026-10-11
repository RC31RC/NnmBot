package com.nnm.bot.warframe.plugin;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.nnm.bot.warframe.model.WorldState;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * WorldState 内部业务投影，供引用解析、管理端和订阅匹配使用。
 * 命令展示字段由 WarframeWorldStateDisplayMapper 在引用解析之后选择。
 */
public final class WarframeCommandData {

    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("MM-dd HH:mm").withZone(ZONE_ID);

    private WarframeCommandData() {
    }

    public static AbstractWorldStateCommandPlugin.WorldStateCommandOutput alerts(WorldState state) {
        List<Map<String, Object>> items = new ArrayList<>();
        for (WorldState.Alert alert : state.alerts()) {
            // 官方 WorldState 可能短时间保留已结束警报，当前命令只投影仍有效的任务。
            if (alert.expiry() != null && !alert.expiry().isAfter(Instant.now())) {
                continue;
            }
            Map<String, Object> item = item(
                    "id", alert.id(),
                    "mission", alert.mission(),
                    "reward", alert.reward(),
                    "node", alert.node(),
                    "faction", alert.faction(),
                    "minEnemyLevel", alert.minEnemyLevel(),
                    "maxEnemyLevel", alert.maxEnemyLevel(),
                    "activation", format(alert.activation()),
                    "expiry", format(alert.expiry()),
                    "remaining", remaining(alert.expiry())
            );
            item.put("rewardItems", alert.rewardItems().stream()
                    .map(reward -> item("item", reward.item(), "count", reward.count()))
                    .toList());
            items.add(item);
        }
        return output(state, "警报", "当前警报：" + items.size() + " 条", items,
                "items", "alertCount");
    }

    public static AbstractWorldStateCommandPlugin.WorldStateCommandOutput sorties(
            WorldState state, boolean archon) {
        List<WorldState.Sortie> source = archon ? state.archonHunts() : state.allSorties();
        List<Map<String, Object>> items = source.stream().map(WarframeCommandData::sortie).toList();
        String title = archon ? "执刑官猎杀" : "突击";
        return output(state, title, title + "：" + items.size() + " 条", items,
                "items", "itemCount");
    }

    public static AbstractWorldStateCommandPlugin.WorldStateCommandOutput voidTrader(WorldState state) {
        List<Map<String, Object>> items = state.allVoidTraders().stream()
                .map(trader -> {
                    Map<String, Object> item = item(
                            "location", trader.location(),
                            "activation", format(trader.activation()),
                            "expiry", format(trader.expiry()),
                            "remaining", remaining(trader.expiry()),
                            "active", trader.active()
                    );
                    item.put("inventory", trader.inventory().stream()
                            .map(product -> item(
                                    "item", product.item(),
                                    "ducats", product.ducats(),
                                    "credits", product.credits()))
                            .toList());
                    return item;
                })
                .toList();
        return output(state, "奸商", "奸商数据：" + items.size() + " 条", items,
                "items", "itemCount");
    }

    public static AbstractWorldStateCommandPlugin.WorldStateCommandOutput arbitration(
            WorldState state, boolean table) {
        WorldState.Arbitration arbitration = state.arbitration();
        if (ObjectUtil.isNull(arbitration)) {
            return new AbstractWorldStateCommandPlugin.WorldStateCommandOutput(
                    table ? "仲裁轮换表暂时没有可用数据" : "当前没有进行中的仲裁",
                    base(state, table ? "仲裁表" : "仲裁"));
        }
        Map<String, Object> current = item(
                "node", arbitration.node(),
                "type", arbitration.type(),
                "expiry", format(arbitration.expiry()),
                "remaining", remaining(arbitration.expiry())
        );
        Map<String, Object> data = base(state, table ? "仲裁表" : "仲裁");
        data.put("current", current);
        data.put("items", List.of(current));
        data.put("itemCount", 1);
        String text = "仲裁：" + value(arbitration.node(), "未知节点")
                + "（" + value(arbitration.type(), "未知任务") + "）"
                + "，剩余 " + value(remaining(arbitration.expiry()), "未知");
        return new AbstractWorldStateCommandPlugin.WorldStateCommandOutput(text, data);
    }

    public static AbstractWorldStateCommandPlugin.WorldStateCommandOutput dailyDeals(WorldState state) {
        List<Map<String, Object>> items = state.dailyDeals().stream()
                .map(deal -> item(
                        "item", deal.item(),
                        "stock", deal.stock(),
                        "sold", deal.sold(),
                        "credits", deal.credits(),
                        "platinum", deal.platinum(),
                        "activation", format(deal.activation()),
                        "expiry", format(deal.expiry()),
                        "remaining", remaining(deal.expiry())
                ))
                .toList();
        return output(state, "每日特惠", "每日特惠：" + items.size() + " 条", items,
                "items", "itemCount");
    }

    public static AbstractWorldStateCommandPlugin.WorldStateCommandOutput invasions(WorldState state) {
        List<Map<String, Object>> items = state.invasions().stream()
                .filter(invasion -> !invasion.completed())
                .map(invasion -> item(
                        "node", invasion.node(),
                        "completion", invasion.completion(),
                        "completed", invasion.completed(),
                        "attackerReward", invasion.attackerReward(),
                        "defenderReward", invasion.defenderReward(),
                        "activation", format(invasion.activation()),
                        "attackerRewards", invasion.attackerRewards().stream()
                                .map(reward -> item("item", reward.item(), "count", reward.count())).toList(),
                        "defenderRewards", invasion.defenderRewards().stream()
                                .map(reward -> item("item", reward.item(), "count", reward.count())).toList()
                ))
                .toList();
        return output(state, "入侵", "当前入侵：" + items.size() + " 条", items,
                "items", "itemCount");
    }

    public static AbstractWorldStateCommandPlugin.WorldStateCommandOutput fissures(
            WorldState state, Predicate<WorldState.ActiveMission> filter, String title) {
        List<Map<String, Object>> items = state.allActiveMissions().stream()
                .filter(filter)
                .map(mission -> item(
                        "id", mission.id(),
                        "node", mission.node(),
                        "missionType", mission.missionType(),
                        "tier", mission.tier(),
                        "hard", mission.hard(),
                        "voidStorm", mission.voidStorm(),
                        "activation", format(mission.activation()),
                        "expiry", format(mission.expiry()),
                        "remaining", remaining(mission.expiry())
                ))
                .toList();
        return output(state, title, title + "：" + items.size() + " 条", items,
                "items", "itemCount");
    }

    public static AbstractWorldStateCommandPlugin.WorldStateCommandOutput steelPath(WorldState state) {
        List<Map<String, Object>> items = state.steelPathOfferings().stream()
                .map(offering -> item(
                        "node", offering.node(),
                        "reward", offering.reward(),
                        "activation", format(offering.activation()),
                        "expiry", format(offering.expiry()),
                        "remaining", remaining(offering.expiry())
                ))
                .toList();
        return output(state, "钢铁奖励", "钢铁路径奖励：" + items.size() + " 条", items,
                "items", "itemCount");
    }

    public static AbstractWorldStateCommandPlugin.WorldStateCommandOutput cycles(WorldState state) {
        return cycles(state, "平原");
    }

    public static AbstractWorldStateCommandPlugin.WorldStateCommandOutput cycles(
            WorldState state, String title) {
        Map<String, Object> cycles = new LinkedHashMap<>();
        if (ObjectUtil.isNotNull(state.earthCycle())) {
            cycles.put("地球", cycle(state.earthCycle().isDay() ? "白天" : "夜晚",
                    state.earthCycle().expiry()));
        }
        if (ObjectUtil.isNotNull(state.cetusCycle())) {
            cycles.put("夜灵平原", cycle(state.cetusCycle().isDay() ? "白天" : "夜晚",
                    state.cetusCycle().expiry()));
        }
        if (ObjectUtil.isNotNull(state.vallisCycle())) {
            cycles.put("福尔图娜", cycle(state.vallisCycle().isWarm() ? "温暖" : "寒冷",
                    state.vallisCycle().expiry()));
        }
        if (ObjectUtil.isNotNull(state.cambionCycle())) {
            cycles.put("魔胎之境", cycle(state.cambionCycle().active(),
                    state.cambionCycle().expiry()));
        }
        if (ObjectUtil.isNotNull(state.zarimanCycle())) {
            cycles.put("扎里曼", cycle(state.zarimanCycle().state(),
                    state.zarimanCycle().expiry()));
        }
        if (ObjectUtil.isNotNull(state.duviriCycle())) {
            cycles.put("双衍王境", cycle(state.duviriCycle().state(),
                    state.duviriCycle().expiry()));
        }
        if ("双衍王境".equals(title)) {
            cycles.keySet().removeIf(name -> !"双衍王境".equals(name));
        }
        Map<String, Object> data = base(state, title);
        data.put("cycles", cycles);
        data.put("items", cycles.entrySet().stream()
                .map(entry -> item("name", entry.getKey(), "value", entry.getValue()))
                .toList());
        data.put("itemCount", cycles.size());
        return new AbstractWorldStateCommandPlugin.WorldStateCommandOutput(
                title + "：" + cycles.size() + " 个", data);
    }

    public static AbstractWorldStateCommandPlugin.WorldStateCommandOutput syndicate(
            WorldState state, String title, Predicate<String> matcher) {
        List<Map<String, Object>> items = state.syndicateMissions().stream()
                .filter(mission -> matcher.test(value(mission.syndicate(), "")))
                .map(mission -> item(
                        "syndicate", mission.syndicate(),
                        "node", mission.node(),
                        "nodes", mission.nodes(),
                        "activation", format(mission.activation()),
                        "expiry", format(mission.expiry()),
                        "remaining", remaining(mission.expiry()),
                        "jobs", mission.jobs().stream().map(job -> item(
                                "jobType", value(job.jobType(), job.type()),
                                "type", value(job.type(), job.isVault() ? "隔离库" : job.jobType()),
                                "minEnemyLevel", job.minEnemyLevel(),
                                "maxEnemyLevel", job.maxEnemyLevel(),
                                "standingStages", job.standingStages(),
                                "minMR", job.minMR(),
                                "isVault", job.isVault(),
                                "locationTag", job.locationTag(),
                                "expiry", format(job.expiry()),
                                "remaining", remaining(job.expiry()),
                                "timeBound", job.timeBound(),
                                "rewardPoolReference", job.rewardPoolReference(),
                                "rewards", job.rewardPool().stream().map(reward -> item(
                                        "item", reward.item(), "count", reward.count(),
                                        "chance", reward.chance(), "rarity", reward.rarity())).toList()
                        )).toList()
                ))
                .toList();
        return output(state, title, title + "：" + items.size() + " 条", items,
                "items", "itemCount");
    }

    public static AbstractWorldStateCommandPlugin.WorldStateCommandOutput nightwave(WorldState state) {
        List<Map<String, Object>> items = state.nightwave().stream()
                .map(mission -> item(
                        "id", mission.id(),
                        "challenge", mission.challenge(),
                        "title", mission.title(),
                        "description", mission.desc(),
                        "reputation", mission.reputation(),
                        "daily", mission.isDaily()
                ))
                .toList();
        return output(state, "电波", "电波任务：" + items.size() + " 条", items,
                "items", "itemCount");
    }

    public static AbstractWorldStateCommandPlugin.WorldStateCommandOutput archimedeas(
            WorldState state, String title, Predicate<WorldState.Archimedea> filter) {
        List<Map<String, Object>> items = state.archimedeas().stream().filter(filter).map(conquest -> item(
                "id", conquest.id(),
                "type", conquest.type(),
                "typeKey", conquest.typeKey(),
                "activation", format(conquest.activation()),
                "expiry", format(conquest.expiry()),
                "remaining", remaining(conquest.expiry()),
                "personalModifiers", conquest.personalModifiers(),
                "missions", conquest.missions().stream().map(mission -> item(
                        "faction", mission.faction(),
                        "factionKey", mission.factionKey(),
                        "missionType", mission.missionType(),
                        "missionTypeKey", mission.missionTypeKey(),
                        "deviation", mission.deviation(),
                        "risks", mission.risks())).toList()
        )).toList();
        return output(state, title, title + "：" + items.size() + " 条", items,
                "items", "itemCount");
    }

    public static AbstractWorldStateCommandPlugin.WorldStateCommandOutput descents(WorldState state) {
        List<Map<String, Object>> items = state.descents().stream().limit(1).map(descent -> item(
                "seed", descent.seed(),
                "challenges", descent.challenges().stream().map(challenge -> item(
                        "index", challenge.index(),
                        "type", challenge.type(),
                        "challenge", challenge.challenge(),
                        "level", challenge.level(),
                        "specs", challenge.specs(),
                        "auras", challenge.auras())).toList()
        )).toList();
        return output(state, "深层下降", "深层下降：" + items.size() + " 个轮换", items,
                "items", "itemCount");
    }

    public static AbstractWorldStateCommandPlugin.WorldStateCommandOutput globalUpgrades(WorldState state) {
        List<Map<String, Object>> items = state.globalUpgrades().stream()
                .map(upgrade -> item(
                        "id", upgrade.id(),
                        "upgradeType", upgrade.upgradeType(),
                        "operationType", upgrade.operationType(),
                        "value", upgrade.value(),
                        "activation", format(upgrade.activation()),
                        "expiry", format(upgrade.expiry()),
                        "remaining", remaining(upgrade.expiry())))
                .toList();
        return output(state, "全局增益", "全局增益：" + items.size() + " 条", items,
                "items", "itemCount");
    }

    public static AbstractWorldStateCommandPlugin.WorldStateCommandOutput flashSales(WorldState state) {
        List<Map<String, Object>> items = state.flashSales().stream()
                .map(sale -> item(
                        "id", sale.id(),
                        "item", sale.uniqueName(),
                        "discount", sale.discount(),
                        "regularOverride", sale.regularOverride(),
                        "premiumOverride", sale.premiumOverride(),
                        "shownInMarket", sale.shownInMarket(),
                        "featured", sale.featured(),
                        "popular", sale.popular(),
                        "activation", format(sale.activation()),
                        "expiry", format(sale.expiry()),
                        "remaining", remaining(sale.expiry())))
                .toList();
        return output(state, "促销", "促销：" + items.size() + " 条", items,
                "items", "itemCount");
    }

    public static AbstractWorldStateCommandPlugin.WorldStateCommandOutput persistentEnemies(WorldState state) {
        List<Map<String, Object>> items = state.persistentEnemies().stream()
                .map(enemy -> item(
                        "id", enemy.id(),
                        "agentType", enemy.agentType(),
                        "locationTag", enemy.locationTag(),
                        "rank", enemy.rank(),
                        "healthPercent", enemy.healthPercent(),
                        "fleeDamage", enemy.fleeDamage(),
                        "region", enemy.region(),
                        "lastDiscoveredTime", format(enemy.lastDiscoveredTime()),
                        "lastDiscoveredLocation", enemy.lastDiscoveredLocation(),
                        "discovered", enemy.discovered(),
                        "usingTicketing", enemy.usingTicketing()))
                .toList();
        return output(state, "小小黑", "小小黑：" + items.size() + " 条", items,
                "items", "itemCount");
    }

    public static AbstractWorldStateCommandPlugin.WorldStateCommandOutput events(WorldState state) {
        List<Map<String, Object>> items = state.events().stream()
                .map(event -> item(
                        "id", event.id(),
                        "title", event.title(),
                        "description", event.description(),
                        "category", event.category(),
                        "node", event.node(),
                        "progress", event.progress(),
                        "goal", event.goal(),
                        "activation", format(event.activation()),
                        "expiry", format(event.expiry()),
                        "remaining", remaining(event.expiry()),
                        "rewards", event.rewards().stream()
                                .map(reward -> item("item", reward.item(), "count", reward.count())).toList(),
                        "rewardCredits", event.rewardCredits()
                ))
                .toList();
        return output(state, "活动目标", "活动目标：" + items.size() + " 条", items,
                "items", "itemCount");
    }

    public static AbstractWorldStateCommandPlugin.WorldStateCommandOutput news(WorldState state) {
        List<Map<String, Object>> items = state.news().stream()
                .map(news -> item(
                        "id", news.id(),
                        "title", news.title(),
                        "link", news.link(),
                        "imageUrl", news.imageUrl(),
                        "publishedAt", format(news.publishedAt()),
                        "expiry", format(news.expiry()),
                        "priority", news.priority()
                ))
                .toList();
        return output(state, "新闻", "新闻：" + items.size() + " 条", items,
                "items", "itemCount");
    }

    public static AbstractWorldStateCommandPlugin.WorldStateCommandOutput conclave(WorldState state) {
        List<Map<String, Object>> items = state.conclaveChallenges().stream()
                .filter(challenge -> !challenge.rootChallenge())
                .map(challenge -> item(
                        "id", challenge.id(),
                        "mode", challenge.mode(),
                        "category", challenge.category(),
                        "categoryKey", challenge.categoryKey(),
                        "title", challenge.title(),
                        "description", challenge.description(),
                        "amount", challenge.amount(),
                        "standing", challenge.standing(),
                        "daily", challenge.daily(),
                        "activation", format(challenge.activation()),
                        "expiry", format(challenge.expiry()),
                        "remaining", remaining(challenge.expiry())))
                .toList();
        return output(state, "竞技场", "竞技场：" + items.size() + " 条", items,
                "items", "itemCount");
    }

    public static AbstractWorldStateCommandPlugin.WorldStateCommandOutput constructionProgress(WorldState state) {
        var progress = state.constructionProgress();
        List<Map<String, Object>> items = progress == null ? List.of() : List.of(
                item("name", "巨人战舰", "progress", progress.fomorian()),
                item("name", "利刃豺狼", "progress", progress.razorback()),
                item("name", "未标明项目", "progress", progress.unknown()));
        return output(state, "舰队建造", "舰队建造进度", items, "items", "itemCount");
    }

    public static AbstractWorldStateCommandPlugin.WorldStateCommandOutput simaris(WorldState state) {
        var target = state.simaris();
        return output(state, "西玛里斯", "西玛里斯公共研究目标",
                target == null ? List.of() : List.of(item("title", target.target(),
                        "active", target.active())), "items", "itemCount");
    }

    public static AbstractWorldStateCommandPlugin.WorldStateCommandOutput sentientOutposts(WorldState state) {
        var outpost = state.sentientOutpost();
        return output(state, "先遣舰", "先遣舰当前节点",
                outpost == null ? List.of() : List.of(item("node", outpost.node(),
                        "activation", format(outpost.activation()), "expiry", format(outpost.expiry()),
                        "remaining", remaining(outpost.expiry()))), "items", "itemCount");
    }

    public static AbstractWorldStateCommandPlugin.WorldStateCommandOutput clanInitiatives(WorldState state) {
        return output(state, "氏族周任务", "氏族周任务", state.clanInitiatives().stream()
                .map(clan -> item("week", clan.week(), "location", clan.region(),
                        "rewards", clan.rewards().stream().map(reward -> item(
                                "item", reward.item(), "count", reward.count(),
                                "points", reward.points(), "claimed", reward.claimed())).toList())).toList(),
                "items", "itemCount");
    }

    public static AbstractWorldStateCommandPlugin.WorldStateCommandOutput circuit(
            WorldState state, boolean hard) {
        return output(state, hard ? "灵化轮换" : "普通回环", "回环当前奖励",
                state.circuitChoices().stream()
                        .filter(choice -> (hard ? "EXC_HARD" : "EXC_NORMAL").equalsIgnoreCase(choice.category())
                                || (hard ? "hard" : "normal").equalsIgnoreCase(choice.category()))
                        .flatMap(choice -> choice.choices().stream())
                        .map(choice -> item("item", choice.replaceAll("([a-z0-9])([A-Z])", "$1 $2")
                                .replace(" And ", " & "))).toList(), "items", "itemCount");
    }

    public static AbstractWorldStateCommandPlugin.WorldStateCommandOutput calendar(WorldState state) {
        return output(state, "实时日历", "1999 实时日历", state.calendarSeasons().stream()
                .flatMap(season -> season.days().stream().map(day ->
                        item("season", season.season(), "iteration", season.iteration(), "date", day.date(),
                                "events", day.events().stream().map(event -> item(
                                        "type", event.type(), "title", event.title(),
                                        "description", event.description(), "reward", event.reward(),
                                        "dialogueName", event.dialogueName(), "dialogueConvo", event.dialogueConvo()))
                                        .toList()))).toList(), "items", "itemCount");
    }

    public static AbstractWorldStateCommandPlugin.WorldStateCommandOutput vaultTrader(WorldState state) {
        return output(state, "宝库商人", "宝库商人", state.vaultTraders().stream().map(trader ->
                item("location", trader.location(), "activation", format(trader.activation()),
                        "expiry", format(trader.expiry()), "inventory", trader.inventory().stream()
                                .map(product -> item("item", product.item(), "primePrice", product.primePrice(),
                                        "regularPrice", product.regularPrice())).toList())).toList(),
                "items", "itemCount");
    }

    public static AbstractWorldStateCommandPlugin.WorldStateCommandOutput timestamp(WorldState state) {
        return output(state, "世界时间", "世界状态时间戳",
                state.timestamp() == null ? List.of() : List.of(item("timestamp", format(state.timestamp()))),
                "items", "itemCount");
    }

    public static AbstractWorldStateCommandPlugin.WorldStateCommandOutput unavailable(
            WorldState state, String name) {
        Map<String, Object> data = base(state, name);
        data.put("available", false);
        return new AbstractWorldStateCommandPlugin.WorldStateCommandOutput(
                name + "功能已注册，但真实数据源尚未接入，当前不返回演示数据", data);
    }

    public static Map<String, Object> base(WorldState state, String title) {
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

    private static AbstractWorldStateCommandPlugin.WorldStateCommandOutput output(
            WorldState state, String title, String text, List<Map<String, Object>> items,
            String itemsKey, String countKey) {
        Map<String, Object> data = base(state, title);
        data.put(itemsKey, items);
        data.put(countKey, items.size());
        if (!"itemCount".equals(countKey)) {
            data.put("itemCount", items.size());
        }
        data.put("available", !items.isEmpty());
        if (items.isEmpty()) {
            text = "当前没有可用的 " + title + " 数据";
        }
        return new AbstractWorldStateCommandPlugin.WorldStateCommandOutput(text, data);
    }

    private static Map<String, Object> sortie(WorldState.Sortie sortie) {
        return item(
                "id", sortie.id(),
                "boss", sortie.boss(),
                "expiry", format(sortie.expiry()),
                "remaining", remaining(sortie.expiry()),
                "variants", sortie.variants().stream()
                        .map(variant -> item(
                                "missionType", variant.missionType(),
                                "modifier", variant.modifier(),
                                "node", variant.node()))
                        .toList()
        );
    }

    private static Map<String, Object> cycle(String value, Instant expiry) {
        return item("value", value, "expiry", format(expiry), "remaining", remaining(expiry));
    }

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

    private static String format(Instant instant) {
        return ObjectUtil.isNull(instant) ? "" : TIME_FORMAT.format(instant);
    }

    private static String remaining(Instant expiry) {
        if (ObjectUtil.isNull(expiry)) {
            return "";
        }
        Duration duration = Duration.between(Instant.now(), expiry);
        if (duration.isNegative() || duration.isZero()) {
            return "已结束";
        }
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        if (hours > 24) {
            return "%d天%02d小时".formatted(duration.toDays(), duration.toHoursPart());
        }
        return "%d小时%02d分".formatted(hours, minutes);
    }

    private static String value(String value, String fallback) {
        return StrUtil.isBlank(value) ? fallback : value;
    }
}
