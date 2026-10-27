package com.nnm.bot.warframe.persistent.curated.pojo.entity.reference;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

/** 人工资料的共同身份；业务行没有输入渠道或外部来源标签。 */
@Getter
@Setter
@MappedSuperclass
public abstract class WarframeCommunityRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "record_key", nullable = false, unique = true, length = 500)
    private String recordKey;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    @Version
    @Column(nullable = false)
    private long version;
}
