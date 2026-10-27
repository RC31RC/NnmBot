package com.nnm.bot.core.group.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * 成员合并后的旧主键解析记录。它不是第二条成员数据，只用于在途操作解析到新主键。
 */
@Getter
@Setter
@Entity
@Table(name = "bot_group_member_merge")
public class BotGroupMemberMerge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "source_member_id", nullable = false, unique = true)
    private Long sourceMemberId;

    @Column(name = "target_member_id", nullable = false)
    private Long targetMemberId;

    @Column(name = "merged_at", nullable = false)
    private LocalDateTime mergedAt;
}
