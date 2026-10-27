package com.nnm.bot.warframe.persistent.official.service.dataset;

import java.util.function.Consumer;
import com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialRecordSource;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialRecordChangeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.OfficialFileDigestDTO;
import java.util.List;
import java.io.IOException;
import java.util.Set;

/** 官方数据审批及数据库基线边界。 */
public interface WarframeOfficialApprovalService {
    /**
     * 计算当前已采纳内容的稳定指纹，供审批基线核对。
     *
     * @return 当前内容的稳定校验摘要。
     */
    String fingerprint();

    /**
     * 比较冻结清单与当前基线，提取需要审核的官方文件路径。
     *
     * @param files 文件集合。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    Set<String> changedPaths(List<OfficialFileDigestDTO> files);

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
     * @throws IOException 读取输入、绑定字段或写入输出失败时抛出；由调用边界处理。
     */
    void apply(String batchId, WarframeOfficialRecordSource incoming, List<OfficialFileDigestDTO> files) throws IOException;
}
