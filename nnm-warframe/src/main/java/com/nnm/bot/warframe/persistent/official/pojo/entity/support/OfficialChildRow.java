package com.nnm.bot.warframe.persistent.official.pojo.entity.support;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

/** 子表公共字段；bkey 保存直接父记录的本地 id。 */
@Getter
@Setter
@MappedSuperclass
public abstract class OfficialChildRow extends OfficialRow {
    /** 直接父记录的本地主键；与父表主键相同。 */
    @Column(name = "bkey", nullable = false, insertable = false, updatable = false)
    private Long bkey;

    /** 字典成员在源数据中的原始键；数组成员为空。 */
    @Column(name = "member_key", columnDefinition = "TEXT")
    private String memberKey;

    /** 子记录在源集合中的顺序。 */
    @Column(name = "sort_order", nullable = false)
    private int sortOrder;
}
