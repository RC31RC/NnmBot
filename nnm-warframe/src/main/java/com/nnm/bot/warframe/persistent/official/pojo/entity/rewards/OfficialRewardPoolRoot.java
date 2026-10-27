package com.nnm.bot.warframe.persistent.official.pojo.entity.rewards;

import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialRecord;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

/** 源 REWARD_POOL 独立主表 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_reward_pool_root")
public class OfficialRewardPoolRoot extends OfficialRecord {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 奖池键是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "poolkey_state")
    private Short poolKeyState;

    /** 奖池键。 */
    @Column(name = "poolkey", columnDefinition = "TEXT")
    private String poolKey;

    /** 阶段列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "stages_state")
    private Short stagesState;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRewardPoolStagesItems> officialRewardPoolStagesItemsRows = new ArrayList<>();
}
