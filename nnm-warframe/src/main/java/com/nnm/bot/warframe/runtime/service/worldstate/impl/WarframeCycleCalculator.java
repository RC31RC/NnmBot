package com.nnm.bot.warframe.runtime.service.worldstate.impl;

import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/** 按世界状态协议的周期规则执行确定性计算，阶段时间采用左闭右开区间。 */
public final class WarframeCycleCalculator {
    private static final long EARTH_FULL_CYCLE_SECONDS = 28_800L;
    private static final long EARTH_DAY_SECONDS = 14_400L;
    private static final long VALLIS_FULL_CYCLE_MILLIS = 1_600_000L;
    private static final long VALLIS_WARM_MILLIS = 400_000L;
    private static final long CETUS_FULL_CYCLE_MILLIS = 9_000_000L;
    private static final long CETUS_DAY_MILLIS = 6_000_000L;
    private static final long BOUNTY_ANCHOR_MAX_FUTURE_SECONDS = 9_000L;
    private static final String CORPUS_FACTION = "corpus";
    private static final String GRINEER_FACTION = "grineer";
    private static final long ZARIMAN_FULL_CYCLE_MILLIS = 18_000_000L;
    private static final long ZARIMAN_STATE_MILLIS = 9_000_000L;
    private static final long DUVIRI_FULL_CYCLE_SECONDS = 36_000L;
    private static final long DUVIRI_STATE_SECONDS = 7_200L;
    private static final List<String> DUVIRI_STATES = List.of("sorrow", "fear", "joy", "anger", "envy");

    /**
     * 禁止实例化，仅通过本类的静态方法使用此能力。
     */
    private WarframeCycleCalculator() {}

    /**
     * 根据固定日夜时长计算地球当前阶段及下一次切换时间。
     *
     * @param now 本次操作使用的当前时间。
     * @return 地球周期对象。
     */
    public static WorldStateDTO.EarthCycleDTO earth(Instant now) {
        long phase = Math.floorMod(now.getEpochSecond(), EARTH_FULL_CYCLE_SECONDS);
        return new WorldStateDTO.EarthCycleDTO(phase < EARTH_DAY_SECONDS,
            Instant.ofEpochSecond(now.getEpochSecond() + EARTH_DAY_SECONDS - phase % EARTH_DAY_SECONDS));
    }

    /**
     * 以暖期起点为锚点，计算奥布山谷当前冷热阶段。
     *
     * @param now 本次操作使用的当前时间。
     * @param warmEpoch 奥布山谷暖期的已知起点。
     * @return 奥布山谷周期对象。
     */
    public static WorldStateDTO.VallisCycleDTO vallis(Instant now, Instant warmEpoch) {
        long phase = Math.floorMod(now.toEpochMilli() - warmEpoch.toEpochMilli(), VALLIS_FULL_CYCLE_MILLIS);
        boolean warm = phase < VALLIS_WARM_MILLIS;
        return new WorldStateDTO.VallisCycleDTO(warm,
            now.plusMillis((warm ? VALLIS_WARM_MILLIS : VALLIS_FULL_CYCLE_MILLIS) - phase));
    }

    /**
     * 以有效的赏金过期时间为锚点，计算希图斯昼夜周期。
     *
     * @param now 本次操作使用的当前时间。
     * @param bountyExpiry 官方赏金的结束时间，用于校准当前轮次。
     * @param maxAge 允许赏金锚点早于当前时间的最大时长。
     * @return 希图斯周期对象。
     */
    public static WorldStateDTO.CetusCycleDTO cetus(Instant now, Instant bountyExpiry, Duration maxAge) {
        if (!validBountyAnchor(now, bountyExpiry, maxAge)) {
            return null;
        }
        // 赏金结束时间用于校准夜晚结束点；仅接受近期有效锚点，避免回退到 Unix 起点。
        Instant anchor = bountyExpiry.truncatedTo(ChronoUnit.MINUTES);
        long phase = Math.floorMod(now.toEpochMilli() - anchor.toEpochMilli(), CETUS_FULL_CYCLE_MILLIS);
        boolean day = phase < CETUS_DAY_MILLIS;
        return new WorldStateDTO.CetusCycleDTO(day,
            now.plusMillis((day ? CETUS_DAY_MILLIS : CETUS_FULL_CYCLE_MILLIS) - phase));
    }

    /**
     * 以有效赏金结束时间和已知阵营锚点计算扎里曼当前阵营周期。
     *
     * @param now 本次操作使用的当前时间。
     * @param bountyExpiry 官方赏金的结束时间，用于校准当前轮次。
     * @param bountyOffset 官方赏金结束时间与阵营轮换锚点之间的偏移。
     * @param corpusEpoch 扎里曼 Corpus 阶段的已知起点。
     * @param maxAge 允许赏金锚点早于当前时间的最大时长。
     * @return 扎里曼周期对象。
     */
    public static WorldStateDTO.ZarimanCycleDTO zariman(Instant now, Instant bountyExpiry, Duration bountyOffset,
        Instant corpusEpoch, Duration maxAge) {
        if (!validBountyAnchor(now, bountyExpiry, maxAge)) {
            return null;
        }
        Instant calculationPoint = bountyExpiry.minus(bountyOffset);
        long elapsed =
            Math.floorMod(calculationPoint.toEpochMilli() - corpusEpoch.toEpochMilli(), ZARIMAN_FULL_CYCLE_MILLIS);
        boolean corpus = ZARIMAN_FULL_CYCLE_MILLIS - elapsed > ZARIMAN_STATE_MILLIS;
        // 先扣除赏金偏移，再按分钟对齐阵营轮次的结束时间。
        // 按完整阵营阶段外推近期过期锚点，并覆盖恰好到期的边界。
        Instant anchor = calculationPoint.plusSeconds(30).truncatedTo(ChronoUnit.MINUTES);
        long stages = Math.floorDiv(Duration.between(anchor, now).getSeconds(),
            Duration.ofMillis(ZARIMAN_STATE_MILLIS).toSeconds()) + 1;
        Instant expiry = anchor.plusMillis(stages * ZARIMAN_STATE_MILLIS);
        if (Math.floorMod(stages, 2) != 0) {
            corpus = !corpus;
        }
        return new WorldStateDTO.ZarimanCycleDTO(corpus ? CORPUS_FACTION : GRINEER_FACTION, expiry);
    }

    /**
     * 以配置的轮换起点计算双衍王境当前情绪及切换时间。
     *
     * @param now 本次操作使用的当前时间。
     * @param epoch 当前周期计算使用的已知起点。
     * @return 双衍王境周期对象。
     */
    public static WorldStateDTO.DuviriCycleDTO duviri(Instant now, Instant epoch) {
        long elapsed = Duration.between(epoch, now).getSeconds();
        long phase = Math.floorMod(elapsed, DUVIRI_FULL_CYCLE_SECONDS);
        int stateIndex = (int)(phase / DUVIRI_STATE_SECONDS);
        long nextStage = Math.floorDiv(elapsed, DUVIRI_STATE_SECONDS) + 1;
        // 保留校准秒数，截断到分钟会使上一状态提前 52 秒过期。
        return new WorldStateDTO.DuviriCycleDTO(DUVIRI_STATES.get(stateIndex),
            epoch.plusSeconds(nextStage * DUVIRI_STATE_SECONDS));
    }

    /**
     * 判断赏金过期时间能否作为本次周期计算的可靠锚点。
     *
     * @param now 本次操作使用的当前时间。
     * @param expiry 官方赏金的结束时间，可缺省。
     * @param maxAge 允许赏金锚点早于当前时间的最大时长。
     * @return 赏金结束时间存在且处于允许校准窗口时返回真，否则返回假。
     */
    static boolean validBountyAnchor(Instant now, Instant expiry, Duration maxAge) {
        return expiry != null && !expiry.isBefore(now.minus(maxAge))
            && !expiry.isAfter(now.plusSeconds(BOUNTY_ANCHOR_MAX_FUTURE_SECONDS));
    }
}
