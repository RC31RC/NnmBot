package com.nnm.bot.warframe.persistent.official.service.dataset;

import java.util.function.Consumer;
import com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialRecordSource;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialRecordChangeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.OfficialFileDigestDTO;
import java.util.List;
import java.io.IOException;
import java.util.Set;
import java.util.Map;

/** 官方数据审批及数据库基线边界。 */
public interface WarframeOfficialApprovalService {
    /**
     * 计算当前已采纳内容的稳定指纹，供审批基线核对。
     *
     * @return 当前内容的稳定校验摘要。
     */
    String fingerprint();

    /**
     * 计算指定官方文件的审批指纹；单文件任务不依赖无关文件的变更。
     *
     * @param paths 审核的文件路径。
     * @return 对应文件的基线指纹。
     */
    String fingerprint(Set<String> paths);

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
     * 采纳已经确认的业务变更。
     *
     * @param batchId 当前冻结导入批次的标识。
     * @param incoming 本次输入。
     * @param files 文件集合。
     * @param selectedIdentities 本次选中采纳的记录身份。
     * @param selectedPaths 本次选中记录所属的官方文件路径。
     * @param remainingPaths 当前仍有待审核差异的官方文件路径。
     * @param remainingChangeObjectKey 过滤后剩余差异对象键。
     * @param remainingCounts 过滤后剩余差异计数。
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    void apply(String batchId, WarframeOfficialRecordSource incoming, List<OfficialFileDigestDTO> files,
        Set<String> selectedIdentities, Set<String> selectedPaths, Set<String> remainingPaths,
        String remainingChangeObjectKey, Map<String, Integer> remainingCounts) throws IOException;
}
