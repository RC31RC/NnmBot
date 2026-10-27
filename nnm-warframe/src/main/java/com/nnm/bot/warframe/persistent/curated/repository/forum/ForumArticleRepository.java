package com.nnm.bot.warframe.persistent.curated.repository.forum;

import com.nnm.bot.warframe.persistent.curated.pojo.entity.forum.ForumArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.forum.ForumArticleDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.query.forum.ForumArticleQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
import java.util.Optional;

public interface ForumArticleRepository
        extends JpaRepository<ForumArticle, Long>,
        JpaSpecificationExecutor<ForumArticle> {

    /**
     * 判断是否已有文章使用该访问标识，比较时忽略大小写。
     *
     * @param slug 文章对外访问使用的唯一标识。
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    boolean existsBySlugIgnoreCase(String slug);

    /**
     * 排除当前文章后，判断访问标识是否已被其他文章使用。
     *
     * @param slug 文章对外访问使用的唯一标识。
     * @param id 目标记录的本地主键。
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    boolean existsBySlugIgnoreCaseAndIdNot(String slug, Long id);

    /**
     * 读取指定文章以外的文章投影。
     *
     * @param id 目标记录的本地主键。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<ForumArticleDTO> findAllProjectedByIdNot(Long id);

    /**
     * 读取全部启用记录的非受管投影。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<ForumArticleDTO> findAllProjectedByEnabledTrue();

    /**
     * 按主键倒序读取最新一条记录的非受管投影。
     *
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    Optional<ForumArticleDTO> findFirstProjectedByOrderByIdDesc();
    /**
     * 读取全部记录的非受管投影。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<ForumArticleDTO> findAllProjectedBy();

    /**
     * 按本地主键读取单条非受管投影。
     *
     * @param id 目标记录的本地主键。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    Optional<ForumArticleDTO> findProjectedById(long id);

    /**
     * 根据文章筛选规格读取非受管文章投影分页。
     *
     * @param request 本次查询的筛选与分页参数。
     * @param pageable 分页大小、页码和排序条件。
     * @return 包含当前页记录及总数的分页结果。
     */
    default Page<ForumArticleDTO> query(ForumArticleQuery request, Pageable pageable) {
        return findBy(ForumArticleSpecifications.specification(request), query -> query.as(ForumArticleDTO.class).page(pageable));
    }
}
