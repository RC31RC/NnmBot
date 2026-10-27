package com.nnm.bot.warframe.persistent.curated.pojo.entity.watch;

import cn.hutool.core.util.StrUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * 群组成员长期维护的 Warframe 蹲任务规则。
 *
 * <p>groupId 和 memberId 是跨模块的逻辑关联，不声明 JPA 关联，避免 Warframe
 * 模块反向依赖管理端数据模型。</p>
 */
@Getter
@Setter
@Entity
@Table(name = "wf_group_watch_rule")
public class WarframeGroupWatchRule {

    /** 规则自增主键。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属逻辑群组主键，关联 bot_group.id。 */
    @Column(name = "group_id", nullable = false)
    private Long groupId;

    /** 订阅成员主键，关联当前群的 bot_group_member.id。 */
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    /** 规范化主题编码，例如 fissures、arbitration。 */
    @Column(name = "watch_type", nullable = false, length = 80)
    private String watchType;

    /** 匹配方式；filter 表示本项目订阅条件语法。 */
    @Column(name = "watch_key", nullable = false, length = 160)
    private String watchKey;

    /** 条件文本，竖线分隔多个或条件，星号表示该主题全部。 */
    @Column(name = "watch_value", nullable = false, length = 500)
    private String watchValue;

    /** 是否启用规则。 */
    @Column(nullable = false)
    private boolean enabled = true;

    /** 是否推送通知；关闭时仍记录匹配事件。 */
    @Column(name = "notify_enabled", nullable = false)
    private boolean notifyEnabled = true;

    /** 最近已处理的事件摘要及记忆到期时间 JSON；有界保留，避免任务集合缩小时重复通知。 */
    @Column(name = "matched_events_json", columnDefinition = "TEXT")
    private String matchedEventsJson;

    /** 规则创建时间。 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** 规则或去重记录最后更新时间。 */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 首次保存记录时初始化默认状态。
     */
    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        normalize();
    }

    /**
     * 更新记录时维护本地元数据。
     */
    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
        normalize();
    }

    /**
     * 规范化输入，消除查询或比较时的格式差异。
     */
    private void normalize() {
        watchType = StrUtil.trim(watchType);
        watchKey = StrUtil.trim(watchKey);
        watchValue = StrUtil.trim(watchValue);

    }
}
