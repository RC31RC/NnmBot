package com.nnm.bot.warframe.persistent.curated.repository.reference;

import com.nnm.bot.warframe.persistent.curated.pojo.entity.reference.WarframeCommunityTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityTermDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Collection;
import java.util.List;

public interface WarframeCommunityTermRepository extends JpaRepository<WarframeCommunityTerm, Long>, JpaSpecificationExecutor<WarframeCommunityTerm> {
    /**
     * 按给定记录键集合批量查询记录。
     *
     * @param keys 待处理的查找键集合。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeCommunityTerm> findAllByRecordKeyIn(Collection<String> keys);
    /**
     * 按记录键升序读取全部非受管投影。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeCommunityTermDTO> findAllProjectedByOrderByRecordKeyAsc();

    /**
     * 按给定记录键集合批量查询非受管投影。
     *
     * @param keys 待处理的查找键集合。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeCommunityTermDTO> findAllProjectedByRecordKeyIn(Collection<String> keys);

    /**
     * 按关键词筛选当前社区资料，并返回非受管投影分页。
     *
     * @param keyword 筛选使用的关键词。
     * @param pageable 分页大小、页码和排序条件。
     * @return 包含当前页记录及总数的分页结果。
     */
    default Page<WarframeCommunityTermDTO> query(String keyword, Pageable pageable) {
        return findBy(CommunitySpecifications.contains(keyword, List.of("recordKey", "termGroup", "englishText", "chineseText")), query -> query.as(WarframeCommunityTermDTO.class).page(pageable));
    }
}
