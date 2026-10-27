package com.nnm.bot.warframe.persistent.official.mapper.dataset;

import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialVersionDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialPreviewDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot.WarframeOfficialRecordChangeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.snapshot.WarframeOfficialVersionVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.snapshot.WarframeOfficialPreviewVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.snapshot.WarframeOfficialRecordChangeVO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.snapshot.WarframeOfficialFieldChangeVO;

/** 将数据集传输快照转换为管理端展示合同，不在 Controller 暴露内部 DTO。 */
public final class WarframeOfficialImportViewMapper {
    /**
     * 禁止实例化，仅通过本类的静态方法使用此能力。
     */
    private WarframeOfficialImportViewMapper() {
    }

    /**
     * 将非受管官方版本数据转换为前端版本选项。
     * @param value 待处理的字段值。
     * @return 可供刷新选择的官方版本。
     */
    public static WarframeOfficialVersionVO version(WarframeOfficialVersionDTO value) {
        return value == null ? null : new WarframeOfficialVersionVO(value.ref(), value.commit());
    }

    /**
     * 生成变更预览，供后续确认采纳。
     *
     * @param value 待处理的字段值。
     * @return 冻结官方数据批次的变更预览。
     */
    public static WarframeOfficialPreviewVO preview(WarframeOfficialPreviewDTO value) {
        return new WarframeOfficialPreviewVO(value.id(), version(value.version()), value.buildId(), value.baselineId(),
                value.status(), value.expiresAt(), value.totalBytes(), value.addedRecords(), value.changedRecords(),
                value.removedRecords(), value.unmappedPaths(), value.files().stream()
                .map(file -> new WarframeOfficialPreviewVO.FileChangeVO(file.path(), file.sha256(), file.bytes(),
                        file.recordCount(), file.changeType())).toList());
    }

    /**
     * 对比当前数据并生成待采纳变更。
     *
     * @param page 从一开始的页码。
     * @return 包含当前页记录及总数的分页结果。
     */
    public static PageResult<WarframeOfficialRecordChangeVO> changes(PageResult<WarframeOfficialRecordChangeDTO> page) {
        return PageResult.of(page.records().stream().map(value -> new WarframeOfficialRecordChangeVO(
                value.path(), value.recordKey(), value.displayName(), value.changeType(), value.previousJson(),
                value.incomingJson(), value.previousLocalizedJson(), value.incomingLocalizedJson(),
                value.fieldChanges().stream().map(field -> new WarframeOfficialFieldChangeVO(
                        field.pointer(), field.previousJson(), field.incomingJson())).toList())).toList(),
                page.current(), page.size(), page.total());
    }
}
