package com.nnm.bot.warframe.persistent.official.pojo.vo.guide;

import java.math.BigDecimal;
import java.util.List;

public record WarframeGuideVO<T>(String query, String world, String layer, List<T> items,
                                 int page, int totalPages, int totalCount) {
    /**
     * 构造指南展示对象，并保存初始化参数。
     *
     * @param query 当前业务的查询条件。
     * @param world 世界。
     * @param layer 图层。
     * @param items 当前物品或成员列表。
     * @param page 从一开始的页码。
     * @param totalPages 总数页数。
     * @param totalCount 总数数量。
     */
    public WarframeGuideVO {
        items = List.copyOf(items);
    }

    /**
     * 对已筛选数据执行分页。
     *
     * @param query 当前业务的查询条件。
     * @param world 世界。
     * @param layer 图层。
     * @param entries 条目集合。
     * @param page 从一开始的页码。
     * @return 钓鱼或地图资料的分页视图。
     */
    public static <T> WarframeGuideVO<T> page(String query, String world, String layer,
                                              List<T> entries, int page) {
        if (page < 1) throw new IllegalArgumentException("Page must be positive");
        int from = (int) Math.min((long) (page - 1) * 4, entries.size());
        return new WarframeGuideVO<>(query, world, layer,
                entries.subList(from, Math.min(from + 4, entries.size())), page,
                Math.max(1, (int) ((entries.size() + 3L) / 4)), entries.size());
    }

    /**
     * 生成当前数据的分页说明。
     *
     * @return 当前数据的分页说明对应的文本。
     */
    public String pagination() {
        return page + "/" + totalPages + " 页，共 " + totalCount + " 条";
    }

    /**
     * 将开放世界代码转换为中文地点名称。
     * @param world 世界。
     * @return 开放世界的中文名称。
     */
    public static String worldName(String world) {
        return switch (world) {
            case "plains" -> "夜灵平野";
            case "vallis" -> "奥布山谷";
            case "deimos" -> "魔胎之境";
            default -> world;
        };
    }

    public record FishVO(String name, String world, String location, String rarity, String baitName,
                         FishTimeVO time, List<String> spears, List<String> uniqueResources, List<SizeRewardVO> sizeRewards,
                         String maximumMass, String maximumPoint, String sourceCode, String resolutionMode) {
        /**
         * 构造鱼类展示对象，并保存初始化参数。
         *
         * @param name 当前业务对象的名称。
         * @param world 世界。
         * @param location 位置。
         * @param rarity 稀有度。
         * @param baitName 鱼饵名称。
         * @param time 时间。
         * @param spears 鱼叉集合。
         * @param uniqueResources 唯一资源集合。
         * @param sizeRewards 数量奖励集合。
         * @param maximumMass 最大质量。
         * @param maximumPoint 最大点位。
         * @param sourceCode 来源代码。
         * @param resolutionMode 解析状态模式。
         */
        public FishVO {
            spears = spears == null ? List.of() : List.copyOf(spears);
            uniqueResources = uniqueResources == null ? List.of() : List.copyOf(uniqueResources);
            sizeRewards = sizeRewards == null ? List.of() : List.copyOf(sizeRewards);
        }
    }

    public record FishTimeVO(String label, List<TimeConditionVO> conditions) {
        /**
         * 构造鱼类时间展示对象，并保存初始化参数。
         *
         * @param label 标签标题。
         * @param conditions 条件集合。
         */
        public FishTimeVO {
            conditions = conditions == null ? List.of() : List.copyOf(conditions);
        }
    }

    public record TimeConditionVO(String name, boolean appears, boolean preferred) {
    }

    public record SizeRewardVO(String size, List<ResourceAmountVO> resources, BigDecimal standing) {
        /**
         * 构造数量奖励展示对象，并保存初始化参数。
         *
         * @param size 当前操作允许的数据数量。
         * @param resources 资源集合。
         * @param standing 声望。
         */
        public SizeRewardVO {
            resources = resources == null ? List.of() : List.copyOf(resources);
        }
    }

    public record ResourceAmountVO(String name, BigDecimal amount) {
    }

    public record FeatureVO(String name, String world, String layer, String featureType,
                            GeometryVO geometry, FeaturePropertiesVO properties,
                            String sourceCode, String resolutionMode) {
    }

    public record GeometryVO(String type, List<BigDecimal> pointCoordinates, int boundaryRingCount) {
        /**
         * 构造几何图形展示对象，并保存初始化参数。
         *
         * @param type 目标业务类型。
         * @param pointCoordinates 点位坐标集合。
         * @param boundaryRingCount 边界环数量。
         */
        public GeometryVO {
            pointCoordinates = pointCoordinates == null ? List.of() : List.copyOf(pointCoordinates);
        }
    }

    public record FeaturePropertiesVO(String description) {
    }
}
