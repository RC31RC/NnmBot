package com.nnm.bot.warframe.runtime.constant;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

/** 三地普通赏金的四段掉落分组，不等于任务实际阶段序号。 */
public enum WarframeBountyRewardPhase {
    FIRST(0), MIDDLE(1), PENULTIMATE_OF_FIVE(2), FINAL(3);

    private static final Pattern STANDARD_POOL = Pattern.compile(
        "^/Lotus/Types/Game/MissionDecks/(?:EidolonJobMissionRewards|VenusJobMissionRewards|DeimosMissionRewards)/"
            + "(?:(?:Venus)?Tier[A-Z]|Narmer)Table([A-Z])Rewards$",
        Pattern.CASE_INSENSITIVE);
    private static final Set<Integer> REQUIRED_BUCKETS =
        Set.of(FIRST.bucket, MIDDLE.bucket, PENULTIMATE_OF_FIVE.bucket, FINAL.bucket);

    private final int bucket;

    WarframeBountyRewardPhase(int bucket) {
        this.bucket = bucket;
    }

    /**
     * 严格限定已核对的四组结构；隔离库等其他协议不能按位置推断。
     *
     * @param poolKey 官方奖池完整引用。
     * @param stageCount 本轮实际阶段数。
     * @param buckets 已入库奖池中的非空组索引。
     * @return 可把奖池分组对应到任务阶段时为真。
     */
    public static boolean supports(String poolKey, int stageCount, Set<Integer> buckets) {
        return rotation(poolKey).isPresent() && stageCount >= 3 && stageCount <= 5 && REQUIRED_BUCKETS.equals(buckets);
    }

    /**
     * 读取普通平原赏金池的轮换代码，路径中的阶段档位与轮换独立。
     *
     * @param poolKey 当轮动态快照实际引用的奖池路径。
     * @return 奖池命名合同中的轮换；其他类型奖池为空。
     */
    public static Optional<String> rotation(String poolKey) {
        if (poolKey == null)
            return Optional.empty();
        var match = STANDARD_POOL.matcher(poolKey);
        return match.matches() ? Optional.of(match.group(1).toUpperCase(Locale.ROOT)) : Optional.empty();
    }

    /**
     * 按已验证的任务阶段数选择相应掉落分组。
     *
     * @param stage 从零开始的阶段序号。
     * @param stageCount 本轮实际阶段数。
     * @return 该阶段的掉落分组索引。
     */
    public static int bucketFor(int stage, int stageCount) {
        if (stage == 0)
            return FIRST.bucket;
        if (stage == stageCount - 1)
            return FINAL.bucket;
        return stageCount == 5 && stage == stageCount - 2 ? PENULTIMATE_OF_FIVE.bucket : MIDDLE.bucket;
    }
}
