package com.nnm.bot.warframe.persistent.curated.repository.reference;

import cn.hutool.core.text.CharSequenceUtil;
import com.nnm.bot.warframe.persistent.curated.constant.WarframeArbitrationEfficiencyGrade;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityArbitrationEssenceDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.entity.reference.WarframeCommunityArbitrationEssence;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WarframeCommunityArbitrationEssenceRepository
    extends JpaRepository<WarframeCommunityArbitrationEssence, Long>,
    JpaSpecificationExecutor<WarframeCommunityArbitrationEssence> {
    /**
     * 按给定记录键集合批量查询记录。
     *
     * @param keys 待处理的查找键集合。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeCommunityArbitrationEssence> findAllByRecordKeyIn(Collection<String> keys);

    /**
     * 按记录键升序读取全部非受管投影。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeCommunityArbitrationEssenceDTO> findAllProjectedByOrderByRecordKeyAsc();

    /**
     * 按给定记录键集合批量查询非受管投影。
     *
     * @param keys 待处理的查找键集合。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeCommunityArbitrationEssenceDTO> findAllProjectedByRecordKeyIn(Collection<String> keys);

    /**
     * 按关键词筛选当前社区资料，并返回非受管投影分页。
     *
     * @param keyword 筛选使用的关键词。
     * @param localizedNodeKeys 官方中文名称命中的节点键。
     * @param pageable 分页大小、页码和排序条件。
     * @return 包含当前页记录及总数的分页结果。
     */
    default Page<WarframeCommunityArbitrationEssenceDTO> query(String keyword, Set<String> localizedNodeKeys,
        Pageable pageable) {
        String normalizedKeyword = CharSequenceUtil.trimToEmpty(keyword);
        Specification<WarframeCommunityArbitrationEssence> textMatches = CommunitySpecifications
            .contains(normalizedKeyword, List.of("recordKey", "nodeKey", "nodeName", "systemName"));
        String pattern = "%"
            + normalizedKeyword.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_").toLowerCase(Locale.ROOT)
            + "%";
        Specification<WarframeCommunityArbitrationEssence> amountMatches = (root, query, builder) -> {
            if (CharSequenceUtil.isBlank(normalizedKeyword))
                return builder.disjunction();
            return builder.like(builder.lower(root.get("essenceAmount").as(String.class)), pattern, '\\');
        };
        WarframeArbitrationEfficiencyGrade matchingGrade = null;
        for (WarframeArbitrationEfficiencyGrade grade : WarframeArbitrationEfficiencyGrade.values()) {
            if (grade.code().equals(normalizedKeyword.toUpperCase(Locale.ROOT))
                || CharSequenceUtil.containsIgnoreCase(grade.label(), normalizedKeyword)) {
                matchingGrade = grade;
                break;
            }
        }
        WarframeArbitrationEfficiencyGrade gradeKeyword = matchingGrade;
        Specification<WarframeCommunityArbitrationEssence> gradeMatches = (root, query, builder) -> gradeKeyword == null
            ? builder.disjunction() : builder.equal(root.get("efficiencyGrade"), gradeKeyword);
        Specification<WarframeCommunityArbitrationEssence> localizedNameMatches = (root, query,
            builder) -> localizedNodeKeys.isEmpty() ? builder.disjunction() : root.get("nodeKey").in(localizedNodeKeys);
        return findBy(textMatches.or(amountMatches).or(gradeMatches).or(localizedNameMatches),
            query -> query.as(WarframeCommunityArbitrationEssenceDTO.class).page(pageable));
    }
}
