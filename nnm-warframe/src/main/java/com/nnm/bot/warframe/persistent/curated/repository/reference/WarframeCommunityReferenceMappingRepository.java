package com.nnm.bot.warframe.persistent.curated.repository.reference;

import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityReferenceMappingDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.entity.reference.WarframeCommunityReferenceMapping;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/** 独立读取社区解析映射，运行时按作用域建立索引。 */
public interface WarframeCommunityReferenceMappingRepository extends JpaRepository<WarframeCommunityReferenceMapping, Long>,
    JpaSpecificationExecutor<WarframeCommunityReferenceMapping> {
    List<WarframeCommunityReferenceMapping> findAllByRecordKeyIn(Collection<String> keys);

    List<WarframeCommunityReferenceMappingDTO> findAllProjectedByRecordKeyIn(Collection<String> keys);

    List<WarframeCommunityReferenceMappingDTO> findAllProjectedByOrderByRecordKeyAsc();

    default Page<WarframeCommunityReferenceMappingDTO> query(String keyword, Pageable pageable) {
        return findBy(CommunitySpecifications.contains(keyword, List.of("mappingScope", "sourceKey", "targetKey")),
            query -> query.as(WarframeCommunityReferenceMappingDTO.class).page(pageable));
    }
}
