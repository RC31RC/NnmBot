package com.nnm.bot.warframe.persistent.official.repository.rewards.custom.impl;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.METADATA_BKEY;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.persistent.official.repository.rewards.custom.OfficialRelicRepositoryCustom;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.MaintenanceType;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialRelicRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialRelicStatesEntries;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialRelicStatesEntriesRewardsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialRelicLocationsItems;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Locale;

/** 跨遗物、状态、奖励三级且含覆盖排除子查询，按需启用 QueryDSL。 */
public class OfficialRelicRepositoryCustomImpl implements OfficialRelicRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 从本地遗物主表筛出可能匹配名称、奖励或获取地点的优先记录。
     *
     * @param keyword 命令查询词；为空时返回全部启用的优先遗物主记录。
     * @return 已按自维护优先规则排除被覆盖官方记录的主键列表。
     */
    @Override
    public List<Long> findPreferredRelicIds(String keyword) {
        QOfficialRelicRoot root = QOfficialRelicRoot.officialRelicRoot;
        QOfficialRelicRoot self = new QOfficialRelicRoot("selfMaintainedRoot");
        QOfficialRelicStatesEntries state = QOfficialRelicStatesEntries.officialRelicStatesEntries;
        QOfficialRelicStatesEntriesRewardsItems reward =
                QOfficialRelicStatesEntriesRewardsItems.officialRelicStatesEntriesRewardsItems;
        QOfficialRelicLocationsItems location = QOfficialRelicLocationsItems.officialRelicLocationsItems;

        BooleanExpression preferred = preferred(root, self);
        BooleanExpression predicate = root.enabled.isTrue().and(root.bkey.ne(METADATA_BKEY)).and(preferred);

        if (StrUtil.isNotBlank(keyword)) {
            String queryText = keyword.trim().toLowerCase(Locale.ROOT);
            BooleanExpression rootMatch = root.bkey.containsIgnoreCase(queryText)
                    .or(root.officialName.containsIgnoreCase(queryText))
                    .or(root.localizedName.containsIgnoreCase(queryText))
                    .or(root.name.containsIgnoreCase(queryText))
                    .or(root.nameEn.containsIgnoreCase(queryText))
                    .or(root.nameZh.containsIgnoreCase(queryText));
            BooleanExpression rewardMatch = JPAExpressions.selectOne().from(state).join(reward)
                    .on(reward.bkey.eq(state.id))
                    .where(state.bkey.eq(root.id).and(reward.itemUniqueName.containsIgnoreCase(queryText)
                            .or(reward.itemName.containsIgnoreCase(queryText))
                            .or(reward.itemNameEn.containsIgnoreCase(queryText))
                            .or(reward.itemNameZh.containsIgnoreCase(queryText))
                            .or(reward.rewardName.containsIgnoreCase(queryText)))).exists();
            BooleanExpression locationMatch = JPAExpressions.selectOne().from(location)
                    .where(location.bkey.eq(root.id).and(location.source.containsIgnoreCase(queryText)
                            .or(location.sourceZh.containsIgnoreCase(queryText)))).exists();
            predicate = predicate.and(rootMatch.or(rewardMatch).or(locationMatch));
        }

        return new JPAQueryFactory(entityManager).select(root.id).distinct().from(root).where(predicate)
                .orderBy(root.id.asc()).fetch();
    }

    private static BooleanExpression preferred(QOfficialRelicRoot root, QOfficialRelicRoot self) {
        return root.maintenanceType.eq(MaintenanceType.SELF_MAINTAINED)
                .or(JPAExpressions.selectOne().from(self)
                        .where(self.bkey.lower().eq(root.bkey.lower())
                                .and(self.maintenanceType.eq(MaintenanceType.SELF_MAINTAINED))
                                .and(self.enabled.isTrue())).notExists());
    }

}
