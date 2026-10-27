package com.nnm.bot.warframe.persistent.official.repository.rewards;

import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.OfficialRelicRoot;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.repository.support.OfficialMainRepository;
import com.nnm.bot.warframe.persistent.official.repository.rewards.custom.OfficialRelicRepositoryCustom;

/** rewards/relics.json 主表数据访问；由生成器和契约测试共同维护。 */
public interface OfficialRelicRepository extends OfficialMainRepository<OfficialRelicRoot>, OfficialRelicRepositoryCustom {
    /**
     * 取得本 Repository 对应的官方 JSON 领域。
     *
     * @return 当前独立 JSON 的领域标识。
     */
    @Override
    default WarframeOfficialDataDomain domain() {
        return WarframeOfficialDataDomain.RELIC;
    }

    /**
     * 创建本 JSON 主表的待写入记录。
     *
     * @return 尚未持久化的具体主表实体。
     */
    @Override
    default OfficialRelicRoot newRecord() {
        return new OfficialRelicRoot();
    }
}
