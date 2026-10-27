package com.nnm.bot.warframe.persistent.official.pojo.entity.reference;

import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialRecord;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** 源 IMAGE 独立主表 的固定业务列；由生成器和契约测试共同维护。 */
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
