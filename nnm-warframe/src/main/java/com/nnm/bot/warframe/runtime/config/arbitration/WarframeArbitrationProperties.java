package com.nnm.bot.warframe.runtime.config.arbitration;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.collection.CollUtil;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.time.Duration;
import java.time.ZoneId;
import java.util.List;

/** 仲裁运行时快照的查询规则；来源拉取独立配置，查询不触发网络请求。 */
@Getter
@Setter
@ConfigurationProperties(prefix = "nnm.warframe.query.arbitration")
public class WarframeArbitrationProperties {
    /** 快照超过此周期时显示陈旧提示；刷新任务单独决定何时拉取来源。 */
    private Duration refreshInterval = Duration.ofDays(7);
    /** 查询侧有效历史范围，避免把来源缺口解释为没有任务。 */
    private Duration historyRetention = Duration.ofDays(90);
    /** 今日仲裁所用的自然日时区。 */
    private ZoneId zone = ZoneId.of("Asia/Shanghai");
    /** 未来查询覆盖天数。 */
    private int previewDays = 7;
    /** 未来及高效视图最多展示的任务数。 */
    private int previewLimit = 10;
    /** NyxBot 高效仲裁规则中的任务类型候选。 */
    private List<String> efficientMissionTypes = List.of("防御", "拦截", "Defense", "Interception");
    /** 与类型、阵营同时匹配的星球候选。 */
    private List<String> efficientPlanets = List.of("谷神星", "水星", "Ceres", "Mercury");
    /** 与类型、星球同时匹配的阵营候选。 */
    private List<String> efficientEnemies = List.of("Grineer", "Infestation", "Infested");

    /** 查询日期、保留时长和完整筛选规则必须在启动时有效。 */
    @PostConstruct
    public void validate() {
        if (zone == null || invalid(refreshInterval) || invalid(historyRetention)
                || previewDays < 1 || previewDays > 30 || previewLimit < 1 || previewLimit > 100
                || empty(efficientMissionTypes) || empty(efficientPlanets) || empty(efficientEnemies)) {
            throw new IllegalArgumentException("Invalid nnm.warframe.query.arbitration configuration");
        }
    }

    /**
     * 保留时间及提示周期必须为正，不能用零值关闭校验。
     *
     * @param value 待处理的字段值。
     * @return 满足构造包含明确输入位置的解析失败异常时为真，否则为假。
     */
    private boolean invalid(Duration value) {
        return value == null || value.isNegative() || value.isZero();
    }

    /**
     * 完整候选名不可为空，避免空规则意外匹配全部任务。
     *
     * @param values 待处理的值集合。
     * @return 满足构造没有有效业务内容的缺省对象时为真，否则为假。
     */
    private boolean empty(List<String> values) {
        return CollUtil.isEmpty(values)
                || values.stream().anyMatch(value -> StrUtil.isBlank(value));
    }
}
