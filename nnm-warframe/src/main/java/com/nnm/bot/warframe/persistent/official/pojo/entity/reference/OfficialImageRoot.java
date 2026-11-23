package com.nnm.bot.warframe.persistent.official.pojo.entity.reference;

import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialRecord;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 图标资产路径与内容哈希的索引。按原始图片路径定位可用图片资源，物品归属由对应定义确定。 同一业务键可保留官方和自维护版本。由生成器和契约测试共同维护。
 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_image_root")
public class OfficialImageRoot extends OfficialRecord {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 内容校验摘要是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "contenthash_state")
    private Short contentHashState;

    /** 内容校验摘要。 */
    @Column(name = "contenthash", columnDefinition = "TEXT")
    private String contentHash;

    /** 论坛图片名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "forumname_state")
    private Short forumNameState;

    /** 论坛图片名称。 */
    @Column(name = "forumname", columnDefinition = "TEXT")
    private String forumName;

}
