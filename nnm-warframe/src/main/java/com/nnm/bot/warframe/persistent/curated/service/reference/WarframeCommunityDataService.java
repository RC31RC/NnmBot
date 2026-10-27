package com.nnm.bot.warframe.persistent.curated.service.reference;

import com.nnm.bot.warframe.persistent.curated.pojo.query.reference.WarframeCommunityQuery;
import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.reference.WarframeCommunityDatasetVO;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.reference.WarframeCommunityRecordVO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityPreviewDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityPreviewPageDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityApplyDTO;
import com.nnm.bot.warframe.persistent.curated.pojo.vo.reference.WarframeCommunityApplyVO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface WarframeCommunityDataService {
    /**
     * 列出当前支持的数据文件。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeCommunityDatasetVO> datasets();
    /**
     * 按查询条件筛选社区人工资料并返回分页结果。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param query 当前业务的查询条件。
     * @return 包含当前页记录及总数的分页结果。
     */
    PageResult<WarframeCommunityRecordVO> query(String dataset, WarframeCommunityQuery query);
    /**
     * 生成变更预览，供后续确认采纳。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param json 待绑定的 JSON 文本。
     * @return 游戏资料社区预览对象。
     */
    WarframeCommunityPreviewDTO preview(String dataset, String json);
    /**
     * 读取上传文件并生成人工资料的变更预览。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param file 本次上传或读取的文件。
     * @return 游戏资料社区预览对象。
     */
    WarframeCommunityPreviewDTO uploadPreview(String dataset, MultipartFile file);
    /**
     * 读取已配置的人工资料输入并生成变更预览。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @return 游戏资料社区预览对象。
     */
    WarframeCommunityPreviewDTO pullPreview(String dataset);
    /**
     * 读取已冻结预览中的一页变更。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param id 目标记录的本地主键或协议标识。
     * @param page 从一开始的页码。
     * @param size 当前操作允许的数据数量。
     * @return 游戏资料社区预览分页对象。
     */
    WarframeCommunityPreviewPageDTO previewPage(String dataset, String id, int page, int size);
    /**
     * 采纳已经确认的业务变更。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param request 本次新增、修改或执行操作使用的已接收参数。
     * @return 本次社区资料采纳结果及变更数量。
     */
    WarframeCommunityApplyVO apply(String dataset, WarframeCommunityApplyDTO request);
    /**
     * 校验权限后删除社区人工资料，并维护关联资源。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param id 目标记录的本地主键。
     */
    void delete(String dataset, long id);
    /**
     * 取得或更新当前业务使用的数据来源。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param url 本次外部请求或资源的地址。
     */
    void source(String dataset, String url);
}
