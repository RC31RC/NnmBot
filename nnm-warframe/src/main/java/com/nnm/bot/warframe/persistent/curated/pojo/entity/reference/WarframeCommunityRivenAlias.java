package com.nnm.bot.warframe.persistent.curated.pojo.entity.reference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** 裂罅玩家术语到属性代码的映射，供命令识别词条或组合。正式属性和单位存于官方裂罅属性表。 */
@Getter
@Setter
@Entity
@Table(name = "wf_community_riven_alias")
public class WarframeCommunityRivenAlias extends WarframeCommunityRow {
    /** 对应官方属性代码；组合使用以加号连接的多个代码。 */
    @Column(name = "attribute_code", nullable = false, columnDefinition = "TEXT")
    private String attributeCode;

    @Column(name = "alias_name", nullable = false, columnDefinition = "TEXT")
    private String aliasName;

}
