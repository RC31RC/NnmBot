package com.nnm.bot.warframe.persistent.official.service.record;

import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.world.WarframeOfficialRegionDTO;
import java.util.List;
import java.util.Collection;
import java.util.Optional;

/**
 * 提供已采纳官方 Nnm 静态资料的只读数据。
 */
public interface WarframeOfficialRecordReadService {

    /**
     * 读取当前已采纳的官方数据批次标识，尚无基线时返回空结果。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    Optional<String> currentBatchId();

    /**
     * 读取当前条件下的业务记录。
     *
     * @return 当前独立官方记录的摘要及类型化详情组成的列表。
     */
    List<WarframeOfficialRecordReadDTO> records();

    /**
     * 读取当前条件下的业务记录。
     *
     * @param domain 官方数据文件所属的业务领域。
     * @return 当前独立官方记录的摘要及类型化详情组成的列表。
     */
    List<WarframeOfficialRecordReadDTO> records(WarframeOfficialDataDomain domain);

    /**
     * 按已在数据库筛选出的本地主键装载完整记录树。
     *
     * @param domain 官方数据文件所属的业务领域。
     * @param recordIds 已筛选的主记录标识。
     * @return 所选主记录及其同源完整详情树。
     */
    List<WarframeOfficialRecordReadDTO> recordsByIds(
            WarframeOfficialDataDomain domain,
            Collection<Long> recordIds);

    /**
     * 读取当前业务身份对应的记录。
     *
     * @param domain 官方数据文件所属的业务领域。
     * @param bkey 记录的稳定业务键；子表中表示直接父记录的主键。
     * @return 当前独立官方记录的摘要及类型化详情；没有匹配数据时返回空结果。
     */
    Optional<WarframeOfficialRecordReadDTO> record(WarframeOfficialDataDomain domain, String bkey);

    /**
     * 读取已采纳的星系和节点资料，供引用解析与展示使用。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeOfficialRegionDTO> regions();
}
