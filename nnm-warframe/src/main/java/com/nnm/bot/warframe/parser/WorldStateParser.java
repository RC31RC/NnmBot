package com.nnm.bot.warframe.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.MissingNode;
import java.time.LocalDate;
import java.util.Locale;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.warframe.model.WorldState;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 将 warframestat.us 和官方 worldState 风格 JSON 转成 NnmBot 领域模型。
 *
 * <p>解析器只处理协议兼容性：字段别名、时间戳格式、空数组和部分缺失字段。
 * 未知字段会被忽略，但不会阻断已知业务数据；模型本身不携带 JsonNode。</p>
 */
@Component
public class WorldStateParser {

    @Resource
    private ObjectMapper objectMapper;

    public WorldState parse(String json) {
        return parse(json, "warframe-official");
    }

    public WorldState parse(String json, String source) {
        if (StrUtil.isBlank(json)) {
            throw new IllegalArgumentException("world state response must not be blank");
        }
        try {
            JsonNode root = objectMapper.readTree(json);
            if (ObjectUtil.isNull(root) || !root.isObject()) {
                throw new IllegalArgumentException("world state response must be a JSON object");
            }
            Instant providerTimestamp = instant(root, "timestamp", "time", "serverTime");
            Instant fetchedAt = Instant.now();
            WorldState state = new WorldState(
                    alerts(root),
                    fissures(root),
                    invasions(root),
                    firstSortie(root),
                    cetusCycle(root),
                    vallisCycle(root),
                    cambionCycle(root),
                    firstVoidTrader(root),
                    arbitration(root),
                    nightwave(root),
                    providerTimestamp,
                    sorties(root),
                    voidTraders(root),
                    earthCycle(root),
                    duviriCycle(root),
                    zarimanCycle(root),
                    dailyDeals(root),
                    events(root),
                    news(root),
                    syndicateMissions(root),
                    steelPathOfferings(root),
                    activeMissions(root),
                    archonHunts(root),
                    text(root, "buildLabel", "BuildLabel"),
                    globalUpgrades(root),
                    flashSales(root),
                    persistentEnemies(root),
                    new WorldState.WorldStateMetadata(source, fetchedAt, providerTimestamp, false,
                            "nnm-world-state-v1", sha256(root.toString())),
                    archimedeas(root),
                    conclaveChallenges(root),
                    constructionProgress(root),
                    simaris(root),
                    sentientOutpost(root),
                    clanInitiatives(root),
                    circuitChoices(root),
                    calendarSeasons(root),
                    vaultTraders(root),
                    weeklyChallenges(root),
                    darkSectors(root),
                    descents(root)
            );
            validate(state);
            return state;
        } catch (Exception exception) {
            if (exception instanceof IllegalArgumentException illegalArgumentException) {
                throw illegalArgumentException;
            }
            throw new IllegalArgumentException(
                    "failed to parse world state JSON", exception);
        }
    }

    private List<WorldState.Alert> alerts(JsonNode root) {
        List<WorldState.Alert> result = new ArrayList<>();
        for (JsonNode item : array(root, "alerts")) {
            Instant expiry = instant(item, "expiry", "expiration");
            JsonNode reward = first(item, "reward", "mission.reward", "missionInfo.missionReward");
            List<WorldState.AlertReward> rewardItems = alertRewards(reward);
            result.add(new WorldState.Alert(
                    text(item, "id", "_id"),
                    text(item, "mission.type", "missionInfo.missionType", "mission", "description"),
                    rewardSummary(reward, rewardItems),
                    expiry,
                    instant(item, "activation"),
                    text(item, "node", "mission.node", "mission.location", "missionInfo.location"),
                    text(item, "faction", "mission.faction", "missionInfo.faction"),
                    integer(item, "minEnemyLevel", "mission.minEnemyLevel", "missionInfo.minEnemyLevel"),
                    integer(item, "maxEnemyLevel", "mission.maxEnemyLevel", "missionInfo.maxEnemyLevel"),
                    rewardItems
            ));
        }
        return result;
    }

    private List<WorldState.AlertReward> alertRewards(JsonNode reward) {
        List<WorldState.AlertReward> result = new ArrayList<>();
        for (String field : List.of("items", "countedItems")) {
            for (JsonNode entry : array(reward, field)) {
                String item = entry.isTextual() ? entry.asText()
                        : text(entry, "itemType", "item", "name", "uniqueName", "type");
                if (StrUtil.isNotBlank(item)) {
                    result.add(new WorldState.AlertReward(item,
                            entry.isTextual() ? null : integer(entry, "itemCount", "count")));
                }
            }
        }
        return List.copyOf(result);
    }

    private List<WorldState.Fissure> fissures(JsonNode root) {
        Map<String, WorldState.Fissure> result = new LinkedHashMap<>();
        for (RawMission raw : activeMissionNodes(root)) {
            JsonNode item = raw.node();
            String tier = raw.voidStorm()
                    ? text(item, "tier", "modifier", "activeMissionTier", "ActiveMissionTier")
                    : text(item, "tier", "modifier");
            WorldState.Fissure fissure = new WorldState.Fissure(
                    text(item, "id", "_id"),
                    text(item, "node", "mission.node"),
                    tier,
                    text(item, "missionType", "mission.type"),
                    instant(item, "expiry", "expiration"),
                    instant(item, "activation"),
                    bool(item, "hard", "isHard", "Hard"),
                    raw.voidStorm() || bool(item, "voidStorm", "voidStorms", "isVoidStorm"));
            result.putIfAbsent(missionKey(fissure.id(), fissure.node(), fissure.missionType(),
                    fissure.activation(), fissure.expiry(), fissure.hard(), fissure.voidStorm()), fissure);
        }
        return List.copyOf(result.values());
    }

    private List<WorldState.Invasion> invasions(JsonNode root) {
        List<WorldState.Invasion> result = new ArrayList<>();
        for (JsonNode item : array(root, "invasions")) {
            JsonNode attackerReward = first(item, "attackerReward");
            JsonNode defenderReward = first(item, "defenderReward");
            List<WorldState.InvasionReward> attackerRewards = invasionRewards(attackerReward);
            List<WorldState.InvasionReward> defenderRewards = invasionRewards(defenderReward);
            result.add(new WorldState.Invasion(
                    text(item, "id", "_id"),
                    text(item, "node", "location"),
                    invasionRewardSummary(attackerReward, attackerRewards),
                    invasionRewardSummary(defenderReward, defenderRewards),
                    invasionCompletion(item),
                    instant(item, "activation"),
                    bool(item, "completed"),
                    attackerRewards,
                    defenderRewards
            ));
        }
        return result;
    }

    private float invasionCompletion(JsonNode item) {
        Double completion = number(item, "completion", "completionRate");
        if (completion != null && Double.isFinite(completion)) {
            return completion.floatValue();
        }
        Double count = number(item, "count");
        Double goal = number(item, "goal");
        if (count == null || goal == null || !Double.isFinite(count)
                || !Double.isFinite(goal) || goal <= 0) {
            return 0;
        }
        // Count 是双方净争夺值；感染入侵的范围为 [-Goal, 0]，普通入侵为 [-Goal, Goal]。
        boolean infestation = StrUtil.emptyIfNull(text(item, "defenderMissionInfo.faction"))
                .toLowerCase(Locale.ROOT).contains("infest");
        return (float) Math.clamp((1 + count / goal) * (infestation ? 100 : 50), 0, 100);
    }

    private List<WorldState.InvasionReward> invasionRewards(JsonNode reward) {
        List<WorldState.InvasionReward> result = new ArrayList<>();
        for (String field : List.of("items", "countedItems")) {
            for (JsonNode entry : array(reward, field)) {
                String name = entry.isTextual() ? entry.asText()
                        : text(entry, "itemType", "item", "name", "uniqueName", "type");
                if (StrUtil.isNotBlank(name)) {
                    result.add(new WorldState.InvasionReward(name,
                            entry.isTextual() ? null : integer(entry, "itemCount", "count")));
                }
            }
        }
        return List.copyOf(result);
    }

    private String invasionRewardSummary(JsonNode reward, List<WorldState.InvasionReward> items) {
        if (reward.isTextual()) {
            return reward.asText();
        }
        String direct = text(reward, "asString", "description", "string");
        if (StrUtil.isNotBlank(direct)) {
            return direct;
        }
        List<String> parts = new ArrayList<>();
        for (WorldState.InvasionReward item : items) {
            parts.add(item.item() + (item.count() == null ? "" : " x" + item.count()));
        }
        int credits = integerOrZero(reward, "credits");
        if (credits > 0) {
            parts.add(credits + " credits");
        }
        return String.join(", ", parts);
    }

    private List<WorldState.Sortie> sorties(JsonNode root) {
        return sorties(root, "sorties", "sortie");
    }

    private List<WorldState.Sortie> archonHunts(JsonNode root) {
        return sorties(root, "archonHunts", "archonHunt", "liteSorties");
    }

    private List<WorldState.Sortie> sorties(JsonNode root, String... paths) {
        List<WorldState.Sortie> result = new ArrayList<>();
        for (JsonNode item : array(root, paths)) {
            List<WorldState.SortieVariant> variants = new ArrayList<>();
            for (JsonNode variant : array(item, "variants", "missions")) {
                variants.add(new WorldState.SortieVariant(
                        text(variant, "missionType", "type"),
                        text(variant, "modifier", "modifierType", "modifierDescription"),
                        text(variant, "node", "location")
                ));
            }
            result.add(new WorldState.Sortie(text(item, "id", "_id"), text(item, "boss", "bossName"),
                    variants, instant(item, "expiry", "expiration")));
        }
        return result;
    }

    private List<WorldState.VoidTrader> voidTraders(JsonNode root) {
        List<WorldState.VoidTrader> result = new ArrayList<>();
        for (JsonNode item : array(root, "voidTraders", "voidTrader")) {
            List<WorldState.VoidTraderItem> inventory = new ArrayList<>();
            for (JsonNode product : array(item, "inventory", "manifest", "Manifest")) {
                inventory.add(new WorldState.VoidTraderItem(
                        text(product, "item", "itemType", "uniqueName", "ItemType"),
                        integerOrZero(product, "ducats", "primePrice", "PrimePrice"),
                        integerOrZero(product, "credits", "regularPrice", "RegularPrice")
                ));
            }
            Instant activation = instant(item, "activation", "Activation");
            Instant expiry = instant(item, "expiry", "expiration", "Expiry");
            boolean completed = bool(item, "completed", "Completed");
            boolean active = !completed && activation != null && expiry != null
                    && !Instant.now().isBefore(activation) && Instant.now().isBefore(expiry);
            result.add(new WorldState.VoidTrader(text(item, "location", "node", "Node"), activation,
                    expiry, active, inventory));
        }
        return result;
    }

    private List<WorldState.NightwaveMission> nightwave(JsonNode root) {
        List<WorldState.NightwaveMission> result = new ArrayList<>();
        JsonNode season = first(root, "nightwave", "seasonInfo", "SeasonInfo");
        for (JsonNode item : array(season, "missions", "activeChallenges")) {
            String id = text(item, "id", "_id");
            String challenge = text(item, "challenge", "Challenge");
            boolean daily = bool(item, "isDaily", "daily", "dailyTasks", "Daily");
            boolean elite = challenge.toLowerCase(Locale.ROOT).contains("hard");
            int standing = integerOrZero(item, "reputation", "standing");
            if (standing == 0) {
                standing = 1000 + (daily ? 0 : 3500) + (elite ? 2500 : 0);
            }
            result.add(new WorldState.NightwaveMission(
                    id,
                    challenge,
                    text(item, "title", "name"),
                    text(item, "desc", "description"),
                    standing,
                    daily));
        }
        return result;
    }

    private List<WorldState.DailyDeal> dailyDeals(JsonNode root) {
        List<WorldState.DailyDeal> result = new ArrayList<>();
        for (JsonNode item : array(root, "dailyDeals")) {
            result.add(new WorldState.DailyDeal(text(item, "id", "_id"),
                    text(item, "item", "itemType", "storeItem"),
                    integerOrZero(item, "stock", "total", "amountTotal"),
                    integerOrZero(item, "sold", "amountSold"),
                    integerOrZero(item, "credits", "regularPrice", "originalPrice"),
                    integerOrZero(item, "platinum", "primePrice", "salePrice"),
                    instant(item, "activation"), instant(item, "expiry", "expiration")));
        }
        return result;
    }

    private List<WorldState.WorldEvent> events(JsonNode root) {
        List<WorldState.WorldEvent> result = new ArrayList<>();
        List<JsonNode> items = array(root, "Goals", "goals", "events");
        for (JsonNode item : items) {
            JsonNode reward = first(item, "reward");
            result.add(new WorldState.WorldEvent(
                    text(item, "id", "_id"),
                    text(item, "description", "desc", "title", "name"),
                    text(item, "tooltip", "toolTip", "description"),
                    instant(item, "activation", "eventStartDate", "date"),
                    instant(item, "expiry", "expiration", "eventEndDate"),
                    text(item, "category", "tag", "type"),
                    text(item, "node", "location"),
                    number(item, "currentScore", "healthPct", "count"),
                    number(item, "maximumScore", "goal"),
                    eventRewards(reward),
                    integer(reward, "credits")
            ));
        }
        return result;
    }

    private List<WorldState.WorldEventReward> eventRewards(JsonNode reward) {
        List<WorldState.WorldEventReward> result = new ArrayList<>();
        for (String field : List.of("items", "countedItems")) {
            for (JsonNode entry : array(reward, field)) {
                String name = entry.isTextual() ? entry.asText()
                        : text(entry, "itemType", "item", "name", "uniqueName", "type");
                if (StrUtil.isNotBlank(name)) {
                    result.add(new WorldState.WorldEventReward(name,
                            entry.isTextual() ? null : integer(entry, "itemCount", "count")));
                }
            }
        }
        return List.copyOf(result);
    }

    private List<WorldState.NewsItem> news(JsonNode root) {
        List<WorldState.NewsItem> result = new ArrayList<>();
        List<JsonNode> items;
        items = array(root, "news", "News", "Events", "events");
        for (JsonNode item : items) {
            result.add(new WorldState.NewsItem(
                    text(item, "id", "_id"),
                    eventTitle(item),
                    text(item, "link", "prop"),
                    text(item, "imageLink", "imageUrl", "image", "icon"),
                    instant(item, "date", "activation", "eventStartDate"),
                    instant(item, "expiry", "expiration", "eventEndDate"),
                    bool(item, "priority")
            ));
        }
        return result;
    }

    private WorldState.ConstructionProgress constructionProgress(JsonNode root) {
        JsonNode raw = first(root, "ProjectPct");
        if (raw != null && raw.isArray() && !raw.isEmpty()) {
            return new WorldState.ConstructionProgress(arrayNumber(raw, 0),
                    arrayNumber(raw, 1), arrayNumber(raw, 2));
        }
        JsonNode parsed = first(root, "constructionProgress");
        return parsed == null || !parsed.isObject() ? null : new WorldState.ConstructionProgress(
                number(parsed, "fomorianProgress"), number(parsed, "razorbackProgress"),
                number(parsed, "unknownProgress"));
    }

    private static Double arrayNumber(JsonNode array, int index) {
        JsonNode value = array.get(index);
        return value == null || !value.isNumber() ? null : value.doubleValue();
    }

    private WorldState.Simaris simaris(JsonNode root) {
        JsonNode raw = first(root, "LibraryInfo");
        if (raw != null && raw.isObject()) {
            String target = text(raw, "LastCompletedTargetType");
            return new WorldState.Simaris(target, StrUtil.isBlank(target));
        }
        JsonNode parsed = first(root, "simaris");
        return parsed == null || !parsed.isObject() ? null
                : new WorldState.Simaris(text(parsed, "target"), bool(parsed, "isTargetActive"));
    }

    private WorldState.SentientOutpost sentientOutpost(JsonNode root) {
        JsonNode parsed = first(root, "sentientOutposts");
        String node = text(parsed, "mission.node");
        if (StrUtil.isNotBlank(node)) {
            return new WorldState.SentientOutpost(node, instant(parsed, "activation"),
                    instant(parsed, "expiry"));
        }
        JsonNode tmp = first(root, "Tmp");
        if (tmp != null && tmp.isTextual()) {
            try {
                tmp = objectMapper.readTree(tmp.asText());
            } catch (JsonProcessingException ignored) {
                // Tmp 是独立嵌套 JSON；损坏时只缺失先遣舰，不丢弃整份世界状态。
                return null;
            }
        }
        Integer number = integer(tmp, "sfn");
        return number == null || number <= 0 ? null
                : new WorldState.SentientOutpost("CrewBattleNode" + number, null, null);
    }

    private List<WorldState.ClanInitiative> clanInitiatives(JsonNode root) {
        return array(root, "WeeklyVaultBonusRewards", "clanWeeklyInitiative").stream()
                .map(entry -> new WorldState.ClanInitiative(integer(entry, "WeekCount", "week"),
                        text(entry, "BonusRegion", "bonusRegion"),
                        array(entry, "Rewards", "rewwards", "rewards").stream()
                                .map(reward -> new WorldState.ClanReward(
                                        text(reward, "Reward", "uniqueName", "reward"),
                                        integer(reward, "ItemCount", "count"),
                                        integer(reward, "PointThreshold", "pointsRequired"),
                                        bool(reward, "RewardClaimed", "isClaimed"))).toList())).toList();
    }

    private List<WorldState.CircuitChoice> circuitChoices(JsonNode root) {
        List<JsonNode> schedule = array(root, "EndlessXpSchedule");
        List<JsonNode> choices = schedule.isEmpty()
                ? array(root, "duviriCycle.choices")
                : array(schedule.getFirst(), "CategoryChoices");
        return choices.stream().map(entry -> new WorldState.CircuitChoice(
                text(entry, "Category", "category"), strings(entry, "Choices", "choices"))).toList();
    }

    private List<WorldState.CalendarSeason> calendarSeasons(JsonNode root) {
        return array(root, "KnownCalendarSeasons", "calendar").stream()
                .map(season -> new WorldState.CalendarSeason(text(season, "Season", "season"),
                        integer(season, "YearIteration", "yearIteration"),
                        array(season, "Days", "days").stream().map(day -> {
                            Integer index = integer(day, "day");
                            String date = index != null && index >= 1 && index <= 365
                                    ? LocalDate.ofYearDay(1999, index).toString()
                                    : text(day, "date");
                            return new WorldState.CalendarDay(date,
                                    array(day, "events").stream().map(event -> new WorldState.CalendarEvent(
                                            text(event, "type"),
                                            text(event, "challenge.title", "upgrade.title", "challenge", "upgrade"),
                                            text(event, "challenge.description", "upgrade.description"),
                                            text(event, "uniqueName", "reward"),
                                            text(event, "dialogueName"), text(event, "dialogueConvo"))).toList());
                        }).toList())).toList();
    }

    private List<WorldState.VaultTrader> vaultTraders(JsonNode root) {
        return array(root, "PrimeVaultTraders", "vaultTrader").stream()
                .map(trader -> new WorldState.VaultTrader(text(trader, "Node", "location"),
                        instant(trader, "Activation", "activation"), instant(trader, "Expiry", "expiry"),
                        array(trader, "Manifest", "inventory").stream().map(product ->
                                new WorldState.VaultProduct(text(product, "ItemType", "uniqueName", "item"),
                                        integer(product, "PrimePrice", "ducats"),
                        integer(product, "RegularPrice", "credits"))).toList())).toList();
    }

    private List<WorldState.WeeklyChallenge> weeklyChallenges(JsonNode root) {
        return array(root, "WeeklyChallenges", "weeklyChallenges").stream()
                .map(entry -> new WorldState.WeeklyChallenge(
                        text(entry, "id", "_id"),
                        instant(entry, "activation", "Activation"),
                        instant(entry, "expiry", "Expiry", "expiration"),
                        array(entry, "Challenges", "challenges").stream()
                                .map(challenge -> new WorldState.WeeklyChallengeItem(
                                        text(challenge, "Type", "type"),
                                        integer(challenge, "MinimumEnemyLevel", "minEnemyLevel"),
                                        integer(challenge, "RequiredCount", "requiredAmount"),
                                        integer(challenge, "ProgressIndicatorFreq", "progressAmount"),
                                        text(challenge, "DamageType", "damageType"),
                                        firstString(challenge, "VictimType", "target", "Script._faction")))
                                .toList()))
                .toList();
    }

    private List<WorldState.DarkSector> darkSectors(JsonNode root) {
        return array(root, "BadlandNodes", "darkSectors").stream()
                .map(entry -> {
                    JsonNode defender = first(entry, "DefenderInfo", "defenderInfo");
                    JsonNode mission = first(defender, "MissionInfo", "mission");
                    return new WorldState.DarkSector(
                            text(entry, "id", "_id", "Node", "node"),
                            text(defender, "Name", "defenderName"),
                            text(defender, "Attacker", "attacker"),
                            number(defender, "StrengthRemaining", "defenderPoolRemaining"),
                            number(defender, "MaxStrength", "defenderMaxPool"),
                            integer(defender, "MissionBattlePay", "battlePay"),
                            text(mission, "node", "Node", "location"),
                            instant(defender, "DeploymentActivationTime", "activation"),
                            instant(defender, "DeploymentExpiryTime", "expiry"));
                })
                .toList();
    }

    private List<WorldState.Descent> descents(JsonNode root) {
        return array(root, "Descents", "descents").stream()
                .map(entry -> new WorldState.Descent(
                        integer(entry, "RandSeed", "randSeed", "seed"),
                        array(entry, "Challenges", "challenges").stream()
                                .map(challenge -> new WorldState.DescentChallenge(
                                        integer(challenge, "Index", "index"),
                                        text(challenge, "Type", "type"),
                                        text(challenge, "Challenge", "challenge"),
                                        text(challenge, "Level", "level"),
                                        strings(challenge, "Specs", "specs"),
                                        strings(challenge, "Auras", "auras")))
                                .toList()))
                .toList();
    }

    private List<WorldState.ConclaveChallenge> conclaveChallenges(JsonNode root) {
        List<WorldState.ConclaveChallenge> result = new ArrayList<>();
        for (JsonNode item : array(root, "PVPChallengeInstances", "pvpChallengeInstances",
                "conclaveChallenges", "ConclaveChallenges")) {
            String categoryKey = text(item, "Category", "category");
            String mode = text(item, "PVPMode", "pvpMode", "mode");
            String challengeKey = text(item, "challengeTypeRefID", "challengeTypeRefId",
                    "challengeType", "title");
            JsonNode params = first(item, "params", "Params");
            Integer amount = params != null && params.isArray() && !params.isEmpty()
                    ? integer(params.get(0), "v", "value", "amount") : integer(item, "amount");
            String category = text(item, "categoryName", "categoryText", "category");
            String title = text(item, "title", "name");
            String description = text(item, "description");
            if (StrUtil.isBlank(title)) {
                title = challengeKey;
            }
            boolean daily = "daily".equalsIgnoreCase(categoryKey)
                    || "PVPChallengeTypeCategory_DAILY".equals(categoryKey)
                    || "day".equalsIgnoreCase(categoryKey)
                    || bool(item, "daily");
            boolean rootChallenge = "week_root".equalsIgnoreCase(categoryKey)
                    || "PVPChallengeTypeCategory_WEEKLY_ROOT".equals(categoryKey)
                    || bool(item, "rootChallenge");
            result.add(new WorldState.ConclaveChallenge(
                    text(item, "id", "_id"),
                    mode,
                    category,
                    categoryKey,
                    title,
                    description,
                    amount,
                    integer(item, "standing", "reputation"),
                    daily,
                    rootChallenge,
                    instant(item, "startDate", "activation"),
                    instant(item, "endDate", "expiry", "expiration")));
        }
        return result;
    }

    private List<WorldState.SyndicateMission> syndicateMissions(JsonNode root) {
        List<WorldState.SyndicateMission> result = new ArrayList<>();
        for (JsonNode item : array(root, "syndicateMissions")) {
            result.add(new WorldState.SyndicateMission(text(item, "id", "_id"),
                    text(item, "syndicate", "faction", "tag"), firstString(item, "node", "location", "nodes"),
                    instant(item, "activation"), instant(item, "expiry", "expiration"),
                    syndicateJobs(item), strings(item, "nodes", "node", "location")));
        }
        return result;
    }

    /** 兼容官方 Jobs 对象和社区 API 的已解析赏金，不将对象扁平化成路径字符串。 */
    private List<WorldState.SyndicateJob> syndicateJobs(JsonNode mission) {
        List<WorldState.SyndicateJob> jobs = new ArrayList<>();
        for (JsonNode job : array(mission, "jobs", "missions")) {
            List<Integer> levels = integers(job, "enemyLevels");
            List<WorldState.SyndicateReward> rewards = new ArrayList<>();
            for (JsonNode reward : array(job, "rewardPoolDrops", "rewardPool")) {
                rewards.add(new WorldState.SyndicateReward(
                        reward.isTextual() ? reward.asText() : text(reward, "item", "name", "type"),
                        integer(reward, "count", "itemCount"), number(reward, "chance"),
                        text(reward, "rarity")));
            }
            String jobType = job.isTextual() ? job.asText() : text(job, "jobType");
            String timeBound = text(job, "timeBound");
            Instant expiry = instant(job, "expiry");
            if (expiry == null) {
                expiry = instant(mission, "expiry", "expiration");
                if (jobType != null && jobType.toLowerCase(Locale.ROOT).contains("narmer")) {
                    boolean cetus = jobType.toLowerCase(Locale.ROOT).contains("eidolon");
                    timeBound = cetus ? "白天" : "夜晚";
                    if (cetus && expiry != null) {
                        expiry = expiry.minusSeconds(50 * 60);
                    }
                }
            }
            jobs.add(new WorldState.SyndicateJob(jobType, text(job, "type"),
                    text(job, "rewards", "uniqueName"), rewards,
                    levels.size() >= 2 ? levels.get(0) : integer(job, "minEnemyLevel"),
                    levels.size() >= 2 ? levels.get(1) : integer(job, "maxEnemyLevel"),
                    integers(job, "xpAmounts", "standingStages"),
                    integer(job, "masteryReq", "minMR"), bool(job, "isVault"),
                    text(job, "locationTag"), expiry, timeBound));
        }
        return List.copyOf(jobs);
    }

    private List<Integer> integers(JsonNode root, String... paths) {
        List<Integer> values = new ArrayList<>();
        for (JsonNode value : array(root, paths)) {
            if (value.canConvertToInt()) {
                values.add(value.intValue());
            }
        }
        return List.copyOf(values);
    }

    private List<WorldState.SteelPathOffering> steelPathOfferings(JsonNode root) {
        List<WorldState.SteelPathOffering> result = new ArrayList<>();
        for (JsonNode item : array(root, "steelPathOfferings")) {
            result.add(new WorldState.SteelPathOffering(text(item, "id", "_id"),
                    text(item, "node", "location"), text(item, "reward", "reward.asString"),
                    instant(item, "activation"), instant(item, "expiry", "expiration")));
        }
        JsonNode currentReward = first(root, "steelPath.currentReward");
        if (currentReward.isObject()) {
            result.add(new WorldState.SteelPathOffering(text(currentReward, "id", "_id"),
                    text(root, "steelPath.node", "steelPath.location"),
                    text(currentReward, "name", "item", "reward"),
                    instant(root, "steelPath.activation"), instant(root, "steelPath.expiry")));
        }
        return result;
    }

    private List<WorldState.ActiveMission> activeMissions(JsonNode root) {
        Map<String, WorldState.ActiveMission> result = new LinkedHashMap<>();
        for (RawMission raw : activeMissionNodes(root)) {
            JsonNode item = raw.node();
            WorldState.ActiveMission mission = new WorldState.ActiveMission(text(item, "id", "_id"),
                    text(item, "node", "Node"),
                    text(item, "missionType", "MissionType", "mission.type"),
                    raw.voidStorm()
                            ? text(item, "tier", "modifier", "activeMissionTier", "ActiveMissionTier")
                            : text(item, "tier", "modifier"),
                    instant(item, "activation"), instant(item, "expiry", "expiration"),
                    bool(item, "hard", "isHard", "Hard"),
                    raw.voidStorm() || bool(item, "voidStorm", "voidStorms", "isVoidStorm"));
            result.putIfAbsent(missionKey(mission.id(), mission.node(), mission.missionType(),
                    mission.activation(), mission.expiry(), mission.hard(), mission.voidStorm()), mission);
        }
        return List.copyOf(result.values());
    }

    private List<RawMission> activeMissionNodes(JsonNode root) {
        Map<String, RawMission> result = new LinkedHashMap<>();
        for (JsonNode item : array(root, "activeMissions", "ActiveMissions", "fissures", "Fissures")) {
            RawMission mission = new RawMission(item,
                    bool(item, "voidStorm", "voidStorms", "isVoidStorm"));
            result.putIfAbsent(missionKey(
                    text(item, "id", "_id"), text(item, "node", "Node"),
                    text(item, "missionType", "MissionType", "mission.type"),
                    instant(item, "activation", "Activation"),
                    instant(item, "expiry", "expiration", "Expiry"),
                    bool(item, "hard", "isHard", "Hard"),
                    mission.voidStorm()), mission);
        }
        for (JsonNode item : array(root, "voidStorms", "VoidStorms")) {
            RawMission mission = new RawMission(item, true);
            result.putIfAbsent(missionKey(
                    text(item, "id", "_id"), text(item, "node", "Node"),
                    text(item, "missionType", "MissionType", "mission.type"),
                    instant(item, "activation", "Activation"),
                    instant(item, "expiry", "expiration", "Expiry"),
                    false, true), mission);
        }
        return List.copyOf(result.values());
    }

    private record RawMission(JsonNode node, boolean voidStorm) {
    }

    private List<WorldState.GlobalUpgrade> globalUpgrades(JsonNode root) {
        List<WorldState.GlobalUpgrade> result = new ArrayList<>();
        for (JsonNode item : array(root, "globalUpgrades", "GlobalUpgrades")) {
            result.add(new WorldState.GlobalUpgrade(
                    text(item, "id", "_id"),
                    text(item, "upgradeType", "UpgradeType"),
                    text(item, "operationType", "OperationType"),
                    number(item, "value", "Value"),
                    instant(item, "activation", "Activation"),
                    instant(item, "expiryDate", "ExpiryDate", "expiry", "Expiry")));
        }
        return result;
    }

    private List<WorldState.FlashSale> flashSales(JsonNode root) {
        List<WorldState.FlashSale> result = new ArrayList<>();
        for (JsonNode item : array(root, "flashSales", "FlashSales")) {
            result.add(new WorldState.FlashSale(
                    text(item, "id", "_id"),
                    text(item, "uniqueName", "UniqueName", "typeName", "TypeName", "itemType", "ItemType"),
                    number(item, "discount", "Discount"),
                    integer(item, "regularOverride", "RegularOverride"),
                    integer(item, "premiumOverride", "PremiumOverride"),
                    bool(item, "shownInMarket", "ShowInMarket"),
                    bool(item, "featured", "Featured"),
                    bool(item, "popular", "Popular"),
                    instant(item, "activation", "Activation", "startDate", "StartDate"),
                    instant(item, "expiry", "Expiry", "endDate", "EndDate")));
        }
        return result;
    }

    private List<WorldState.PersistentEnemy> persistentEnemies(JsonNode root) {
        List<WorldState.PersistentEnemy> result = new ArrayList<>();
        for (JsonNode item : array(root, "persistentEnemies", "PersistentEnemies")) {
            result.add(new WorldState.PersistentEnemy(
                    text(item, "id", "_id"),
                    text(item, "agentType", "AgentType"),
                    text(item, "locationTag", "locTag", "LocTag"),
                    integer(item, "rank", "Rank"),
                    number(item, "healthPercent", "HealthPercent"),
                    number(item, "fleeDamage", "FleeDamage"),
                    text(item, "region", "Region"),
                    instant(item, "lastDiscoveredTime", "LastDiscoveredTime"),
                    text(item, "lastDiscoveredLocation", "LastDiscoveredLocation"),
                    bool(item, "discovered", "Discovered"),
                    bool(item, "usingTicketing", "useTicketing", "UseTicketing")));
        }
        return result;
    }

    private List<WorldState.Archimedea> archimedeas(JsonNode root) {
        List<WorldState.Archimedea> result = new ArrayList<>();
        for (JsonNode item : array(root, "Conquests", "conquests", "archimedeas", "Archimedeas")) {
            List<WorldState.ArchimedeaMission> missions = new ArrayList<>();
            for (JsonNode mission : array(item, "Missions", "missions")) {
                JsonNode normal = array(mission, "difficulties", "Difficulties").stream()
                        .findFirst().orElse(null);
                List<String> risks = normal == null
                        ? strings(mission, "risks", "Risks")
                        : strings(normal, "risks", "Risks");
                missions.add(new WorldState.ArchimedeaMission(
                        text(mission, "faction", "Faction"),
                        text(mission, "faction", "Faction"),
                        text(mission, "missionType", "MissionType"),
                        text(mission, "missionType", "MissionType"),
                        normal == null ? text(mission, "deviation", "Deviation")
                                : text(normal, "deviation", "Deviation"),
                        risks));
            }
            String type = text(item, "Type", "type");
            result.add(new WorldState.Archimedea(
                    text(item, "id", "_id"),
                    type, type,
                    missions,
                    strings(item, "Variables", "variables", "personalModifiers"),
                    instant(item, "Activation", "activation"),
                    instant(item, "Expiry", "expiry", "expiration")));
        }
        return List.copyOf(result);
    }

    private String missionKey(String id, String node, String missionType, Instant activation,
                              Instant expiry, boolean hard, boolean voidStorm) {
        String identity = StrUtil.isNotBlank(id)
                ? id : String.join("|", String.valueOf(node), String.valueOf(missionType),
                String.valueOf(activation), String.valueOf(expiry),
                Boolean.toString(hard), Boolean.toString(voidStorm));
        return identity;
    }

    private WorldState.CetusCycle cetusCycle(JsonNode root) {
        JsonNode node = first(root, "cetusCycle");
        return node.isMissingNode() || node.isNull() ? null : new WorldState.CetusCycle(
                bool(node, "isDay", "day"), instant(node, "expiry", "expiration"));
    }

    private WorldState.VallisCycle vallisCycle(JsonNode root) {
        JsonNode node = first(root, "vallisCycle");
        return node.isMissingNode() || node.isNull() ? null : new WorldState.VallisCycle(
                bool(node, "isWarm", "warm"), instant(node, "expiry", "expiration"));
    }

    private WorldState.CambionCycle cambionCycle(JsonNode root) {
        JsonNode node = first(root, "cambionCycle");
        return node.isMissingNode() || node.isNull() ? null : new WorldState.CambionCycle(
                text(node, "active", "state"), instant(node, "expiry", "expiration"));
    }

    private WorldState.EarthCycle earthCycle(JsonNode root) {
        JsonNode node = first(root, "earthCycle");
        return node.isMissingNode() || node.isNull() ? null
                : new WorldState.EarthCycle(bool(node, "isDay", "day"), instant(node, "expiry", "expiration"));
    }

    private WorldState.DuviriCycle duviriCycle(JsonNode root) {
        JsonNode node = first(root, "duviriCycle", "duvalierCycle");
        return node.isMissingNode() || node.isNull() ? null
                : new WorldState.DuviriCycle(text(node, "state", "active", "choice"), instant(node, "expiry", "expiration"));
    }

    private WorldState.ZarimanCycle zarimanCycle(JsonNode root) {
        JsonNode node = first(root, "zarimanCycle");
        return node.isMissingNode() || node.isNull() ? null
                : new WorldState.ZarimanCycle(text(node, "state", "active"), instant(node, "expiry", "expiration"));
    }

    private WorldState.Sortie firstSortie(JsonNode root) {
        return sorties(root).stream().findFirst().orElse(null);
    }

    private WorldState.VoidTrader firstVoidTrader(JsonNode root) {
        return voidTraders(root).stream().findFirst().orElse(null);
    }

    private WorldState.Arbitration arbitration(JsonNode root) {
        JsonNode node = first(root, "arbitration");
        return node.isMissingNode() || node.isNull() ? null
                : new WorldState.Arbitration(text(node, "node", "location"), text(node, "type", "missionType"),
                instant(node, "expiry", "expiration"));
    }

    private List<JsonNode> array(JsonNode root, String... paths) {
        for (String path : paths) {
            JsonNode value = first(root, path);
            if (value.isArray()) {
                List<JsonNode> result = new ArrayList<>();
                value.forEach(result::add);
                if (!result.isEmpty()) {
                    return result;
                }
            }
            if (value.isObject()) {
                return List.of(value);
            }
        }
        return List.of();
    }

    private List<JsonNode> exactArray(JsonNode root, String field) {
        if (ObjectUtil.isNull(root) || !root.isObject()) {
            return List.of();
        }
        JsonNode value = root.get(field);
        if (ObjectUtil.isNull(value) || value.isNull()) {
            return List.of();
        }
        if (value.isArray()) {
            List<JsonNode> result = new ArrayList<>();
            value.forEach(result::add);
            return result;
        }
        return value.isObject() ? List.of(value) : List.of();
    }

    private JsonNode first(JsonNode root, String... paths) {
        if (ObjectUtil.isNull(root)) {
            return MissingNode.getInstance();
        }
        for (String path : paths) {
            JsonNode current = root;
            for (String part : path.split("\\.")) {
                current = childIgnoreCase(current, part);
            }
            if (!current.isMissingNode() && !current.isNull()) {
                return current;
            }
        }
        return MissingNode.getInstance();
    }

    private String text(JsonNode root, String... paths) {
        for (String path : paths) {
            JsonNode value = first(root, path);
            if (value.isValueNode() && !value.isContainerNode()) {
                return value.asText(null);
            }
            JsonNode wrapped = first(value, "$oid", "$numberLong", "value");
            if (wrapped.isValueNode()) {
                return wrapped.asText(null);
            }
        }
        return null;
    }

    private Integer integer(JsonNode root, String... paths) {
        JsonNode value = first(root, paths);
        if (value.isNumber()) {
            return value.asInt();
        }
        return value.isTextual() ? parseInt(value.asText()) : null;
    }

    private int integerOrZero(JsonNode root, String... paths) {
        Integer value = integer(root, paths);
        return ObjectUtil.isNull(value) ? 0 : value;
    }

    private float decimal(JsonNode root, String... paths) {
        JsonNode value = first(root, paths);
        return value.isNumber() ? value.floatValue() : value.isTextual() ? parseFloat(value.asText()) : 0;
    }

    private Double number(JsonNode root, String... paths) {
        JsonNode value = first(root, paths);
        if (value.isNumber()) {
            return value.doubleValue();
        }
        if (value.isTextual()) {
            try {
                return Double.valueOf(value.asText());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private boolean bool(JsonNode root, String... paths) {
        JsonNode value = first(root, paths);
        return value.isBoolean() ? value.booleanValue() : value.isTextual() && Boolean.parseBoolean(value.asText());
    }

    private List<String> strings(JsonNode root, String... paths) {
        for (String path : paths) {
            JsonNode value = first(root, path);
            if (value.isArray()) {
                List<String> result = new ArrayList<>();
                value.forEach(item -> {
                    String text = item.isValueNode() ? item.asText(null)
                            : text(item, "name", "item", "itemType", "uniqueName", "jobType", "rewards");
                    if (StrUtil.isNotBlank(text)) {
                        result.add(text);
                    }
                });
                if (!result.isEmpty()) {
                    return result;
                }
            } else {
                String text = text(root, path);
                    if (StrUtil.isNotBlank(text)) {
                    return List.of(text);
                }
            }
        }
        return List.of();
    }

    private Instant instant(JsonNode root, String... paths) {
        for (String path : paths) {
            Instant parsed = instant(first(root, path));
            if (ObjectUtil.isNotNull(parsed)) {
                return parsed;
            }
        }
        return null;
    }

    private Instant instant(JsonNode value) {
        if (value.isObject()) {
            JsonNode nested = first(value, "$date", "$numberLong", "epochMillis", "value");
            return nested == value || nested.isMissingNode() ? null : instant(nested);
        }
        if (value.isNumber()) {
            long raw = value.asLong();
            return Instant.ofEpochMilli(raw < 10_000_000_000L ? raw * 1000 : raw);
        }
        if (!value.isTextual() || value.asText().isBlank()) {
            return null;
        }
        String text = value.asText();
        try {
            return Instant.parse(text);
        } catch (Exception ignored) {
            try {
                long raw = Long.parseLong(text);
                return raw < 10_000_000_000L ? Instant.ofEpochSecond(raw) : Instant.ofEpochMilli(raw);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    private JsonNode childIgnoreCase(JsonNode parent, String name) {
        JsonNode exact = parent.path(name);
        if (!exact.isMissingNode() || !parent.isObject()) {
            return exact;
        }
        var fields = parent.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            if (field.getKey().equalsIgnoreCase(name)) {
                return field.getValue();
            }
        }
        return MissingNode.getInstance();
    }

    private void validate(WorldState state) {
        boolean hasData = ObjectUtil.isNotNull(state.timestamp())
                || !state.alerts().isEmpty()
                || !state.fissures().isEmpty()
                || !state.invasions().isEmpty()
                || !state.allSorties().isEmpty()
                || !state.archonHunts().isEmpty()
                || !state.allVoidTraders().isEmpty()
                || ObjectUtil.isNotNull(state.cetusCycle())
                || ObjectUtil.isNotNull(state.vallisCycle())
                || ObjectUtil.isNotNull(state.cambionCycle())
                || ObjectUtil.isNotNull(state.earthCycle())
                || ObjectUtil.isNotNull(state.duviriCycle())
                || ObjectUtil.isNotNull(state.zarimanCycle())
                || !state.events().isEmpty()
                || !state.news().isEmpty()
                || !state.dailyDeals().isEmpty()
                || !state.nightwave().isEmpty()
                || !state.syndicateMissions().isEmpty()
                || !state.steelPathOfferings().isEmpty()
                || !state.allActiveMissions().isEmpty()
                || ObjectUtil.isNotNull(state.arbitration())
                || !state.archimedeas().isEmpty()
                || !state.conclaveChallenges().isEmpty()
                || !state.flashSales().isEmpty()
                || !state.persistentEnemies().isEmpty()
                || state.constructionProgress() != null
                || state.simaris() != null
                || state.sentientOutpost() != null
                || !state.clanInitiatives().isEmpty()
                || !state.circuitChoices().isEmpty()
                || !state.calendarSeasons().isEmpty()
                || !state.vaultTraders().isEmpty()
                || !state.weeklyChallenges().isEmpty()
                || !state.darkSectors().isEmpty()
                || !state.descents().isEmpty();
        if (!hasData) {
            throw new IllegalArgumentException("world state response contains no recognized data");
        }
    }

    private String eventTitle(JsonNode item) {
        String direct = text(item, "translations.zh", "translations.zh-hans", "title", "name", "message");
        if (ObjectUtil.isNotNull(direct)) {
            return direct;
        }
        String english = null;
        String fallback = null;
        for (JsonNode message : array(item, "messages")) {
            String value = text(message, "message", "text");
            String language = text(message, "languageCode", "language");
            if (ObjectUtil.isNull(fallback)) {
                fallback = value;
            }
            if (ObjectUtil.isNotNull(language) && (language.equalsIgnoreCase("zh") || language.equalsIgnoreCase("zh-hans"))) {
                return value;
            }
            if (ObjectUtil.isNotNull(language) && language.equalsIgnoreCase("en")) {
                english = value;
            }
        }
        return ObjectUtil.isNull(english) ? fallback : english;
    }

    private String rewardSummary(JsonNode reward, List<WorldState.AlertReward> items) {
        String direct = text(reward, "asString", "description", "string");
        if (ObjectUtil.isNotNull(direct)) {
            return direct;
        }
        int credits = integerOrZero(reward, "credits");
        List<String> parts = items.stream()
                .map(item -> item.item() + (item.count() == null ? "" : " x" + item.count()))
                .toList();
        if (!parts.isEmpty() && credits > 0) {
            return String.join(", ", parts) + ", " + credits + " credits";
        }
        if (!parts.isEmpty()) {
            return String.join(", ", parts);
        }
        return credits > 0 ? credits + " credits" : null;
    }

    private String firstString(JsonNode root, String... paths) {
        for (String path : paths) {
            JsonNode value = first(root, path);
            if (value.isArray() && !value.isEmpty()) {
                String item = text(value.get(0), "value", "name", "node");
                return ObjectUtil.isNull(item) && value.get(0).isValueNode() ? value.get(0).asText(null) : item;
            }
            String item = text(root, path);
            if (ObjectUtil.isNotNull(item)) {
                return item;
            }
        }
        return null;
    }

    private Integer parseInt(String value) {
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private float parseFloat(String value) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String sha256(String content) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(content.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is unavailable", e);
        }
    }
}
