package com.nnm.bot.warframe.runtime.mapper.worldstate;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.ActiveMissionRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.AlertRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.CalendarDayRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.CalendarEventRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.CalendarSeasonRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.ClanInitiativeRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.ClanRewardRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.ConclaveChallengeRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.ConclaveParameterRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.ConquestDifficultyRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.ConquestMissionRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.ConstructionProgressRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.DailyDealRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.DarkSectorDefenderRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.DarkSectorRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.DescentChallengeRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.DescentRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.EndlessXpScheduleRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.FlashSaleRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.GlobalUpgradeRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.InvasionRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.MissionInfoRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.NewsRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.NightwaveChallengeRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.PersistentEnemyRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.RewardRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.SeasonRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.SimarisRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.SortieRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.SteelPathOfferingRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.SyndicateJobRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.SyndicateMissionRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.SyndicateRewardRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.TmpRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.VoidTraderItemRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.VoidTraderRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.WeeklyChallengeItemRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.WeeklyChallengeRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData.WorldEventRawData;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.OfficialWorldStateRawData;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/** 将官方动态数据的类型化输入映射为机器人命令和管理端共用的稳定 DTO。 */
@Slf4j
@Component
public class OfficialWorldStateMapper {

    /** 官方原始 JSON 的 Jackson 映射器。 */
    @Resource
    private ObjectMapper objectMapper;

    /**
     * 从官方 JSON 文本完成类型绑定和稳定 DTO 映射。
     *
     * @param json 待绑定的 JSON 文本。
     * @return 已经解析并校验的世界状态快照。
     */
    public WorldStateDTO map(String json) {
        return map(json, "warframe-official");
    }

    /**
     * 按指定来源从官方 JSON 文本完成类型绑定和稳定 DTO 映射。
     * @param json 待绑定的 JSON 文本。
     * @param source 当前快照的数据源标识，用于采集元数据和排障。
     * @return 已经解析并校验的世界状态快照。
     */
    public WorldStateDTO map(String json, String source) {
        if (StrUtil.isBlank(json)) {
            throw new IllegalArgumentException("world state response must not be blank");
        }
        try {
            OfficialWorldStateRawData rawData = objectMapper.readValue(json, OfficialWorldStateRawData.class);
            return map(rawData, source, json);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("官方世界状态字段绑定失败：" + exception.getMessage(), exception);
        }
    }

    /**
     * 从已绑定对象构造 DTO，并使用原始响应文本生成内容摘要。
     * @param rawData Jackson 绑定得到的原始协议对象。
     * @param source 当前快照的数据源标识，用于采集元数据和排障。
     * @param rawJson 用于审核或排障的原始响应文本。
     * @return 已经解析并校验的世界状态快照。
     */
    public WorldStateDTO map(OfficialWorldStateRawData rawData, String source, String rawJson) {
        if (rawData == null) {
            throw new IllegalArgumentException("world state input must not be null");
        }
        Instant fetchedAt = Instant.now();
        Instant providerTimestamp = epoch(rawData.getTimestamp());
        List<WorldStateDTO.ActiveMissionDTO> activeMissions = activeMissions(rawData);
        List<WorldStateDTO.SortieDTO> sorties = sorties(rawData.getSorties());
        List<WorldStateDTO.VoidTraderDTO> traders = voidTraders(rawData.getVoidTraders());
        WorldStateDTO state = new WorldStateDTO(
                alerts(rawData.getAlerts()),
                fissures(activeMissions),
                invasions(rawData.getInvasions()),
                first(sorties),
                cetusCycle(rawData),
                vallisCycle(rawData),
                cambionCycle(rawData),
                first(traders),
                arbitration(rawData),
                nightwave(rawData.getNightwave()),
                providerTimestamp,
                sorties,
                traders,
                earthCycle(rawData),
                duviriCycle(rawData),
                zarimanCycle(rawData),
                dailyDeals(rawData.getDailyDeals()),
                worldEvents(rawData.getGoals()),
                news(rawData.getNews()),
                syndicateMissions(rawData.getSyndicateMissions()),
                steelPathOfferings(rawData.getSteelPathOfferings()),
                activeMissions,
                sorties(rawData.getArchonHunts()),
                rawData.getBuildLabel(),
                globalUpgrades(rawData.getGlobalUpgrades()),
                flashSales(rawData.getFlashSales()),
                persistentEnemies(rawData.getPersistentEnemies()),
                new WorldStateDTO.WorldStateMetadataDTO(source, fetchedAt, providerTimestamp, false,
                        "nnm-world-state-v1", sha256(Objects.requireNonNull(rawJson, "官方原始响应不能为空"))),
                archimedeas(rawData),
                conclaveChallenges(rawData.getPvpChallengeInstances()),
                constructionProgress(rawData),
                simaris(rawData),
                sentientOutpost(rawData),
                clanInitiatives(rawData.getWeeklyVaultBonusRewards()),
                circuitChoices(rawData.getEndlessXpSchedule()),
                calendarSeasons(rawData.getKnownCalendarSeasons()),
                vaultTraders(rawData.getPrimeVaultTraders()),
                weeklyChallenges(rawData.getWeeklyChallenges()),
                darkSectors(rawData.getDarkSectors()),
                descents(rawData.getDescents()));
        validate(state);
        return state;
    }

    /**
     * 将警报输入及结构化奖励投影为机器人展示所需字段。
     *
     * @param input 待解析或校验的输入。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<WorldStateDTO.AlertDTO> alerts(List<AlertRawData> input) {
        List<WorldStateDTO.AlertDTO> result = new ArrayList<>();
        for (AlertRawData alert : values(input)) {
            MissionInfoRawData mission = alert.getMissionInfo();
            RewardRawData reward = mission == null ? null : mission.getMissionReward();
            List<WorldStateDTO.AlertRewardDTO> rewards = alertRewards(reward);
            result.add(new WorldStateDTO.AlertDTO(
                    alert.getId(), mission == null ? null : mission.getMissionType(), rewardSummary(reward, rewards),
                    alert.getExpiry(), alert.getActivation(), mission == null ? null : mission.getLocation(),
                    mission == null ? null : mission.getFaction(), mission == null ? null : mission.getMinEnemyLevel(),
                    mission == null ? null : mission.getMaxEnemyLevel(), rewards));
        }
        return List.copyOf(result);
    }

    /**
     * 将普通任务、裂缝和虚空风暴统一为可执行动态任务。
     *
     * @param input 待解析或校验的输入。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<WorldStateDTO.ActiveMissionDTO> activeMissions(OfficialWorldStateRawData input) {
        Map<String, WorldStateDTO.ActiveMissionDTO> unique = new LinkedHashMap<>();
        addMissions(unique, input.getActiveMissions(), false);
        addMissions(unique, input.getFissures(), false);
        addMissions(unique, input.getVoidStorms(), true);
        return List.copyOf(unique.values());
    }

    /**
     * 合并一个官方任务分区并按稳定标识去重。
     *
     * @param target 本次操作的目标对象或路径。
     * @param input 待解析或校验的输入。
     * @param stormSection 风暴分区。
     */
    private void addMissions(Map<String, WorldStateDTO.ActiveMissionDTO> target,
                             List<ActiveMissionRawData> input, boolean stormSection) {
        for (ActiveMissionRawData raw : values(input)) {
            MissionInfoRawData mission = raw.getMissionInfo();
            String node = firstNonNull(raw.getNode(), mission == null ? null : mission.getLocation());
            String type = firstNonNull(raw.getMissionType(), mission == null ? null : mission.getMissionType());
            boolean storm = stormSection || Boolean.TRUE.equals(raw.getVoidStorm());
            WorldStateDTO.ActiveMissionDTO value = new WorldStateDTO.ActiveMissionDTO(
                    raw.getId(), node, type, raw.getTier(), raw.getActivation(), raw.getExpiry(),
                    Boolean.TRUE.equals(raw.getHard()), storm);
            String key = raw.getId() == null
                    ? String.join("|", Objects.toString(node, ""), Objects.toString(type, ""),
                    Objects.toString(raw.getActivation(), ""), Objects.toString(raw.getExpiry(), ""),
                    Boolean.toString(value.hard()), Boolean.toString(storm))
                    : raw.getId();
            target.putIfAbsent(key, value);
        }
    }

    /**
     * 从动态任务投影中选出具有裂缝等级或风暴标记的任务。
     *
     * @param missions 任务集合。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<WorldStateDTO.FissureDTO> fissures(List<WorldStateDTO.ActiveMissionDTO> missions) {
        return missions.stream()
                .filter(mission -> mission.tier() != null || mission.voidStorm())
                .map(mission -> new WorldStateDTO.FissureDTO(mission.id(), mission.node(), mission.tier(),
                        mission.missionType(), mission.expiry(), mission.activation(), mission.hard(),
                        mission.voidStorm()))
                .toList();
    }

    /**
     * 映射入侵双方奖励，并按官方 Count/Goal 计算进度。
     *
     * @param input 待解析或校验的输入。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<WorldStateDTO.InvasionDTO> invasions(List<InvasionRawData> input) {
        List<WorldStateDTO.InvasionDTO> result = new ArrayList<>();
        for (InvasionRawData invasion : values(input)) {
            List<WorldStateDTO.InvasionRewardDTO> attacker = invasionRewards(invasion.getAttackerReward());
            List<WorldStateDTO.InvasionRewardDTO> defender = invasionRewards(invasion.getDefenderReward());
            Double completion = invasion.getCompletion();
            if (completion == null) {
                completion = invasionCompletion(invasion);
            }
            result.add(new WorldStateDTO.InvasionDTO(invasion.getId(), invasion.getNode(),
                    invasionRewardSummary(invasion.getAttackerReward(), attacker),
                    invasionRewardSummary(invasion.getDefenderReward(), defender), completion.floatValue(),
                    invasion.getActivation(), Boolean.TRUE.equals(invasion.getCompleted()), attacker, defender));
        }
        return List.copyOf(result);
    }

    /**
     * 根据官方入侵净争夺数和阵营规则推导完成百分比。
     *
     * @param invasion 入侵。
     * @return 当前操作计算或读取的数值。
     */
    private double invasionCompletion(InvasionRawData invasion) {
        Double count = invasion.getCount();
        Double goal = invasion.getGoal();
        if (count == null || goal == null || !Double.isFinite(count) || !Double.isFinite(goal) || goal <= 0) {
            return 0;
        }
        MissionInfoRawData defender = invasion.getDefenderMissionInfo();
        String faction = defender == null ? "" : Objects.toString(defender.getFaction(), "");
        double factor = faction.toLowerCase(Locale.ROOT).contains("infest") ? 100 : 50;
        return Math.clamp((1 + count / goal) * factor, 0, 100);
    }

    /**
     * 映射普通突击或执政官狩猎任务。
     *
     * @param input 待解析或校验的输入。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<WorldStateDTO.SortieDTO> sorties(List<SortieRawData> input) {
        return values(input).stream().map(this::sortie).toList();
    }

    /**
     * 映射单个突击，包括变体任务和结束时间。
     *
     * @param raw 原始。
     * @return 突击对象。
     */
    private WorldStateDTO.SortieDTO sortie(SortieRawData raw) {
        List<WorldStateDTO.SortieVariantDTO> variants = values(raw.getVariants()).stream()
                .map(variant -> new WorldStateDTO.SortieVariantDTO(
                        variant.getMissionType(), variant.getModifier(), variant.getNode()))
                .toList();
        return new WorldStateDTO.SortieDTO(raw.getId(), raw.getBoss(), variants, raw.getExpiry());
    }

    /**
     * 映射虚空奸商状态与库存。
     *
     * @param input 待解析或校验的输入。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<WorldStateDTO.VoidTraderDTO> voidTraders(List<VoidTraderRawData> input) {
        Instant now = Instant.now();
        return values(input).stream().map(trader -> {
            List<VoidTraderItemRawData> inventory = values(trader.getInventory());
            if (CollUtil.isEmpty(inventory)) {
                inventory = values(trader.getManifest());
            }
            List<WorldStateDTO.VoidTraderItemDTO> items = inventory.stream()
                    .map(item -> new WorldStateDTO.VoidTraderItemDTO(item.getItem(), integer(item.getDucats()),
                            integer(item.getCredits())))
                    .toList();
            Instant start = trader.getActivation();
            Instant end = trader.getExpiry();
            boolean active = !Boolean.TRUE.equals(trader.getCompleted()) && start != null && end != null
                    && !now.isBefore(start) && now.isBefore(end);
            return new WorldStateDTO.VoidTraderDTO(trader.getLocation(), start, end, active, items);
        }).toList();
    }

    /**
     * 映射夜波挑战，并在官方未提供声望时按挑战类别补齐默认值。
     *
     * @param season 赛季。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<WorldStateDTO.NightwaveMissionDTO> nightwave(SeasonRawData season) {
        if (season == null) {
            return List.of();
        }
        return values(season.getMissions()).stream().map(this::nightwaveMission).toList();
    }

    /**
     * 生成单项夜波挑战展示字段。
     *
     * @param raw 原始。
     * @return 夜波任务对象。
     */
    private WorldStateDTO.NightwaveMissionDTO nightwaveMission(NightwaveChallengeRawData raw) {
        String challenge = Objects.toString(raw.getChallenge(), "");
        boolean daily = Boolean.TRUE.equals(raw.getIsDaily());
        boolean elite = challenge.toLowerCase(Locale.ROOT).contains("hard");
        int standing = raw.getReputation() == null ? 1000 + (daily ? 0 : 3500) + (elite ? 2500 : 0)
                : raw.getReputation();
        return new WorldStateDTO.NightwaveMissionDTO(raw.getId(), raw.getChallenge(), raw.getTitle(), raw.getDesc(),
                standing, daily);
    }

    /**
     * 映射每日特惠物品、库存、价格和时间。
     *
     * @param input 待解析或校验的输入。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<WorldStateDTO.DailyDealDTO> dailyDeals(List<DailyDealRawData> input) {
        return values(input).stream().map(deal -> new WorldStateDTO.DailyDealDTO(deal.getId(), deal.getItem(),
                integer(deal.getStock()), integer(deal.getSold()), integer(deal.getCredits()),
                integer(deal.getPlatinum()), deal.getActivation(), deal.getExpiry())).toList();
    }

    /**
     * 映射官方 Goals 活动及奖励。
     *
     * @param input 待解析或校验的输入。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<WorldStateDTO.WorldEventDTO> worldEvents(List<WorldEventRawData> input) {
        return values(input).stream().map(event -> {
            List<WorldStateDTO.WorldEventRewardDTO> rewards = eventRewards(event.getReward());
            return new WorldStateDTO.WorldEventDTO(event.getId(), event.getDescription(), event.getTooltip(),
                    event.getActivation(), event.getExpiry(), event.getCategory(), event.getNode(),
                    event.getCurrentScore(), event.getMaximumScore(), rewards,
                    event.getReward() == null ? null : event.getReward().getCredits());
        }).toList();
    }

    /**
     * 映射官方公告并优先选取中文标题，其次英文标题。
     *
     * @param input 待解析或校验的输入。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<WorldStateDTO.NewsItemDTO> news(List<NewsRawData> input) {
        return values(input).stream().map(item -> new WorldStateDTO.NewsItemDTO(item.getId(),
                localizedTitle(item), item.getLink(), item.getImageLink(), item.getActivation(), item.getExpiry(),
                Boolean.TRUE.equals(item.getPriority()))).toList();
    }

    /**
     * 从官方多语言消息中选择可读标题。
     *
     * @param item 当前物品或集合成员。
     * @return 本地化标题对应的文本。
     */
    private String localizedTitle(NewsRawData item) {
        var translations = item.getTranslations();
        if (translations != null && translations.getChinese() != null) {
            return translations.getChinese();
        }
        if (item.getTitle() != null) {
            return item.getTitle();
        }
        String english = translations == null ? null : translations.getEnglish();
        String fallback = null;
        for (var message : values(item.getMessages())) {
            if (fallback == null) {
                fallback = message.getMessage();
            }
            String language = Objects.toString(message.getLanguageCode(), "");
            if (language.equalsIgnoreCase("zh") || language.equalsIgnoreCase("zh-hans")) {
                return message.getMessage();
            }
            if (language.equalsIgnoreCase("en")) {
                english = message.getMessage();
            }
        }
        return firstNonNull(english, fallback);
    }

    /**
     * 映射官方辛迪加任务及赏金基础参数。
     *
     * @param input 待解析或校验的输入。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<WorldStateDTO.SyndicateMissionDTO> syndicateMissions(List<SyndicateMissionRawData> input) {
        return values(input).stream().map(mission -> new WorldStateDTO.SyndicateMissionDTO(
                mission.getId(), mission.getTag(), first(values(mission.getNodes())), mission.getActivation(),
                mission.getExpiry(), values(mission.getJobs()).stream().map(job -> syndicateJob(job, mission)).toList(),
                values(mission.getNodes()), List.of(), List.of(), "OFFICIAL_ONLY")).toList();
    }

    /**
     * 映射官方或自维护的单个赏金任务。
     *
     * @param job 任务。
     * @param mission 任务。
     * @return 集团任务对象。
     */
    private WorldStateDTO.SyndicateJobDTO syndicateJob(
            SyndicateJobRawData job, SyndicateMissionRawData mission) {
        List<WorldStateDTO.SyndicateRewardDTO> rewards = values(job.getRewardPoolDrops()).stream()
                .map(this::syndicateReward).toList();
        String reference = String.join(", ", values(job.getRewards()));
        List<String> pool = values(job.getRewardPool());
        if (StrUtil.isBlank(reference) && CollUtil.isNotEmpty(pool)) {
            reference = String.join(", ", pool);
        }
        Integer min = job.getMinEnemyLevel();
        Integer max = job.getMaxEnemyLevel();
        if (job.getEnemyLevels() != null && job.getEnemyLevels().size() >= 2) {
            min = job.getEnemyLevels().getFirst();
            max = job.getEnemyLevels().getLast();
        }
        String type = firstNonNull(job.getType(), job.getJobType());
        return new WorldStateDTO.SyndicateJobDTO(job.getJobType(), type, reference, rewards, min, max,
                values(job.getXpAmounts()), job.getMasteryReq(), Boolean.TRUE.equals(job.getIsVault()),
                job.getLocationTag(), mission.getExpiry(), null);
    }

    /**
     * 映射人工维护赏金中的结构化掉落。
     *
     * @param reward 奖励。
     * @return 集团奖励对象。
     */
    private WorldStateDTO.SyndicateRewardDTO syndicateReward(SyndicateRewardRawData reward) {
        return new WorldStateDTO.SyndicateRewardDTO(
                reward.getItem(), reward.getCount(), reward.getChance(), reward.getRarity());
    }

    /**
     * 映射钢铁之路商店奖励。
     *
     * @param input 待解析或校验的输入。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<WorldStateDTO.SteelPathOfferingDTO> steelPathOfferings(
            List<SteelPathOfferingRawData> input) {
        return values(input).stream().map(item -> new WorldStateDTO.SteelPathOfferingDTO(item.getId(), null,
                item.getItem(), item.getActivation(), item.getExpiry())).toList();
    }

    /**
     * 映射官方全局增益。
     *
     * @param input 待解析或校验的输入。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<WorldStateDTO.GlobalUpgradeDTO> globalUpgrades(List<GlobalUpgradeRawData> input) {
        return values(input).stream().map(item -> new WorldStateDTO.GlobalUpgradeDTO(item.getId(),
                item.getUpgradeType(), item.getOperationType(), item.getValue(), item.getActivation(),
                item.getExpiry())).toList();
    }

    /**
     * 映射商城闪购。
     *
     * @param input 待解析或校验的输入。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<WorldStateDTO.FlashSaleDTO> flashSales(List<FlashSaleRawData> input) {
        return values(input).stream().map(item -> new WorldStateDTO.FlashSaleDTO(item.getId(), item.getItem(),
                item.getDiscount(), item.getCredits(), item.getPlatinum(), Boolean.TRUE.equals(item.getShownInMarket()),
                Boolean.TRUE.equals(item.getFeatured()), Boolean.TRUE.equals(item.getPopular()), item.getActivation(),
                item.getExpiry())).toList();
    }

    /**
     * 映射持续追猎敌人状态。
     *
     * @param input 待解析或校验的输入。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<WorldStateDTO.PersistentEnemyDTO> persistentEnemies(List<PersistentEnemyRawData> input) {
        return values(input).stream().map(item -> new WorldStateDTO.PersistentEnemyDTO(item.getId(),
                item.getAgentType(), item.getLocationTag(), item.getRank(), item.getHealthPercent(),
                item.getFleeDamage(), item.getRegion(), item.getLastDiscoveredTime(),
                item.getLastDiscoveredLocation(), Boolean.TRUE.equals(item.getDiscovered()),
                Boolean.TRUE.equals(item.getUsingTicketing()))).toList();
    }

    /**
     * 映射 Conquests 中的 Archimedea 任务、难度和风险条件。
     *
     * @param input 待解析或校验的输入。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<WorldStateDTO.ArchimedeaDTO> archimedeas(OfficialWorldStateRawData input) {
        return values(input.getConquests()).stream().map(conquest -> {
            List<WorldStateDTO.ArchimedeaMissionDTO> missions = values(conquest.getMissions()).stream()
                    .map(this::archimedeaMission).toList();
            return new WorldStateDTO.ArchimedeaDTO(conquest.getId(), conquest.getType(), conquest.getType(),
                    missions, values(conquest.getVariables()), conquest.getActivation(), conquest.getExpiry());
        }).toList();
    }

    /**
     * 选择普通难度配置并映射 Archimedea 的任务规则。
     *
     * @param mission 任务。
     * @return 科研任务对象。
     */
    private WorldStateDTO.ArchimedeaMissionDTO archimedeaMission(ConquestMissionRawData mission) {
        ConquestDifficultyRawData selected = values(mission.getDifficulties()).stream()
                .filter(difficulty -> "CD_NORMAL".equalsIgnoreCase(difficulty.getType()))
                .findFirst().orElse(first(values(mission.getDifficulties())));
        return new WorldStateDTO.ArchimedeaMissionDTO(mission.getFaction(), mission.getFaction(),
                mission.getMissionType(), mission.getMissionType(), selected == null ? mission.getDeviation() : selected.getDeviation(),
                selected == null ? values(mission.getRisks()) : values(selected.getRisks()));
    }

    /**
     * 映射 Conclave 动态挑战和数量参数。
     *
     * @param input 待解析或校验的输入。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<WorldStateDTO.ConclaveChallengeDTO> conclaveChallenges(List<ConclaveChallengeRawData> input) {
        return values(input).stream().map(challenge -> {
            Integer amount = values(challenge.getParams()).stream()
                    .map(ConclaveParameterRawData::getValue)
                    .filter(Objects::nonNull).findFirst().orElse(challenge.getRequiredCount());
            String category = challenge.getCategory();
            return new WorldStateDTO.ConclaveChallengeDTO(challenge.getId(), challenge.getMode(), category,
                    category, challenge.getTitle(), null, amount, null,
                    category != null && category.toUpperCase(Locale.ROOT).contains("DAILY"),
                    category != null && category.endsWith("_ROOT"), challenge.getActivation(), challenge.getExpiry());
        }).toList();
    }

    /**
     * 将三段式官方建造进度投影为具名字段。
     *
     * @param input 待解析或校验的输入。
     * @return 建设进度对象。
     */
    private WorldStateDTO.ConstructionProgressDTO constructionProgress(OfficialWorldStateRawData input) {
        List<Double> values = values(input.getProjectPct());
        if (CollUtil.isNotEmpty(values)) {
            return new WorldStateDTO.ConstructionProgressDTO(at(values, 0), at(values, 1), at(values, 2));
        }
        ConstructionProgressRawData progress = input.getConstructionProgress();
        return progress == null ? null : new WorldStateDTO.ConstructionProgressDTO(
                progress.getFomorianProgress(), progress.getRazorbackProgress(), progress.getUnknownProgress());
    }

    /**
     * 从静态 Sanctuary 信息或精简状态构造 Simaris 当前状态。
     *
     * @param input 待解析或校验的输入。
     * @return Simaris对象。
     */
    private WorldStateDTO.SimarisDTO simaris(OfficialWorldStateRawData input) {
        SimarisRawData simaris = input.getSimaris();
        if (simaris != null) {
            return new WorldStateDTO.SimarisDTO(simaris.getTarget(), Boolean.TRUE.equals(simaris.getTargetActive()));
        }
        var library = input.getLibraryInfo();
        if (library == null) {
            return null;
        }
        String target = library.getLastCompletedTargetType();
        return new WorldStateDTO.SimarisDTO(target, StrUtil.isBlank(target));
    }

    /**
     * 解析 Tmp 字符串中的 Sentient 前哨节点，不从 JSON 树手工读取字段。
     *
     * @param input 待解析或校验的输入。
     * @return Sentient前哨站对象。
     */
    private WorldStateDTO.SentientOutpostDTO sentientOutpost(OfficialWorldStateRawData input) {
        var outpost = input.getSentientOutposts();
        if (outpost != null && outpost.getNode() != null) {
            return new WorldStateDTO.SentientOutpostDTO(outpost.getNode(), outpost.getActivation(), outpost.getExpiry());
        }
        if (StrUtil.isBlank(input.getTmp())) {
            return null;
        }
        try {
            TmpRawData tmp = objectMapper.readValue(input.getTmp(), TmpRawData.class);
            Integer node = tmp.getSentientFleetNode();
            return node == null || node <= 0 ? null
                    : new WorldStateDTO.SentientOutpostDTO("CrewBattleNode" + node, null, null);
        } catch (JsonProcessingException exception) {
            // Tmp 是官方临时扩展，单独损坏时不丢弃其他有效分区；原文仍在快照中。
            log.warn("官方 Tmp 扩展无法绑定为前哨状态：{}", exception.getOriginalMessage());
            return null;
        }
    }

    /**
     * 映射氏族每周行动及其奖励阈值。
     *
     * @param input 待解析或校验的输入。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<WorldStateDTO.ClanInitiativeDTO> clanInitiatives(List<ClanInitiativeRawData> input) {
        return values(input).stream().map(initiative -> new WorldStateDTO.ClanInitiativeDTO(
                initiative.getWeekCount(), initiative.getBonusRegion(), values(initiative.getRewards()).stream()
                .map(this::clanReward).toList())).toList();
    }

    /**
     * 映射单项氏族周常奖励。
     *
     * @param reward 奖励。
     * @return 氏族奖励对象。
     */
    private WorldStateDTO.ClanRewardDTO clanReward(ClanRewardRawData reward) {
        return new WorldStateDTO.ClanRewardDTO(reward.getReward(), reward.getItemCount(), reward.getPointThreshold(),
                reward.getRewardClaimed());
    }

    /**
     * 映射无尽任务经验计划中的 Circuit 奖励选择。
     *
     * @param input 待解析或校验的输入。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<WorldStateDTO.CircuitChoiceDTO> circuitChoices(List<EndlessXpScheduleRawData> input) {
        return values(input).stream().limit(1).flatMap(schedule -> values(schedule.getCategoryChoices()).stream())
                .map(choice -> new WorldStateDTO.CircuitChoiceDTO(choice.getCategory(), values(choice.getChoices())))
                .toList();
    }

    /**
     * 映射 1999 日历赛季、日期和活动。
     *
     * @param input 待解析或校验的输入。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<WorldStateDTO.CalendarSeasonDTO> calendarSeasons(List<CalendarSeasonRawData> input) {
        return values(input).stream().map(season -> new WorldStateDTO.CalendarSeasonDTO(season.getSeason(),
                season.getYearIteration(), values(season.getDays()).stream().map(this::calendarDay).toList())).toList();
    }

    /**
     * 映射单个日历日及当天活动。
     *
     * @param day 日。
     * @return 日历日对象。
     */
    private WorldStateDTO.CalendarDayDTO calendarDay(CalendarDayRawData day) {
        Integer index = day.getDay();
        String date = index != null && index >= 1 && index <= 365
                ? LocalDate.ofYearDay(1999, index).toString() : day.getDate();
        return new WorldStateDTO.CalendarDayDTO(date,
                values(day.getEvents()).stream().map(this::calendarEvent).toList());
    }

    /**
     * 映射单项日历挑战、奖励或增益。
     *
     * @param event 上游日历活动的类型化原始对象。
     * @return 日历事件对象。
     */
    private WorldStateDTO.CalendarEventDTO calendarEvent(CalendarEventRawData event) {
        return new WorldStateDTO.CalendarEventDTO(event.getType(),
                firstNonNull(event.getChallenge(), event.getUpgrade()), event.getDescription(), event.getUniqueName(),
                event.getDialogueName(), event.getDialogueConvo());
    }

    /**
     * 映射 Prime Vault 商人及其独立币种价格。
     *
     * @param input 待解析或校验的输入。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<WorldStateDTO.VaultTraderDTO> vaultTraders(List<VoidTraderRawData> input) {
        return values(input).stream().map(trader -> {
            List<VoidTraderItemRawData> inventory = values(trader.getManifest());
            if (CollUtil.isEmpty(inventory)) {
                inventory = values(trader.getInventory());
            }
            List<WorldStateDTO.VaultProductDTO> products = inventory.stream()
                    .map(item -> new WorldStateDTO.VaultProductDTO(item.getItem(), item.getDucats(), item.getCredits()))
                    .toList();
            return new WorldStateDTO.VaultTraderDTO(trader.getLocation(), trader.getActivation(), trader.getExpiry(),
                    products);
        }).toList();
    }

    /**
     * 映射每周挑战及官方目标字段。
     *
     * @param input 待解析或校验的输入。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<WorldStateDTO.WeeklyChallengeDTO> weeklyChallenges(List<WeeklyChallengeRawData> input) {
        return values(input).stream().map(challenge -> new WorldStateDTO.WeeklyChallengeDTO(challenge.getId(),
                challenge.getActivation(), challenge.getExpiry(), values(challenge.getChallenges()).stream()
                .map(this::weeklyChallengeItem).toList())).toList();
    }

    /**
     * 映射单项每周挑战目标。
     *
     * @param item 当前物品或集合成员。
     * @return 每周挑战物品对象。
     */
    private WorldStateDTO.WeeklyChallengeItemDTO weeklyChallengeItem(WeeklyChallengeItemRawData item) {
        return new WorldStateDTO.WeeklyChallengeItemDTO(item.getType(), item.getMinimumEnemyLevel(),
                item.getRequiredCount(), item.getProgressIndicatorFreq(), item.getDamageType(), firstNonNull(item.getVictimType(),
                        item.getScript() == null ? null : item.getScript().getFaction()));
    }

    /**
     * 映射黑暗地带控制方和部署任务。
     *
     * @param input 待解析或校验的输入。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<WorldStateDTO.DarkSectorDTO> darkSectors(List<DarkSectorRawData> input) {
        return values(input).stream().map(this::darkSector).toList();
    }

    /**
     * 映射单个黑暗地带状态。
     *
     * @param raw 原始。
     * @return 黑暗黑暗地带对象。
     */
    private WorldStateDTO.DarkSectorDTO darkSector(DarkSectorRawData raw) {
        DarkSectorDefenderRawData defender = raw.getDefenderInfo();
        MissionInfoRawData mission = defender == null ? null : defender.getMissionInfo();
        return new WorldStateDTO.DarkSectorDTO(raw.getId(), defender == null ? null : defender.getName(),
                defender == null ? null : defender.getAttacker(),
                defender == null ? null : defender.getStrengthRemaining(), defender == null ? null : defender.getMaxStrength(),
                defender == null ? null : defender.getMissionBattlePay(), mission == null ? null : mission.getLocation(),
                defender == null ? null : defender.getDeploymentActivationTime(),
                defender == null ? null : defender.getDeploymentExpiryTime());
    }

    /**
     * 映射深渊之塔轮换和挑战条件。
     *
     * @param input 待解析或校验的输入。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<WorldStateDTO.DescentDTO> descents(List<DescentRawData> input) {
        return values(input).stream().map(descent -> new WorldStateDTO.DescentDTO(
                descent.getRandSeed(),
                values(descent.getChallenges()).stream().map(this::descentChallenge).toList())).toList();
    }

    /**
     * 映射深渊之塔中的单个挑战。
     *
     * @param challenge 挑战。
     * @return 恶堕挑战对象。
     */
    private WorldStateDTO.DescentChallengeDTO descentChallenge(DescentChallengeRawData challenge) {
        return new WorldStateDTO.DescentChallengeDTO(challenge.getIndex(), challenge.getType(),
                challenge.getChallenge(), challenge.getLevel(), values(challenge.getSpecs()),
                values(challenge.getAuras()));
    }

    /**
     * 从已绑定的官方协议字段构造希图斯昼夜周期；未提供的动态状态保持缺省。
     * @param input 待解析或校验的输入。
     * @return 希图斯周期对象。
     */
    private WorldStateDTO.CetusCycleDTO cetusCycle(OfficialWorldStateRawData input) {
        var raw = input.getCetusCycle();
        return raw == null ? null : new WorldStateDTO.CetusCycleDTO(Boolean.TRUE.equals(raw.getIsDay()), raw.getExpiry());
    }

    /**
     * 从已绑定的官方协议字段构造奥布山谷冷热周期；未提供的动态状态保持缺省。
     * @param input 待解析或校验的输入。
     * @return 奥布山谷周期对象。
     */
    private WorldStateDTO.VallisCycleDTO vallisCycle(OfficialWorldStateRawData input) {
        var raw = input.getVallisCycle();
        return raw == null ? null : new WorldStateDTO.VallisCycleDTO(Boolean.TRUE.equals(raw.getIsWarm()), raw.getExpiry());
    }

    /**
     * 从已绑定的官方协议字段构造魔胎之境双蠕虫周期；未提供的动态状态保持缺省。
     * @param input 待解析或校验的输入。
     * @return 魔胎之境周期对象。
     */
    private WorldStateDTO.CambionCycleDTO cambionCycle(OfficialWorldStateRawData input) {
        var raw = input.getCambionCycle();
        return raw == null ? null : new WorldStateDTO.CambionCycleDTO(raw.getState(), raw.getExpiry());
    }

    /**
     * 从已绑定的官方协议字段构造地球昼夜周期；未提供的动态状态保持缺省。
     * @param input 待解析或校验的输入。
     * @return 地球周期对象。
     */
    private WorldStateDTO.EarthCycleDTO earthCycle(OfficialWorldStateRawData input) {
        var raw = input.getEarthCycle();
        return raw == null ? null : new WorldStateDTO.EarthCycleDTO(Boolean.TRUE.equals(raw.getIsDay()), raw.getExpiry());
    }

    /**
     * 从已绑定的官方协议字段构造双衍王境情绪周期；未提供的动态状态保持缺省。
     * @param input 待解析或校验的输入。
     * @return 双衍王境周期对象。
     */
    private WorldStateDTO.DuviriCycleDTO duviriCycle(OfficialWorldStateRawData input) {
        var raw = input.getDuviriCycle();
        return raw == null ? null : new WorldStateDTO.DuviriCycleDTO(raw.getState(), raw.getExpiry());
    }

    /**
     * 从已绑定的官方协议字段构造扎里曼阵营周期；未提供的动态状态保持缺省。
     * @param input 待解析或校验的输入。
     * @return 扎里曼周期对象。
     */
    private WorldStateDTO.ZarimanCycleDTO zarimanCycle(OfficialWorldStateRawData input) {
        var raw = input.getZarimanCycle();
        return raw == null ? null : new WorldStateDTO.ZarimanCycleDTO(raw.getState(), raw.getExpiry());
    }

    /**
     * 构造当前仲裁任务。
     *
     * @param input 待解析或校验的输入。
     * @return 仲裁对象。
     */
    private WorldStateDTO.ArbitrationDTO arbitration(OfficialWorldStateRawData input) {
        var raw = input.getArbitration();
        return raw == null ? null : new WorldStateDTO.ArbitrationDTO(raw.getNode(), raw.getType(), raw.getExpiry());
    }

    /**
     * 将奖励对象展开为物品和数量列表。
     *
     * @param reward 奖励。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<WorldStateDTO.AlertRewardDTO> alertRewards(RewardRawData reward) {
        if (reward == null) {
            return List.of();
        }
        List<WorldStateDTO.AlertRewardDTO> result = new ArrayList<>();
        values(reward.getItems()).forEach(item -> result.add(new WorldStateDTO.AlertRewardDTO(item, null)));
        values(reward.getCountedItems()).forEach(item -> result.add(
                new WorldStateDTO.AlertRewardDTO(item.getItemType(), item.getItemCount())));
        return List.copyOf(result);
    }

    /**
     * 将活动奖励对象展开为物品和数量列表。
     *
     * @param reward 奖励。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<WorldStateDTO.WorldEventRewardDTO> eventRewards(RewardRawData reward) {
        return alertRewards(reward).stream()
                .map(item -> new WorldStateDTO.WorldEventRewardDTO(item.item(), item.count())).toList();
    }

    /**
     * 将入侵奖励对象展开为物品和数量列表。
     *
     * @param reward 奖励。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private List<WorldStateDTO.InvasionRewardDTO> invasionRewards(RewardRawData reward) {
        return alertRewards(reward).stream()
                .map(item -> new WorldStateDTO.InvasionRewardDTO(item.item(), item.count())).toList();
    }

    /**
     * 生成警报奖励摘要，保留官方提供的自由文本摘要。
     *
     * @param reward 奖励。
     * @param items 当前物品或成员列表。
     * @return 奖励摘要对应的文本。
     */
    private String rewardSummary(RewardRawData reward, List<WorldStateDTO.AlertRewardDTO> items) {
        if (reward == null) {
            return "";
        }
        if (StrUtil.isNotBlank(reward.getAsString())) {
            return reward.getAsString();
        }
        List<String> parts = new ArrayList<>();
        items.forEach(item -> parts.add(item.item() + (item.count() == null ? "" : " x" + item.count())));
        if (integer(reward.getCredits()) > 0) {
            parts.add(reward.getCredits() + " credits");
        }
        return String.join(", ", parts);
    }

    /**
     * 生成入侵奖励摘要。
     *
     * @param reward 奖励。
     * @param items 当前物品或成员列表。
     * @return 入侵奖励摘要对应的文本。
     */
    private String invasionRewardSummary(RewardRawData reward, List<WorldStateDTO.InvasionRewardDTO> items) {
        if (reward == null) {
            return "";
        }
        if (StrUtil.isNotBlank(reward.getAsString())) {
            return reward.getAsString();
        }
        List<String> parts = new ArrayList<>();
        items.forEach(item -> parts.add(item.item() + (item.count() == null ? "" : " x" + item.count())));
        if (integer(reward.getCredits()) > 0) {
            parts.add(reward.getCredits() + " credits");
        }
        return String.join(", ", parts);
    }

    /**
     * 把可空数字统一映射为命令展示使用的零值。
     *
     * @param value 待处理的字段值。
     * @return 当前操作计算或读取的数值。
     */
    private int integer(Integer value) {
        return value == null ? 0 : value;
    }

    /**
     * 将上游 Unix 秒或毫秒时间转换为 Instant。
     *
     * @param value 待处理的字段值。
     * @return 时间点对象。
     */
    private Instant epoch(Long value) {
        if (value == null) {
            return null;
        }
        return Math.abs(value) < 10_000_000_000L ? Instant.ofEpochSecond(value) : Instant.ofEpochMilli(value);
    }

    /**
     * 计算动态响应的 SHA-256 摘要。
     *
     * @param value 待校验或规范化的文本。
     * @return 输入内容的 SHA-256 十六进制摘要。
     */
    private String sha256(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("cannot calculate world state digest", exception);
        }
    }

    /**
     * 确认至少有一个本应用识别的官方分区。
     *
     * @param state 已经解析的当前世界状态。
     */
    private void validate(WorldStateDTO state) {
        boolean hasData = state.timestamp() != null || state.cetusCycle() != null || state.vallisCycle() != null
                || state.cambionCycle() != null || state.earthCycle() != null || state.duviriCycle() != null
                || state.zarimanCycle() != null || state.arbitration() != null || state.constructionProgress() != null
                || state.simaris() != null || state.sentientOutpost() != null
                || List.of(state.alerts(), state.fissures(), state.allActiveMissions(), state.invasions(),
                state.allSorties(), state.archonHunts(), state.events(), state.news(), state.syndicateMissions(),
                state.allVoidTraders(), state.dailyDeals(), state.archimedeas(), state.descents(), state.flashSales(),
                state.globalUpgrades(), state.persistentEnemies(), state.nightwave(), state.steelPathOfferings(),
                state.conclaveChallenges(), state.clanInitiatives(), state.circuitChoices(), state.calendarSeasons(),
                state.vaultTraders(), state.weeklyChallenges(), state.darkSectors())
                .stream().anyMatch(section -> CollUtil.isNotEmpty(section));
        if (!hasData) {
            throw new IllegalArgumentException("world state response contains no recognized data");
        }
    }

    /**
     * 选择列表首项，空列表返回空值。
     *
     * @param values 待处理的值集合。
     * @return 目标类型对象。
     */
    private <T> T first(List<T> values) {
        return CollUtil.isEmpty(values) ? null : values.getFirst();
    }

    /**
     * 返回非空列表；空值统一转换为空不可变列表。
     *
     * @param values 待处理的值集合。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private <T> List<T> values(List<T> values) {
        return values == null ? List.of() : values;
    }

    /**
     * 读取数值列表中指定下标的值。
     *
     * @param values 待处理的值集合。
     * @param index 待读取的成员下标。
     * @return 当前操作计算或读取的数值。
     */
    private Double at(List<Double> values, int index) {
        return values.size() > index ? values.get(index) : null;
    }

    /**
     * 选择首个非空文本值。
     *
     * @param first 优先使用的首个值。
     * @param second 首个值不可用时使用的后续值。
     * @return 首个非空值对应的文本。
     */
    private String firstNonNull(String first, String second) {
        return StrUtil.isBlank(first) ? second : first;
    }
}
