package com.nnm.bot.warframe.persistent.official.service.dataset;

import java.util.function.Consumer;
import com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialRecordSource;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialRecordChangeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.OfficialFileDigestDTO;
import java.util.List;
import java.io.IOException;
import java.util.Set;
import java.util.Map;
import java.util.UUID;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants.ChangeType;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.OfficialFilteredChangesDTO;

/** 官方数据审批及分类事务边界。 */
public interface WarframeOfficialApprovalService {
    /**
     * 返回本次需要与数据库当前内容逐记录比对的官方文件路径；不能仅凭文件摘要跳过。
     *
     * @param files 文件集合。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    Set<String> comparePaths(List<OfficialFileDigestDTO> files);

    /**
     * 将本次冻结文件的候选记录分批写入审核暂存区。
     *
     * @param sourceRef 来源版本引用。
     * @param commitSha 提交哈希。
     * @param buildId 构造标识。
     * @param files 文件集合。
     */
    void stageCandidates(String sourceRef, String commitSha, String buildId, List<OfficialFileDigestDTO> files);

    /**
     * 对比当前数据并生成待采纳变更。
     *
     * @param incoming 本次输入。
     * @param paths 待处理文件的路径集合。
     * @param consumer 逐条接收处理结果的回调。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    void changes(WarframeOfficialRecordSource incoming, Set<String> paths,
        Consumer<WarframeOfficialRecordChangeDTO> consumer) throws IOException;

    /**
     * 在单独事务中采纳该文件下一个未完成的变更分类。
     *
     * @param jobId 文件工作项标识。
     * @param ownerToken 本次认领的所有者令牌。
     * @param incoming 文件记录来源，可重复按类别读取。
     * @param file 待入库文件摘要。
     * @param plan 待保留差异及文件版本计划。
     * @param approvedKeys 从冻结预览读出的各分类已批准业务键。
     * @return 该文件的全部分类是否已提交。
     * @throws IOException 读取候选记录失败时抛出，当前分类事务回滚。
     */
    boolean applyNextCategory(UUID jobId, UUID ownerToken, WarframeOfficialRecordSource incoming,
        OfficialFileDigestDTO file, OfficialFilteredChangesDTO plan, Map<ChangeType, Set<String>> approvedKeys)
        throws IOException;
}
