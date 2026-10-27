package com.nnm.bot.warframe.persistent.official.pojo.entity.support;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.Setter;

/** 共享序列仅分配跨数据集唯一标识，不引入跨数据集父表或数据关联。 */
@Getter
@Setter
@MappedSuperclass
public abstract class OfficialRow {
    /** 本表记录的本地主键。 */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "officialRowSequence")
    @SequenceGenerator(name = "officialRowSequence", sequenceName = "wf_official_row_id_seq", allocationSize = 50)
    private Long id;
}
