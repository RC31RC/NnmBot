package com.nnm.bot.warframe.persistent.official.pojo.vo.items;

import cn.hutool.core.collection.CollUtil;
import java.util.List;

public record WarframeKeyVO(String query, int page, int totalPages, int totalCount, List<EntryVO> items) {
    /**
     * 构造键展示对象，并保存初始化参数。
     *
     * @param query 当前业务的查询条件。
     * @param page 从一开始的页码。
     * @param totalPages 总数页数。
     * @param totalCount 总数数量。
     * @param items 当前物品或成员列表。
     */
    public WarframeKeyVO {
        items = List.copyOf(items);
    }

    /**
     * 对已筛选数据执行分页。
     *
     * @param query 当前业务的查询条件。
     * @param matches 匹配集合。
     * @param requested 请求的。
     * @return 钥匙及任务入口定义的分页视图。
     */
    public static WarframeKeyVO page(String query, List<EntryVO> matches, int requested) {
        int pages = Math.max(1, (int) ((matches.size() + 3L) / 4));
        int page = Math.max(1, Math.min(requested, pages));
        int from = (page - 1) * 4;
        return new WarframeKeyVO(query, page, pages, matches.size(),
                matches.subList(from, Math.min(from + 4, matches.size())));
    }

    /**
     * 生成当前数据对应的文本内容。
     *
     * @return 按当前对象字段组成的文本内容。
     */
    public String text() {
        StringBuilder text = new StringBuilder("钥匙链：").append(page).append("/").append(totalPages)
                .append(" 页，共 ").append(totalCount).append(" 条");
        for (EntryVO entry : items) {
            text.append("\n\n").append(entry.name());
            if (entry.description() != null) text.append("\n").append(entry.description());
            if (entry.replayable() != null) text.append("\n可重玩：").append(entry.replayable() ? "是" : "否");
            if (entry.minEnemyLevel() != null || entry.maxEnemyLevel() != null) {
                text.append("\n敌人等级：").append(entry.minEnemyLevel() == null ? "?" : entry.minEnemyLevel())
                        .append(" - ").append(entry.maxEnemyLevel() == null ? "?" : entry.maxEnemyLevel());
            }
            if (CollUtil.isEmpty(entry.stages())) text.append("\n阶段未提供或未采纳");
            for (StageVO stage : entry.stages()) {
                text.append("\n阶段 ").append(stage.order() + 1);
                if (stage.name() != null) text.append("：").append(stage.name());
                stage.items().forEach(item -> text.append("\n发放：").append(item.name()));
                if (stage.message() != null) text.append("\n").append(stage.message().text());
            }
            if (CollUtil.isEmpty(entry.rewards())) text.append("\n奖励未提供或未采纳");
            for (RewardVO reward : entry.rewards()) {
                text.append("\n").append(switch (reward.kind()) {
                    case "mission_credits" -> "任务信用点";
                    case "mission_item", "mission_store_item", "mission_counted_item" -> "任务奖励";
                    default -> "钥匙奖励";
                });
                if (reward.name() != null) text.append("：").append(reward.name());
                text.append(reward.amount() == null ? " · 数量未提供" : " × " + reward.amount());
            }
            if (entry.missionRewardDroptable() != null) {
                text.append("\n任务奖励池：").append(entry.missionRewardDroptable());
            }
            if (entry.cacheRewardManifest() != null) text.append("\n储藏奖励池：").append(entry.cacheRewardManifest());
        }
        if (CollUtil.isEmpty(items)) text.append("\n没有找到匹配的钥匙链");
        return text.toString();
    }

    public record EntryVO(String key, String name, String description, String imageReference, String iconUrl,
            String sourceCode, String resolutionMode, Boolean replayable, Integer minEnemyLevel,
            Integer maxEnemyLevel, String missionRewardDroptable, String cacheRewardManifest,
            List<StageVO> stages, List<RewardVO> rewards) {
        /**
         * 构造条目展示对象，并保存初始化参数。
         *
         * @param key 当前记录或字段的查找键。
         * @param name 当前业务对象的名称。
         * @param description 说明。
         * @param imageReference 图片引用。
         * @param iconUrl 图标地址。
         * @param sourceCode 来源代码。
         * @param resolutionMode 解析状态模式。
         * @param replayable 可重放。
         * @param minEnemyLevel 最小敌人等级。
         * @param maxEnemyLevel 最大敌人等级。
         * @param missionRewardDroptable 任务奖励掉落表。
         * @param cacheRewardManifest 缓存奖励发布清单。
         * @param stages 阶段集合。
         * @param rewards 奖励集合。
         */
        public EntryVO {
            stages = List.copyOf(stages);
            rewards = List.copyOf(rewards);
        }
    }

    public record StageVO(String recordKey, int order, String stageKey, String name,
            List<WarframeMailVO.AttachmentVO> items, WarframeMailVO message) {
        /**
         * 构造阶段展示对象，并保存初始化参数。
         *
         * @param recordKey 记录键。
         * @param order 成员在输入集合中的位置。
         * @param stageKey 阶段键。
         * @param name 当前业务对象的名称。
         * @param items 当前物品或成员列表。
         * @param message 待发送或处理的消息。
         */
        public StageVO {
            items = List.copyOf(items);
        }
    }

    public record RewardVO(int order, String kind, String rewardType, String itemKey, String name, Long amount) {
    }
}
