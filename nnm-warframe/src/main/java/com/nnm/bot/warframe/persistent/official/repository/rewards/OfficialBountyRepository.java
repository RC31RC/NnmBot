package com.nnm.bot.warframe.persistent.official.repository.rewards;

import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.OfficialBountyRoot;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.repository.support.OfficialMainRepository;

/** rewards/bounties.json 主表数据访问；由生成器和契约测试共同维护。 */
public interface OfficialBountyRepository extends OfficialMainRepository<OfficialBountyRoot> {
    /**
     * 取得本 Repository 对应的官方 JSON 领域。
     *
     * @return 当前独立 JSON 的领域标识。
     */
    @Override
    default WarframeOfficialDataDomain domain() {
        return WarframeOfficialDataDomain.BOUNTY;
    }

    /**
     * 创建本 JSON 主表的待写入记录。
     *
     * @return 尚未持久化的具体主表实体。
     */
    @Override
    default OfficialBountyRoot newRecord() {
        return new OfficialBountyRoot();
    }
}
